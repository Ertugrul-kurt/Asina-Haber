
package com.example.asinahaberuygulamasi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_news")
data class SavedNews(
    @PrimaryKey(autoGenerate = true)
    val roomId: Int = 0,
    val newsId: Int,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val category: String,
    val source: String,
    val date: String,
    val savedAt: Long = System.currentTimeMillis()
)