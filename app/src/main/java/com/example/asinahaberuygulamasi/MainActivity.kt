package com.example.asinahaberuygulamasi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.asinahaberuygulamasi.model.NewsItem
import com.example.asinahaberuygulamasi.model.TarihteBugunResponse
import com.example.asinahaberuygulamasi.ui.screens.AdminScreen
import com.example.asinahaberuygulamasi.ui.screens.HomeScreen
import com.example.asinahaberuygulamasi.ui.screens.LoginScreen
import com.example.asinahaberuygulamasi.ui.screens.NewsDetailScreen
import com.example.asinahaberuygulamasi.ui.theme.AsinaHaberUygulamasiTheme
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AsinaHaberUygulamasiTheme {
                val navController = rememberNavController()

                var realNews by remember { mutableStateOf(listOf<NewsItem>()) }
                var tarihteBugun by remember { mutableStateOf<TarihteBugunResponse?>(null) }

                LaunchedEffect(Unit) {
                    try {
                        realNews = RetrofitClient.apiService.canliHaberleriGetir()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        realNews = listOf(
                            NewsItem(
                                id = "1",
                                title = "Bağlantı Hatası",
                                description = "Haberler yüklenemedi. İnternet bağlantınızı veya sunucuyu kontrol edin.",
                                imageUrl = "https://picsum.photos/seed/error/800/400",
                                category = "SİSTEM",
                                source = "Aşina Haber"
                            )
                        )
                    }

                    try {
                        tarihteBugun = RetrofitClient.apiService.tarihteBugunGetir()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { email, isAdmin ->
                                if (isAdmin) navController.navigate("admin")
                                else navController.navigate("home")
                            },
                            onRegisterClick = { },
                            onBackClick = { navController.navigate("home") }
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            newsList = realNews,
                            tarihteBugun = tarihteBugun,
                            onNewsClick = { news ->
                                val encodedId = URLEncoder.encode(news.id, StandardCharsets.UTF_8.toString())
                                navController.navigate("detail/$encodedId")
                            },
                            onProfileClick = { }
                        )
                    }
                    composable(
                        "detail/{newsId}",
                        arguments = listOf(navArgument("newsId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val newsId = backStackEntry.arguments?.getString("newsId")

                        if (newsId != null) {
                            val decodedId = URLDecoder.decode(newsId, StandardCharsets.UTF_8.toString())
                            val news = realNews.find { it.id == decodedId }

                            if (news != null) {
                                NewsDetailScreen(
                                    news = news,
                                    allNews = realNews,
                                    onBackClick = { navController.popBackStack() },
                                    onRelatedNewsClick = { relatedNews ->
                                        val encodedId = URLEncoder.encode(relatedNews.id, StandardCharsets.UTF_8.toString())
                                        navController.navigate("detail/$encodedId")
                                    }
                                )
                            }
                        }
                    }
                    composable("admin") {
                        AdminScreen(onBackClick = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}