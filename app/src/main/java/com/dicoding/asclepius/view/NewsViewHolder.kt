package com.dicoding.asclepius.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.Article

class NewsViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_news, parent, false)) {
    private val titleView: TextView = itemView.findViewById(R.id.article_title)
    private val descriptionView: TextView = itemView.findViewById(R.id.article_description)
    private val imageView: ImageView = itemView.findViewById(R.id.article_image)

    fun bind(article: Article) {
        titleView.text = article.title
        descriptionView.text = article.description
        Glide.with(itemView.context).load(article.urlToImage).into(imageView)
    }
}
