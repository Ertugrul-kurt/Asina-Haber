package com.example.asinahaberuygulamasi.model

data class Haber(
    val _id: String = "",
    val baslik: String,
    val ozet: String,
    val kategori: String,
    val tarih: String = ""
)

data class HaberlerResponse(
    val durum: String,
    val haber_sayisi: Int,
    val haberler: List<Haber>
)