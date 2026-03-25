package com.sportstechbrands.sportstechlive.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Production: Render.com hosted backend
    // Replace this URL with your actual Render deployment URL
    private const val BASE_URL = "https://sportstechlive-backend.onrender.com/"

    private var tokenProvider: (() -> String?)? = null

    fun init(provider: () -> String?) {
        tokenProvider = provider
    }

    private val httpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE
        }
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = tokenProvider?.invoke()
                val req = if (token != null) {
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else chain.request()
                chain.proceed(req)
            }
            .addInterceptor(logging)
            .build()
    }

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
