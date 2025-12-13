package com.example.gamehub.model

import androidx.annotation.DrawableRes
import com.example.gamehub.R

data class Game(
    val id: Int,
    val title: String,
    val description: String,
    val genre: String,
    @DrawableRes val imageRes: Int,
    val isFavorite: Boolean = false,
    val rating: Double = 4.5,
    val comments: List<String> = emptyList()
)

// Datos de prueba
val mockGames = listOf(
    Game(
        id = 1,
        title = "Plants vs. Zombies: Garden Warfare 2",
        description = "La batalla por Suburbia crece. Un shooter caótico, divertido y lleno de acción vegetal",
        genre = "Shooter",
        imageRes = R.drawable.pvzgw2,
        isFavorite = true,
        rating = 4.7,
        comments = listOf("El mejor shooter para relajarse", "Lanzaguisantes OP")
    ),
    Game(
        id = 2,
        title = "Yakuza 0",
        description = "Japón, 1988. Kiryu y Majima luchan por el control en una historia de crimen, dinero y karaoke",
        genre = "Action RPG",
        imageRes = R.drawable.yakuza0,
        isFavorite = true,
        rating = 4.9,
        comments = listOf("La historia es increíble", "Majima es el mejor personaje")
    ),
    Game(
        id = 3,
        title = "Terraria",
        description = "Excava, lucha, explora, construye. El mundo está en tus manos en esta aventura sandbox",
        genre = "Sandbox",
        imageRes = R.drawable.terraria,
        isFavorite = false,
        rating = 4.8,
        comments = listOf("Horas infinitas de juego", "Cuidado con el Muro de Carne")
    ),
    Game(
        id = 4,
        title = "Balatro",
        description = "Un roguelike de construcción de mazos basado en el póker. Hipnótico, adictivo y genial",
        genre = "Roguelike",
        imageRes = R.drawable.balatro,
        isFavorite = true,
        rating = 5.0,
        comments = listOf("No puedo dejar de jugar", "Solo una ronda más...")
    ),
    Game(
        id = 5,
        title = "The Binding of Isaac",
        description = "Un shooter RPG de acción generado aleatoriamente con elementos roguelike muy oscuros",
        genre = "Roguelike",
        imageRes = R.drawable.isaac,
        isFavorite = false,
        rating = 4.6,
        comments = listOf("Muy difícil pero gratificante", "Items infinitos")
    )
)