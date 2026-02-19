package io.nthcristian

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase() {
    Database.connect(
        url = "jdbc:sqlite:ushort.db",
        driver = "org.sqlite.JDBC",
    )
}
