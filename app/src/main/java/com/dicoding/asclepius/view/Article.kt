package com.dicoding.asclepius.view

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.data.NewsApi
import com.dicoding.asclepius.data.NewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Article(private val recyclerView: RecyclerView, private val context: Context, private val viewAdapter: NewsAdapter) {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://newsapi.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val newsApi = retrofit.create(NewsApi::class.java)

    fun getNews() {
        newsApi.getNews().enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                val newsResponse = response.body()
                if (newsResponse != null) {
                    val newsArticles = newsResponse.articles
                    viewAdapter.updateData(newsArticles)
                    (context as Activity).runOnUiThread {
                        recyclerView.adapter = viewAdapter
                    }
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Toast.makeText(context, "Failed to fetch news: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
