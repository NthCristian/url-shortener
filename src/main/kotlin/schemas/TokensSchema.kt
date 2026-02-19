package io.nthcristian.schemas

import io.nthcristian.isValidUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedToken(val url: String)

object TokenService {
    object Tokens : Table() {
        val id = integer("id").autoIncrement()
        val url = varchar("url", 200)
        val created_at = date("created_at")

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction {
            SchemaUtils.create(Tokens)
        }
    }

    suspend fun create(newUrl: String): Int = dbQuery {
        if (!isValidUrl(newUrl)) {
            throw IllegalArgumentException("Invalid URL")
        }

        Tokens.insert {
            it[url] = newUrl
            it[created_at] = LocalDate.parse(java.time.LocalDate.now().toString())
        }[Tokens.id]
    }

    suspend fun read(id: Int): ExposedToken? = dbQuery {
        Tokens.selectAll()
            .where { Tokens.id eq id }
            .map { ExposedToken(it[Tokens.url]) }
            .singleOrNull()
    }


    suspend fun delete(id: Int) {
        dbQuery {
            Tokens.deleteWhere { Tokens.id eq id }
        }
    }


    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}