package com.example.asinahaberuygulamasi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.asinahaberuygulamasi.GeminiService
import com.example.asinahaberuygulamasi.model.NewsItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    news: NewsItem,
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var ozet by remember { mutableStateOf("") }
    var ozetYukleniyor by remember { mutableStateOf(false) }

    var soru by remember { mutableStateOf("") }
    var cevap by remember { mutableStateOf("") }
    var cevapYukleniyor by remember { mutableStateOf(false) }
    var chatAcik by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Haber Detayı") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (!news.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = news.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(
                    onClick = {},
                    label = { Text(news.category, fontSize = 12.sp) }
                )
                SuggestionChip(
                    onClick = {},
                    label = { Text(news.source, fontSize = 12.sp) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = news.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = news.source,
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = news.description,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "🤖 Yapay Zeka Özeti",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (ozet.isEmpty() && !ozetYukleniyor) {
                Button(
                    onClick = {
                        ozetYukleniyor = true
                        coroutineScope.launch {
                            ozet = GeminiService.haberOzetle(news.title, news.description)
                            ozetYukleniyor = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Haberi Özetle")
                }
            }

            if (ozetYukleniyor) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Özet oluşturuluyor...")
                }
            }

            if (ozet.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = ozet,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "💬 Habere Soru Sor",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (!chatAcik) {
                Button(
                    onClick = { chatAcik = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Soruyu Aç")
                }
            } else {
                OutlinedTextField(
                    value = soru,
                    onValueChange = { soru = it },
                    label = { Text("Habere dair sorunuzu yazın...") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (soru.isNotBlank()) {
                                    cevapYukleniyor = true
                                    cevap = ""
                                    coroutineScope.launch {
                                        cevap = GeminiService.haberSoruCevap(
                                            haberBaslik = news.title,
                                            haberIcerigi = news.description,
                                            kullaniciSorusu = soru
                                        )
                                        cevapYukleniyor = false
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Gönder")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (cevapYukleniyor) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Yanıt oluşturuluyor...")
                    }
                }

                if (cevap.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Soru: $soru",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = cevap,
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}