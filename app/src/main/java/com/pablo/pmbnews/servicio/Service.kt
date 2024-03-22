package com.pablo.pmbnews.servicio

import com.pablo.pmbnews.Extensions.Companion.API_KEY
import com.pablo.pmbnews.bbdd.News
import com.pablo.pmbnews.bbdd.NewsResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface Service {

    @GET("everything")
    suspend fun getAllNews(@Query("q") q: String, @Query("page") page: Int, @Query("apiKey") apiKey: String): Response<NewsResponse>

    companion object {
        private const val BASE_URL = "https://newsapi.org/v2/"

        fun create(): Service {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Service::class.java)
        }
    }
}
