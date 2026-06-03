package com.example.asinahaberuygulamasi.model

data class Comment(
    val id: String = "",
    val newsId: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

