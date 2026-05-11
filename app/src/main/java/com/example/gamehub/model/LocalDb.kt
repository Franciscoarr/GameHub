package com.example.gamehub.model

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM favorite_games")
    fun getAllFavorites(): Flow<List<FavoriteGame>>

    @Query("SELECT * FROM favorite_games WHERE id = :id")
    suspend fun getFavoriteById(id: Long): FavoriteGame?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(game: FavoriteGame)

    @Delete
    suspend fun deleteFavorite(game: FavoriteGame)

    @Query("SELECT EXISTS(SELECT * FROM favorite_games WHERE id = :id)")
    fun isFavorite(id: Long): Flow<Boolean>

    // Comments
    @Query("SELECT * FROM comments WHERE gameId = :gameId ORDER BY timestamp DESC")
    fun getCommentsForGame(gameId: Long): Flow<List<Comment>>

    @Insert
    suspend fun insertComment(comment: Comment)
}

@Database(entities = [FavoriteGame::class, Comment::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gamehub_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
