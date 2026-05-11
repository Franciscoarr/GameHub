package com.example.gamehub.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamehub.model.*
import com.example.gamehub.network.RetrofitInstance
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val dao = db.gameDao()
    private val dataStoreManager = DataStoreManager(application)

    // DataStore Settings
    val userSettings: StateFlow<UserSettings> = dataStoreManager.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings("Invitado", AppTheme.SYSTEM))

    fun updateSettings(username: String, theme: AppTheme) {
        viewModelScope.launch {
            dataStoreManager.saveSettings(UserSettings(username, theme))
        }
    }

    // API Data
    private val _apiGames = MutableStateFlow<List<IGDBGame>>(emptyList())
    val apiGames: StateFlow<List<IGDBGame>> = _apiGames.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchGames(clientId: String, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Query simple para obtener juegos con portadas y géneros
                val body = "fields name,summary,storyline,rating,cover.url,genres.name; limit 20;"
                val result = RetrofitInstance.api.getGames(clientId, "Bearer $token", body)
                _apiGames.value = result
            } catch (e: Exception) {
                // Manejar error
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Local DB (Favorites)
    val favoriteGames: StateFlow<List<FavoriteGame>> = dao.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun isFavorite(id: Long): Flow<Boolean> = dao.isFavorite(id)

    fun addFavorite(game: IGDBGame) {
        viewModelScope.launch {
            val fav = FavoriteGame(
                id = game.id,
                name = game.name,
                summary = game.summary ?: "Sin descripción",
                imageUrl = game.cover?.url?.replace("t_thumb", "t_cover_big")?.let { "https:$it" } ?: "",
                rating = game.rating ?: 0.0,
                genres = game.genres?.joinToString(", ") { it.name } ?: "General"
            )
            dao.insertFavorite(fav)
        }
    }

    fun removeFavorite(game: FavoriteGame) {
        viewModelScope.launch {
            dao.deleteFavorite(game)
        }
    }

    // Comments
    fun getComments(gameId: Long): Flow<List<Comment>> = dao.getCommentsForGame(gameId)

    fun addComment(gameId: Long, content: String) {
        viewModelScope.launch {
            val username = userSettings.value.username
            dao.insertComment(Comment(gameId = gameId, userName = username, content = content))
        }
    }
}
