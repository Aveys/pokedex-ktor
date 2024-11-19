package com.aveys.features.pokemons.routes

import com.aveys.features.pokemons.domain.PokeApiService
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail
import org.koin.ktor.ext.inject

fun Application.pokemonRoutes() {
    val pokeService by inject<PokeApiService>()
    routing {
        route("/pokemon") {
            get("/random") {
                call.respond(pokeService.getRandomPokemon())
            }
            get("/{id}") {
                val id = call.parameters.getOrFail("id").toInt()
                call.respond(pokeService.getPokemon(id))
            }
        }
    }
}
