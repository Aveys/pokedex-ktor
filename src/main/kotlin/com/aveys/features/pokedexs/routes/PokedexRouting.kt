package com.aveys.features.pokedexs.routes

import com.aveys.features.pokedexs.domain.PokedexService
import com.aveys.utils.user
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail
import org.koin.ktor.ext.inject

fun Application.pokedexRoutes() {
    val pokedexService by inject<PokedexService>()
    routing {
        authenticate("auth-session") {
            route("/pokedex") {
                get {
                    val user = call.user()
                    val pokedex = pokedexService.getPokedex(user.name)
                    call.respond(pokedex)
                }
                route("/{id}") {
                    post {
                        val user = call.user()
                        val id = call.parameters.getOrFail("id").toInt()
                        pokedexService.addPokemon(id, user.name)
                        call.respond(HttpStatusCode.OK)
                    }
                    delete {
                        val user = call.user()
                        val id = call.parameters.getOrFail("id").toInt()
                        pokedexService.deletePokemon(id, user.name)
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        }
    }
}
