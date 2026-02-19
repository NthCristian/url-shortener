package io.nthcristian

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.nthcristian.schemas.TokenService

fun Application.configureRouting() {
    routing {
        post("/create") {
            val url = call.receive<String>()

            val newCode = try {
                TokenService.create(url).toBase62()
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid URL")
                return@post
            }

            call.respond(HttpStatusCode.Created, newCode)
        }

        get("/{code}") {
            val code = call.pathParameters["code"]
            val id = code?.fromBase62()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val token = TokenService.read(id)

            if (token == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respondRedirect(token.url, false)
        }

        delete("/{code}") {
            val passwd = call.request.headers["X-Admin-Key"]

            val adminPasswd = environment.config
                .property("auth.passwd")
                .getString()

            if (passwd != adminPasswd) {
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }

            val code = call.pathParameters["code"]
            val id = code?.fromBase62()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            TokenService.delete(id)

            call.respond(HttpStatusCode.OK)
        }
    }
}
