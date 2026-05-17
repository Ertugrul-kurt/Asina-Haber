package com.example.asinahaberuygulamasi.model

data class TarihteBugunResponse(
    val tarih: String,
    val icerik: String,
    val hata: String? = null
)