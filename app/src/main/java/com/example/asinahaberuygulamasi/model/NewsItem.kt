package com.example.asinahaberuygulamasi.model

data class NewsItem(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val category: String,
    val source: String
)