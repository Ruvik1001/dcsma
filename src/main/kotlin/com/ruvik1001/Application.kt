import com.ruvik1001.TokenTable
import com.ruvik1001.UserTable
import com.ruvik1001.login.configureLoginRouting
import com.ruvik1001.plugins.*
import com.ruvik1001.register.configureRegisterRouting
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    val config = environment.config.config("database")
    val hikariConfig = HikariConfig().apply {
        driverClassName = "org.h2.Driver"
        jdbcUrl = "jdbc:h2:file:./db;DB_CLOSE_DELAY=-1"
        username = "sa"
        password = ""
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    }
    val dataSource = HikariDataSource(hikariConfig)
    Database.connect(dataSource)

    transaction {
        SchemaUtils.createMissingTablesAndColumns(UserTable, TokenTable)
    }

    configureSerialization()
    configureRouting()
    configureLoginRouting()
    configureRegisterRouting()
}
