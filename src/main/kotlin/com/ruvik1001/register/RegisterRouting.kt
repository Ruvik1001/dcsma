package com.ruvik1001.register

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

fun Application.configureRegisterRouting() {
    routing {
        post("/register") {
            val receive = call.receive<RegisterReceiveRemote>()
            if (!isValidEmail(receive.email)) {
                call.respond(HttpStatusCode.BadRequest, "Email is not valid")
                return@post
            }

            val userExists = newSuspendedTransaction {
                UserTable.select { UserTable.email eq receive.email }.count() > 0
            }

            if (userExists) {
                call.respond(HttpStatusCode.Conflict, "User already exists")
                return@post
            }

            val hashedPassword = hashPassword(receive.password)

            newSuspendedTransaction {
                UserTable.insert {
                    it[name] = receive.login
                    it[email] = receive.email
                    it[password] = hashedPassword
                }
            }

            val generatedToken = UUID.randomUUID().toString()

            newSuspendedTransaction {
                TokenTable.insert {
                    it[login] = receive.login
                    it[token] = generatedToken
                }
            }

            call.respond(RegisterResponseRemote(token = generatedToken))
        }
    }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    return email.matches(emailRegex.toRegex())
}

