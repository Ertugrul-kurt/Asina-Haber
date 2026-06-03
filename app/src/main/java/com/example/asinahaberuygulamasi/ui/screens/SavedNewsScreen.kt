package com.example.asinahaberuygulamasi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.asinahaberuygulamasi.data.local.AsinaDatabase
import com.example.asinahaberuygulamasi.data.local.NewsRepository
import com.example.asinahaberuygulamasi.data.local.SavedNews
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedNewsScreen(
    onBackClick: () -> Unit,
    onNewsClick: (SavedNews) -> Unit
) {
    val context = LocalContext.current
    val repository = remember {
        NewsRepository(AsinaDatabase.getDatabase(context).savedNewsDao())
    }
    val coroutineScope = rememberCoroutineScope()
    val savedNewsList by repository.tumKayitliHaberler.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kaydedilenler", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (savedNewsList.isEmpty()) {
            // Boş durum
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Henüz kaydedilen haber yok",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Haberleri kaydet",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(savedNewsList, key = { it.roomId }) { savedNews ->
                    SavedNewsCard(
                        savedNews = savedNews,
                        onClick = { onNewsClick(savedNews) },
                        onDeleteClick = {
                            coroutineScope.launch {
                                repository.sil(savedNews.newsId)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SavedNewsCard(
    savedNews: SavedNews,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Görsel
            AsyncImage(
                model = savedNews.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Metin
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = savedNews.category,
                    fontSize = 11.sp,
                    color = Color(0xFF2D7FF9),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = savedNews.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = savedNews.date,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Sil butonu
            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Bookmark,
                    contentDescription = "Kayıttan Kaldır",
                    tint = Color(0xFF2D7FF9)
                )
            }
        }
    }
}

