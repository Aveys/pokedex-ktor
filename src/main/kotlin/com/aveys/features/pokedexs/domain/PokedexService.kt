package com.aveys.features.pokedexs.domain

import com.aveys.features.pokedexs.data.Pokedex
import com.aveys.features.pokedexs.data.Pokedexs
import com.aveys.features.pokemons.domain.PokemonService
import com.aveys.features.pokemons.domain.dto.PokemonDTO
import com.aveys.features.users.domain.UserService
import com.aveys.features.users.domain.exceptions.UserNotFound
import com.aveys.utils.dbQuery
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class PokedexService(
    private val pokemonService: PokemonService,
    private val userService: UserService,
) {
    suspend fun addPokemon(
        id: Int,
        username: String,
    ) {
        val user = userService.getUserByName(username) ?: throw UserNotFound("User with name $username not found")
        val pokemon = pokemonService.getPokemon(id)

        dbQuery {
            val pokedex = Pokedex.find { Pokedexs.user eq user.id }.first()
            val newSet =
                pokedex.pokemon
                    .toMutableSet()
                    .also { it.add(pokemon) }
            pokedex.pokemon = SizedCollection(newSet)
        }
    }

    suspend fun deletePokemon(
        id: Int,
        username: String,
    ) {
        val user = userService.getUserByName(username) ?: throw UserNotFound("User with name $username not found")
        dbQuery {
            Pokedex.findSingleByAndUpdate(Pokedexs.user eq user.id) {
                it.pokemon = SizedCollection(it.pokemon.toMutableSet().also { set -> set.removeIf { pkmn -> pkmn.id.value == id } })
            }
        }
    }

    suspend fun getPokedex(username: String): Set<PokemonDTO> {
        val user = userService.getUserByName(username) ?: throw UserNotFound("User with name $username not found")
        return dbQuery {
            Pokedex
                .find(Pokedexs.user eq user.id)
                .first()
                .pokemon
                .map { it.toDto() }
                .toSet()
        }
    }
}
