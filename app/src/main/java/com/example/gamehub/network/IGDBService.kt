package com.example.gamehub.network

import com.example.gamehub.model.IGDBGame
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IGDBService {
    @POST("games")
    suspend fun getGames(
        @Header("Client-ID") clientId: String,
        @Header("Authorization") authorization: String,
        @Body body: RequestBody
    ): List<IGDBGame>
}

object RetrofitInstance {
    private const val BASE_URL = "https://api.igdb.com/v4/"

    private val client = okhttp3.OkHttpClient.Builder().build()

    val api: IGDBService by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(IGDBService::class.java)
    }
}