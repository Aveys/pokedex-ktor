package com.aveys.features.pokemons.domain

import com.aveys.features.pokemons.data.Pokemon
import com.aveys.utils.dbQuery

class PokemonService(
    private val pokeApiService: PokeApiService,
) {
    suspend fun getPokemon(id: Int) =
        dbQuery {
            val pokemon = Pokemon.findById(id)
            if (pokemon == null) {
                val apiPokemon = pokeApiService.getPokemon(id)
                Pokemon.new(id) {
                    name = apiPokemon.name
                    cries = apiPokemon.cries.latest
                    sprite = apiPokemon.sprites.frontDefault
                }
            } else {
                pokemon
            }
        }
}
