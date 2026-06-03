
package com.example.asinahaberuygulamasi.model

import com.google.gson.annotations.SerializedName

data class NewsItem(
    @SerializedName("newsId")
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val category: String = "",
    val source: String = "",
    val date: String = ""
)