package com.example.asinahaberuygulamasi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.asinahaberuygulamasi.model.NewsItem

val BundleSurface = Color(0xFF1E1E1E)
val BundleTextSecondary = Color(0xFFA0A0A0)
val AccentBlue = Color(0xFF2D7FF9)

@Composable
fun NewsCard(news: NewsItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp), // Köşeleri biraz daha yumuşattık
        colors = CardDefaults.cardColors(containerColor = BundleSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Karanlık temada gölge yerine zemin rengi farkı daha şık durur
    ) {
        Column {
            // --- RESİM VE ÜZERİNDEKİ ETİKET ---
            Box(modifier = Modifier.height(200.dp)) {
                AsyncImage(
                    model = news.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, BundleSurface),
                                startY = 250f // Geçişin başlayacağı yükseklik
                            )
                        )
                )
                Surface(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart),
                    color = AccentBlue,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = news.category.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 18.dp, top = 4.dp)
            ) {
                Text(
                    text = news.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Sol Taraf: Kaynak Logosu ve İsmi
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(24.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = AccentBlue.copy(alpha = 0.2f) // Hafif saydam mavi arkaplan
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = news.source.take(1).uppercase(), // Kaynağın baş harfi
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentBlue
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = news.source,
                            color = BundleTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = "Yeni Eklenen",
                        color = AccentBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}