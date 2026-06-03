package com.example.asinahaberuygulamasi.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsFootball
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.asinahaberuygulamasi.model.NewsItem
import com.example.asinahaberuygulamasi.model.TarihteBugunResponse
import kotlinx.coroutines.launch

val BundleDarkBg = Color(0xFF121212)
val BundleSurface = Color(0xFF1E1E1E)
val BundleTextSecondary = Color(0xFFA0A0A0)

data class CategoryItem(val name: String, val icon: ImageVector)

@Composable
fun HomeScreen(
    newsList: List<NewsItem> = emptyList(),
    tarihteBugun: TarihteBugunResponse? = null,
    usdTry: String = "--",
    onNewsClick: (NewsItem) -> Unit,
    onProfileClick: () -> Unit,
    onSavedClick: () -> Unit,
    onBookmarkToggle: (NewsItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        CategoryItem("HEPSİ", Icons.Default.AllInclusive),
        CategoryItem("TEKNOLOJİ", Icons.Default.Memory),
        CategoryItem("EKONOMİ", Icons.Default.Payments),
        CategoryItem("SPOR", Icons.Default.SportsFootball),
        CategoryItem("MAGAZİN", Icons.Default.Stars),
        CategoryItem("TARİHTE BUGÜN", Icons.Default.MenuBook)
    )

    var selectedCategory by remember { mutableStateOf("HEPSİ") }
    var searchQuery by remember { mutableStateOf("") }
    var isLoaded by remember { mutableStateOf(false) }
    val errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(newsList) {
        kotlinx.coroutines.delay(800)
        isLoaded = true
    }

    val filteredNews = newsList.filter {
        (it.category.uppercase() == selectedCategory || selectedCategory == "HEPSİ") &&
                it.title.contains(searchQuery, ignoreCase = true)
    }

    Row(modifier = modifier.fillMaxSize().background(BundleDarkBg)) {
        Column(
            modifier = Modifier
                .width(120.dp)
                .fillMaxHeight()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("AŞİNA", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(40.dp))

            categories.forEach { category ->
                val isSelected = selectedCategory == category.name
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF252932) else Color.Transparent)
                        .clickable { selectedCategory = category.name }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = null,
                            tint = if (isSelected) Color(0xFF2D7FF9) else BundleTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            category.name,
                            color = if (isSelected) Color.White else BundleTextSecondary,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            IconButton(onClick = onSavedClick) {
                Icon(Icons.Default.Bookmark, contentDescription = "Kaydedilenler", tint = Color.White, modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            IconButton(onClick = onProfileClick) {
                Icon(Icons.Default.AccountCircle, contentDescription = "Profil", tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.weight(1f).height(40.dp),
                    color = BundleSurface,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = BundleTextSecondary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) Text("Haber ve konu ara", color = BundleTextSecondary, fontSize = 13.sp)
                                innerTextField()
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                MarketItem("USD", usdTry, true)
                Spacer(modifier = Modifier.width(12.dp))
                MarketItem("IST", "10.150", false)
            }

            if (errorMessage.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage, color = Color(0xFFE53935))
                }
            } else if (!isLoaded) {
                ShimmerLoading()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    if (selectedCategory == "TARİHTE BUGÜN") {
                        if (tarihteBugun != null &&
                            !tarihteBugun.icerik.contains("503") &&
                            !tarihteBugun.icerik.contains("oluşturulamadı")
                        ) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF252932)),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.HistoryEdu,
                                                contentDescription = null,
                                                tint = Color(0xFF2D7FF9),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                "Tarihte Bugün: ${tarihteBugun.tarih}",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            Surface(
                                                color = Color(0xFF2D7FF9).copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    "AI",
                                                    color = Color(0xFF2D7FF9),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(tarihteBugun.icerik, color = BundleTextSecondary, fontSize = 13.sp, lineHeight = 20.sp)
                                    }
                                }
                            }
                        }
                    } else {
                        if (filteredNews.isNotEmpty()) {
                            item {
                                val headlineNews = filteredNews.take(5)
                                val pagerState = rememberPagerState(pageCount = { headlineNews.size })
                                val coroutineScope = rememberCoroutineScope()

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = Color(0xFFE53935),
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.padding(end = 8.dp)
                                        ) {
                                            Text(
                                                "MANŞET",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text("Günün Önemli Haberleri", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        HorizontalPager(
                                            state = pagerState,
                                            modifier = Modifier.fillMaxWidth().height(240.dp)
                                        ) { page ->
                                            FeaturedHeadlineCard(
                                                news = headlineNews[page],
                                                onClick = { onNewsClick(headlineNews[page]) },
                                                onBookmarkClick = { onBookmarkToggle(headlineNews[page]) }
                                            )
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.Center)
                                                .padding(horizontal = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            if (pagerState.currentPage > 0) {
                                                IconButton(
                                                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                                                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape).size(36.dp)
                                                ) {
                                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Önceki", tint = Color.White)
                                                }
                                            } else {
                                                Spacer(modifier = Modifier.size(36.dp))
                                            }

                                            if (pagerState.currentPage < headlineNews.size - 1) {
                                                IconButton(
                                                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                                                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape).size(36.dp)
                                                ) {
                                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Sonraki", tint = Color.White)
                                                }
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.height(24.dp).fillMaxWidth().padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        repeat(headlineNews.size) { iteration ->
                                            val color = if (pagerState.currentPage == iteration) Color(0xFF2D7FF9) else Color.DarkGray
                                            Box(
                                                modifier = Modifier
                                                    .padding(2.dp)
                                                    .clip(CircleShape)
                                                    .background(color)
                                                    .size(8.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            val remainingNews = filteredNews.drop(5)
                            if (remainingNews.isNotEmpty()) {
                                item {
                                    Text("Son Dakika", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                                items(remainingNews.chunked(2)) { pair ->
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        pair.forEach { news ->
                                            SecondaryCard(news, modifier = Modifier.weight(1f), onClick = { onNewsClick(news) })
                                        }
                                        if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedHeadlineCard(news: NewsItem, onClick: () -> Unit, onBookmarkClick: () -> Unit) {
    var isBookmarked by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BundleSurface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
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
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                            startY = 100f
                        )
                    )
            )

            IconButton(
                onClick = {
                    isBookmarked = !isBookmarked
                    onBookmarkClick()
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Favorilere Ekle",
                    tint = if (isBookmarked) Color(0xFF2D7FF9) else Color.White
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Surface(
                    color = Color(0xFF2D7FF9),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        news.category.uppercase(),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    news.title,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    lineHeight = 28.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ShimmerLoading() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF2A2A2A), Color(0xFF3A3A3A), Color(0xFF2A2A2A)),
        start = androidx.compose.ui.geometry.Offset(translateAnim - 200f, 0f),
        end = androidx.compose.ui.geometry.Offset(translateAnim, 0f)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(4) {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(16.dp)).background(shimmerBrush))
        }
    }
}

@Composable
fun SecondaryCard(news: NewsItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BundleSurface)
    ) {
        Column {
            AsyncImage(
                model = news.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(100.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    news.title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(news.source, color = BundleTextSecondary, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun MarketItem(label: String, value: String, isUp: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = BundleTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(
            value,
            color = if (isUp) Color(0xFF4CAF50) else Color(0xFFE53935),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}