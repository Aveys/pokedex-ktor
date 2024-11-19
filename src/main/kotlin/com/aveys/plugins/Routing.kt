package com.aveys.plugins

import com.aveys.features.users.domain.exceptions.UserNotFound
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            cause.printStackTrace()
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
        exception<UserNotFound> { call, _ ->
            call.respondText(text = "404", status = HttpStatusCode.NotFound)
        }
    }
}
