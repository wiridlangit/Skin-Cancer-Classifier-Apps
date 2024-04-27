package com.dicoding.asclepius.data

import retrofit2.Call
import retrofit2.http.GET

interface NewsApi {
    @GET("v2/top-headlines?q=cancer&category=health&language=en&apiKey=77203d03338942bd8d3fcfe477f8bb00")
    fun getNews(): Call<NewsResponse>
}

