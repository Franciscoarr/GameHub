package com.example.gamehub.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class IGDBGame(
    val id: Long,
    val name: String,
    val summary: String?,
    val cover: IGDBCover?,
    val genres: List<IGDBGenre>?,
    val rating: Double?,
    val storyline: String?
)

data class IGDBCover(
    val id: Long,
    val url: String
)

data class IGDBGenre(
    val id: Long,
    val name: String
)

@Entity(tableName = "favorite_games")
data class FavoriteGame(
    @PrimaryKey val id: Long,
    val name: String,
    val summary: String,
    val imageUrl: String,
    val rating: Double,
    val genres: String
)

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gameId: Long,
    val userName: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

data class UserSettings(
    val username: String,
    val theme: AppTheme
)
