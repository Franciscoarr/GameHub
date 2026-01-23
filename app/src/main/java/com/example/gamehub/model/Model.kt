package com.example.gamehub.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.gamehub.R

data class Game(
    val id: Int,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @StringRes val genreRes: Int,
    @DrawableRes val imageRes: Int,
    val isFavorite: Boolean = false,
    val rating: Double = 4.5,
    @StringRes val commentsRes: List<Int> = emptyList() //Lista de IDs
)

//Datos de prueba
val mockGames = listOf(
    Game(
        id = 1,
        titleRes = R.string.game1_title,
        descriptionRes = R.string.game1_desc,
        genreRes = R.string.genre_shooter,
        imageRes = R.drawable.pvzgw2,
        isFavorite = true,
        rating = 4.7,
        commentsRes = listOf(R.string.game1_c1, R.string.game1_c2)
    ),
    Game(
        id = 2,
        titleRes = R.string.game2_title,
        descriptionRes = R.string.game2_desc,
        genreRes = R.string.genre_rpg,
        imageRes = R.drawable.yakuza0,
        isFavorite = true,
        rating = 4.9,
        commentsRes = listOf(R.string.game2_c1, R.string.game2_c2)
    ),
    Game(
        id = 3,
        titleRes = R.string.game3_title,
        descriptionRes = R.string.game3_desc,
        genreRes = R.string.genre_sandbox,
        imageRes = R.drawable.terraria,
        isFavorite = false,
        rating = 4.8,
        commentsRes = listOf(R.string.game3_c1, R.string.game3_c2)
    ),
    Game(
        id = 4,
        titleRes = R.string.game4_title,
        descriptionRes = R.string.game4_desc,
        genreRes = R.string.genre_roguelike,
        imageRes = R.drawable.balatro,
        isFavorite = true,
        rating = 5.0,
        commentsRes = listOf(R.string.game4_c1, R.string.game4_c2)
    ),
    Game(
        id = 5,
        titleRes = R.string.game5_title,
        descriptionRes = R.string.game5_desc,
        genreRes = R.string.genre_roguelike,
        imageRes = R.drawable.isaac,
        isFavorite = false,
        rating = 4.6,
        commentsRes = listOf(R.string.game5_c1, R.string.game5_c2)
    ),
    Game(
        id = 6,
        titleRes = R.string.game6_title,
        descriptionRes = R.string.game6_desc,
        genreRes = R.string.genre_strategy,
        imageRes = R.drawable.tropico5,
        isFavorite = true,
        rating = 4.4,
        commentsRes = listOf(R.string.game6_c1, R.string.game6_c2, R.string.game6_c3)
    )
)