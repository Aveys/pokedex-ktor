package com.aveys.features.templating

import com.aveys.features.pokedexs.domain.PokedexService
import com.aveys.utils.user
import com.github.mustachejava.DefaultMustacheFactory
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.mustache.Mustache
import io.ktor.server.mustache.MustacheContent
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureTemplating() {
    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates")
    }
    val pokedexService by inject<PokedexService>()
    routing {
        authenticate("auth-session") {
            get("/") {
                val user = call.user()
                val pokedex = pokedexService.getPokedex(user.name)
                call.respond(MustacheContent("index.hbs", mapOf("pokedex" to pokedex, "user" to user.name)))
            }
        }
        get("/login") {
            call.respond(MustacheContent("login.hbs", null))
        }
    }
}
