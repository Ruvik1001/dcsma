package com.ruvik1001

import com.ruvik1001.login.configureLoginRouting
import com.ruvik1001.plugins.*
import com.ruvik1001.register.configureRegisterRouting
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
//    install(ContentNegotiation) {
//        json()
//    }
//
//    val config = environment.config.config("database")
//    val hikariConfig = HikariConfig().apply {
//        driverClassName = config.property("driver").getString()
//        jdbcUrl = config.property("url").getString()
//        username = config.property("user").getString()
//        password = config.property("password").getString()
//        maximumPoolSize = 3
//        isAutoCommit = false
//        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
//    }
//    val dataSource = HikariDataSource(hikariConfig)
//    Database.connect(dataSource)
//
//    transaction {
//        SchemaUtils.create(Users)
//    }


    configureSerialization()
    configureRouting()
    configureLoginRouting()
    configureRegisterRouting()
}
