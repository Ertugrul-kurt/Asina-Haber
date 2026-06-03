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
import com.example.asinahaberuygulamasi.ui.screens.ProfileScreen
import com.example.asinahaberuygulamasi.ui.screens.SavedNewsScreen
import com.example.asinahaberuygulamasi.ui.theme.AsinaHaberUygulamasiTheme
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            AsinaHaberUygulamasiTheme {
                val navController = rememberNavController()
                var realNews by remember { mutableStateOf(listOf<NewsItem>()) }
                var tarihteBugun by remember { mutableStateOf<TarihteBugunResponse?>(null) }
                var kullaniciEmail by remember { mutableStateOf("") }
                var kullaniciIsim by remember { mutableStateOf("") }
                var usdTry by remember { mutableStateOf("--") }

                LaunchedEffect(Unit) {
                    try {
                        val response = RetrofitClient.apiService.canliHaberleriGetir()
                        realNews = response.mapIndexed { index, news -> news.copy(id = index) }
                    } catch (e: Exception) {
                        android.util.Log.e("ASINA", "Haber hatası: ${e.message}", e)
                    }
                    try {
                        tarihteBugun = RetrofitClient.apiService.tarihteBugunGetir()
                    } catch (e: Exception) {
                        android.util.Log.e("ASINA", "Tarihte bugün hatası: ${e.message}", e)
                    }
                    try {
                        val json = withContext(Dispatchers.IO) {
                            URL("https://v6.exchangerate-api.com/v6/ead58b06e98292fa39262179/latest/USD").readText()
                        }
                        val rate = JSONObject(json)
                            .getJSONObject("conversion_rates")
                            .getDouble("TRY")
                        usdTry = String.format("%.2f", rate)
                    } catch (e: Exception) {
                        android.util.Log.e("ASINA", "Kur hatası: ${e.message}", e)
                    }
                }

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { email, isim, isAdmin ->
                                kullaniciEmail = email
                                kullaniciIsim = isim
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
                            usdTry = usdTry,
                            onNewsClick = { news -> navController.navigate("detail/${news.id}") },
                            onProfileClick = { navController.navigate("profile") },
                            onSavedClick = { navController.navigate("saved") }
                        )
                    }
                    composable(
                        "detail/{newsId}",
                        arguments = listOf(navArgument("newsId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val newsId = backStackEntry.arguments?.getInt("newsId")
                        val news = realNews.find { it.id == newsId }
                        if (news != null) {
                            NewsDetailScreen(
                                news = news,
                                allNews = realNews,
                                kullaniciIsim = kullaniciIsim,
                                onBackClick = { navController.popBackStack() },
                                onRelatedNewsClick = { relatedNews ->
                                    navController.navigate("detail/${relatedNews.id}")
                                }
                            )
                        }
                    }
                    composable("saved") {
                        SavedNewsScreen(
                            onBackClick = { navController.popBackStack() },
                            onNewsClick = { savedNews ->
                                navController.navigate("detail/${savedNews.newsId}")
                            }
                        )
                    }
                    composable("profile") {
                        ProfileScreen(
                            email = kullaniciEmail,
                            isim = kullaniciIsim,
                            onBackClick = { navController.popBackStack() },
                            onLogoutClick = {
                                kullaniciEmail = ""
                                kullaniciIsim = ""
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("admin") {
                        AdminScreen(onBackClick = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}