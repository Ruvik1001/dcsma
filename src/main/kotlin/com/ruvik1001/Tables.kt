package com.ruvik1001

import org.jetbrains.exposed.sql.Table
import java.security.MessageDigest

object UserTable: Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    val email = varchar("email", 50)
    val password = varchar("password", 64)
    override val primaryKey = PrimaryKey(id)
}

object TokenTable: Table() {
    val id = integer("id").autoIncrement()
    val login = varchar("login", 50)
    val token = varchar("token", 50)
    override val primaryKey = PrimaryKey(id)
}



fun hashPassword(password: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val hashedBytes = md.digest(password.toByteArray())
    return hashedBytes.joinToString("") { "%02x".format(it) }
}