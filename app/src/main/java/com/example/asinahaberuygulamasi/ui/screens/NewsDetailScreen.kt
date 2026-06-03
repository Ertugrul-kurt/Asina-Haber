package com.example.asinahaberuygulamasi.ui.screens

import android.content.Intent
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.asinahaberuygulamasi.GeminiService
import com.example.asinahaberuygulamasi.data.local.AsinaDatabase
import com.example.asinahaberuygulamasi.data.local.NewsRepository
import com.example.asinahaberuygulamasi.data.local.SavedNews
import com.example.asinahaberuygulamasi.model.Comment
import com.example.asinahaberuygulamasi.model.NewsItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    news: NewsItem,
    allNews: List<NewsItem> = emptyList(),
    kullaniciIsim: String = "",
    onBackClick: () -> Unit,
    onRelatedNewsClick: (NewsItem) -> Unit = {}
) {
    val darkBg = Color(0xFF121212)
    val surface = Color(0xFF1E1E1E)
    val textSecondary = Color(0xFFA0A0A0)

    val relatedNews = allNews
        .filter { it.category == news.category && it.id != news.id }
        .take(3)

    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }

    val repository = remember {
        NewsRepository(AsinaDatabase.getDatabase(context).savedNewsDao())
    }
    val coroutineScope = rememberCoroutineScope()
    val isBookmarked by repository.haberKayitliMi(news.id).collectAsState(initial = false)

    val ttsRef = remember {
        var instance: TextToSpeech? = null
        instance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                instance?.language = Locale("tr", "TR")
            }
        }
        instance
    }

    DisposableEffect(Unit) {
        onDispose {
            ttsRef?.stop()
            ttsRef?.shutdown()
        }
    }

    var showAiDialog by remember { mutableStateOf(false) }
    var isAiLoading by remember { mutableStateOf(false) }
    var aiResult by remember { mutableStateOf("") }

    Scaffold(
        containerColor = darkBg,
        topBar = {
            TopAppBar(
                title = { Text(news.source, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, news.title)
                            putExtra(Intent.EXTRA_TEXT, "${news.title}\n\n${news.description}\n\nKaynak: ${news.source}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Haberi Paylaş"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Paylaş", tint = Color.White)
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            if (isBookmarked) {
                                repository.sil(news.id)
                            } else {
                                repository.kaydet(
                                    SavedNews(
                                        newsId = news.id,
                                        title = news.title,
                                        description = news.description,
                                        imageUrl = news.imageUrl,
                                        category = news.category,
                                        source = news.source,
                                        date = news.date
                                    )
                                )
                            }
                        }
                    }) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Kaydet",
                            tint = if (isBookmarked) Color(0xFF2D7FF9) else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkBg,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
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
                                colors = listOf(Color.Transparent, darkBg),
                                startY = 400f
                            )
                        )
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    color = Color(0xFF2D7FF9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = news.category,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 34.sp,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(news.source.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(news.source, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(news.category, color = textSecondary, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = {
                        if (isPlaying) {
                            ttsRef?.stop()
                            isPlaying = false
                        } else {
                            ttsRef?.speak(news.description, TextToSpeech.QUEUE_FLUSH, null, null)
                            isPlaying = true
                        }
                    }) {
                        Icon(
                            imageVector = if (isPlaying) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Sesli Oku",
                            tint = if (isPlaying) Color(0xFF2D7FF9) else textSecondary
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.TextFormat, contentDescription = null, tint = textSecondary)
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 24.dp))

                Text(
                    text = news.description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 28.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 17.sp
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        showAiDialog = true
                        isAiLoading = true
                        coroutineScope.launch {
                            aiResult = GeminiService.haberDogrula(
                                haberBaslik = news.title,
                                haberIcerigi = news.description
                            )
                            isAiLoading = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF2D7FF9))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Haberi Yapay Zeka ile Doğrula", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("İlgili Haberler", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))

                if (relatedNews.isEmpty()) {
                    Text("Bu kategoride başka haber bulunamadı.", color = textSecondary, fontSize = 13.sp)
                } else {
                    relatedNews.forEach { relatedItem ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .clickable { onRelatedNewsClick(relatedItem) },
                            color = surface,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = relatedItem.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = relatedItem.title,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = relatedItem.source, color = textSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Yorumlar Bölümü ──────────────────────────────────────────
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            CommentsSection(
                newsId = news.id.toString(),
                kullaniciIsim = kullaniciIsim,
                darkBg = Color(0xFF121212),
                surface = Color(0xFF1E1E1E),
                textSecondary = Color(0xFFA0A0A0)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showAiDialog) {
        AlertDialog(
            onDismissRequest = { showAiDialog = false },
            containerColor = Color(0xFF1E1E1E),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF2D7FF9))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Haber Doğrulama", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                if (isAiLoading) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        CircularProgressIndicator(color = Color(0xFF2D7FF9))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Metin analiz ediliyor...", color = Color(0xFFA0A0A0), fontSize = 14.sp)
                    }
                } else {
                    Text(aiResult, color = Color.White, lineHeight = 24.sp, fontSize = 15.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showAiDialog = false }) {
                    Text("Kapat", color = Color(0xFF2D7FF9), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
@Composable
fun CommentsSection(
    newsId: String,
    kullaniciIsim: String = "",
    darkBg: Color,
    surface: Color,
    textSecondary: Color,
    viewModel: NewsDetailViewModel = viewModel(factory = NewsDetailViewModel.Factory)
) {
    val comments by viewModel.comments.collectAsState()
    val commentText by viewModel.commentText.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(newsId) {
        viewModel.loadComments(newsId)
        if (kullaniciIsim.isNotBlank()) {
            viewModel.setKullaniciIsim(kullaniciIsim)
        }
    }

    LaunchedEffect(comments.size) {
        if (comments.isNotEmpty()) {
            listState.animateScrollToItem(comments.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(darkBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Yorumlar",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = CircleShape,
                color = Color(0xFF2D7FF9)
            ) {
                Text(
                    text = "${comments.size}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp, max = 420.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (comments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Henüz yorum yok. İlk yorumu sen yap!",
                            color = textSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            items(comments, key = { it.id }) { comment ->
                CommentItem(
                    comment = comment,
                    isOwner = comment.userId == viewModel.currentUserId,
                    surface = surface,
                    textSecondary = textSecondary,
                    onDelete = { viewModel.deleteComment(comment.id) }
                )
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(surface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { viewModel.onCommentTextChange(it) },
                placeholder = {
                    Text("Yorumunuzu yazın...", color = textSecondary, fontSize = 14.sp)
                },
                modifier = Modifier.weight(1f),
                maxLines = 4,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2D7FF9),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF2D7FF9),
                    focusedContainerColor = Color.White.copy(alpha = 0.05f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { viewModel.sendComment(newsId) },
                enabled = commentText.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (commentText.isNotBlank()) Color(0xFF2D7FF9) else Color.White.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Gönder",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    isOwner: Boolean,
    surface: Color,
    textSecondary: Color,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM HH:mm", Locale("tr"))
    val date = dateFormat.format(Date(comment.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOwner) Color(0xFF1A2E4A) else surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = if (isOwner) Color(0xFF2D7FF9) else Color.White.copy(alpha = 0.15f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = comment.userName.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isOwner) "${comment.userName} (Sen)" else comment.userName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isOwner) Color(0xFF2D7FF9) else Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = date,
                        fontSize = 11.sp,
                        color = textSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comment.text,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 20.sp
                )
            }

            if (isOwner) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Yorumu Sil",
                        tint = Color.Red.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}