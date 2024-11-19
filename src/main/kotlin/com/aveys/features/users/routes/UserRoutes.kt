package com.aveys.features.users.routes

import com.aveys.features.users.domain.UserService
import com.aveys.features.users.domain.dto.UserSignInDTO
import com.aveys.plugins.UserSession
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import org.koin.ktor.ext.inject

fun Application.userRoutes() {
    val userService by inject<UserService>()
    routing {
        authenticate("auth-form") {
            post("/login") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.sessions.set(UserSession(principal.name, 1))
                call.respondRedirect("/")
            }
        }
        post("/signin") {
            val body = call.receive<UserSignInDTO>()
            userService.addUser(body)
            call.respond(HttpStatusCode.OK)
        }
    }
}
