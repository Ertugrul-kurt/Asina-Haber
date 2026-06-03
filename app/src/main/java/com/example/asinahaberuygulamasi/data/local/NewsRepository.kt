package com.example.asinahaberuygulamasi.data.local

import kotlinx.coroutines.flow.Flow

class NewsRepository(private val dao: SavedNewsDao) {

    val tumKayitliHaberler: Flow<List<SavedNews>> = dao.tumKayitlariGetir()

    suspend fun kaydet(savedNews: SavedNews) = dao.haberKaydet(savedNews)

    suspend fun sil(newsId: Int) = dao.haberIdileSil(newsId)

    fun haberKayitliMi(newsId: Int): Flow<Boolean> = dao.haberKayitliMi(newsId)
}

