package com.example.gamehub.network

import com.example.gamehub.model.IGDBGame
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IGDBService {
    @POST("games")
    suspend fun getGames(
        @Header("Client-ID") clientId: String,
        @Header("Authorization") authorization: String,
        @Body body: String
    ): List<IGDBGame>
}

object RetrofitInstance {
    private const val BASE_URL = "https://api.igdb.com/v4/"
    
    val api: IGDBService by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(IGDBService::class.java)
    }
}
