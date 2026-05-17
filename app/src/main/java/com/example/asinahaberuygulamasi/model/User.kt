package com.example.asinahaberuygulamasi.model

data class KullaniciKayitRequest(
    val email: String,
    val sifre: String,
    val isim: String = ""
)

data class KullaniciGirisResponse(
    val email: String,
    val isim: String,
    val admin: Boolean
)

data class MesajResponse(
    val mesaj: String
)