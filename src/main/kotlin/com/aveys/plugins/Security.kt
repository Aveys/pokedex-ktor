package com.aveys.plugins

import com.aveys.features.users.data.User
import com.aveys.features.users.data.Users
import com.aveys.utils.dbQuery
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authentication
import io.ktor.server.auth.form
import io.ktor.server.auth.session
import io.ktor.server.response.respondRedirect
import io.ktor.server.sessions.SessionStorageMemory
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.util.getDigestFunction
import kotlinx.serialization.Serializable
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class UserSession(
    val name: String,
    val count: Int,
)

@OptIn(ExperimentalEncodingApi::class)
fun encodePassword(pass: String): String {
    val bytes = digestFunction(pass)
    return Base64.encode(bytes)
}

val digestFunction = getDigestFunction("SHA-256") { "ktor${it.length}" }

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<UserSession>("user_session", SessionStorageMemory()) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 3600
        }
    }

    authentication {
        session<UserSession>("auth-session") {
            validate { session ->
                dbQuery {
                    val user = User.find { Users.username eq session.name }.firstOrNull()
                    if (user != null) {
                        session
                    } else {
                        null
                    }
                }
            }
            challenge {
                call.respondRedirect("/login")
            }
        }

        form(name = "auth-form") {
            userParamName = "username"
            passwordParamName = "password"
            validate { credentials ->
                dbQuery {
                    val user = User.find { Users.username eq credentials.name }.firstOrNull()
                    if (user != null && user.password == encodePassword(credentials.password)) {
                        UserIdPrincipal(credentials.name)
                    } else {
                        null
                    }
                }
            }
            challenge {
                call.respondRedirect("/login")
            }
        }
    }
}
