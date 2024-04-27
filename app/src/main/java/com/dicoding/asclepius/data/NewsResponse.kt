package com.dicoding.asclepius.data

data class Article(
    val title: String,
    val description: String,
    val urlToImage: String
)

data class NewsResponse(
    val articles: List<Article>
)
