package com.dicoding.asclepius.data

import retrofit2.Call
import retrofit2.http.GET

interface NewsApi {
    @GET("v2/top-headlines?q=cancer&category=health&language=en&apiKey={YOUR_API_TOKEN}")
    fun getNews(): Call<NewsResponse>
}

