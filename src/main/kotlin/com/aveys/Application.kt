package com.aveys

import com.aveys.features.pokedexs.data.Pokedex
import com.aveys.features.pokedexs.data.PokedexPokemons
import com.aveys.features.pokedexs.data.Pokedexs
import com.aveys.features.pokedexs.routes.pokedexRoutes
import com.aveys.features.pokemons.data.Pokemons
import com.aveys.features.pokemons.routes.pokemonRoutes
import com.aveys.features.templating.configureTemplating
import com.aveys.features.users.data.User
import com.aveys.features.users.data.Users
import com.aveys.features.users.routes.userRoutes
import com.aveys.plugins.*
import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

fun main(args: Array<String>): Unit =
    io.ktor.server.cio.EngineMain
        .main(args)

fun Application.module() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/postgres",
        driver = "org.postgresql.Driver",
        user = "root",
        password = "secret",
    )
    transaction {
        SchemaUtils.createMissingTablesAndColumns(Users, Pokemons, Pokedexs, PokedexPokemons)
        val defaultUser = User.find { Users.username eq "arthur" }.firstOrNull()
        if (defaultUser == null) {
            val usercreated =
                User.new {
                    username = "arthur"
                    password = encodePassword("demo")
                }
            Pokedex.new {
                user = usercreated
                createdAt = Instant.now()
            }
        }
    }
    configureSecurity()
    configureTemplating()
    configureMonitoring()
    configureRouting()
    configureKoin()
    configureSerialization()
    userRoutes()
    pokemonRoutes()
    pokedexRoutes()
}
