package com.example.asinahaberuygulamasi.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedNewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun haberKaydet(savedNews: SavedNews)

    @Delete
    suspend fun haberSil(savedNews: SavedNews)

    @Query("DELETE FROM saved_news WHERE newsId = :newsId")
    suspend fun haberIdileSil(newsId: Int)

    @Query("SELECT * FROM saved_news ORDER BY savedAt DESC")
    fun tumKayitlariGetir(): Flow<List<SavedNews>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_news WHERE newsId = :newsId)")
    fun haberKayitliMi(newsId: Int): Flow<Boolean>
}

