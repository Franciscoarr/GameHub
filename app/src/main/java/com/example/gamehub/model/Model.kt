package com.example.gamehub.model

import androidx.compose.ui.graphics.Color
import com.example.gamehub.ui.theme.FavoriteGH

data class Game(
    val id: Int,
    val title: String,
    val description: String,
    val genre: String,
    val isFavorite: Boolean = false,
    val rating: Double = 4.5,
    val comments: List<String> = emptyList()
)

// Datos de prueba
val mockGames = listOf(
    Game(1, "Cyber Odyssey", "Un RPG futurista en una ciudad de neón.", "RPG", true, 4.8),
    Game(2, "Space Racer", "Carreras de alta velocidad en anillos de saturno.", "Racing", false, 4.2),
    Game(3, "Fantasy Quest", "Aventura épica de dragones y mazmorras.", "Adventure", true, 4.9, listOf("Juegazo!", "Me encanta el dragón")),
    Game(4, "Zombie Survival", "Sobrevive a la horda.", "Action", false, 3.8),
    Game(5, "Puzzle Master", "Resuelve acertijos cuánticos.", "Puzzle", false, 4.5)
)