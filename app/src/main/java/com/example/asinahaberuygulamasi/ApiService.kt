package com.example.asinahaberuygulamasi

import com.example.asinahaberuygulamasi.model.HaberlerResponse
import com.example.asinahaberuygulamasi.model.KullaniciGirisResponse
import com.example.asinahaberuygulamasi.model.KullaniciKayitRequest
import com.example.asinahaberuygulamasi.model.MesajResponse
import com.example.asinahaberuygulamasi.model.NewsItem
import com.example.asinahaberuygulamasi.model.TarihteBugunResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("api/haberler")
    suspend fun haberleriGetir(): HaberlerResponse

    @GET("api/tarihte-bugun")
    suspend fun tarihteBugunGetir(): TarihteBugunResponse

    @GET("api/canli-haberler")
    suspend fun canliHaberleriGetir(): List<NewsItem>

    @POST("api/kayit")
    suspend fun kayitOl(@Body kullanici: KullaniciKayitRequest): MesajResponse

    @POST("api/giris")
    suspend fun girisYap(@Body kullanici: KullaniciKayitRequest): KullaniciGirisResponse
}