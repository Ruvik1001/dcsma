package com.ruvik1001.login

import com.ruvik1001.TokenTable
import com.ruvik1001.UserTable
import com.ruvik1001.hashPassword
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID


fun Application.configureLoginRouting() {
    routing {
        post("/login") {
            val receive = call.receive<LoginReceiveRemote>()

            val user = newSuspendedTransaction {
                UserTable.select { UserTable.name eq receive.login }
                    .map { it[UserTable.name] to it[UserTable.password] }
                    .singleOrNull()
            }

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "User not found")
                return@post
            }

            val (email, storedHashedPassword) = user
            val receivedHashedPassword = hashPassword(receive.password)

            if (storedHashedPassword == receivedHashedPassword) {
                val generatedToken = UUID.randomUUID().toString()
                newSuspendedTransaction {
                    TokenTable.insert {
                        it[login] = receive.login
                        it[token] = generatedToken
                    }
                }
                call.respond(LoginResponseRemote(token = generatedToken))
            }
            else
                call.respond(HttpStatusCode.BadRequest)
        }
    }
}
