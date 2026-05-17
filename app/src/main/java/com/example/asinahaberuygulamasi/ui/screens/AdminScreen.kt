package com.example.asinahaberuygulamasi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Paneli") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Haber ekleme formu */ }) {
                Icon(Icons.Default.Add, contentDescription = "Haber Ekle")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Haber Yönetimi", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("İstatistikler", style = MaterialTheme.typography.titleMedium)
                    Text("Toplam Haber: 154")
                    Text("Bugünkü Okunma: 1.2k")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Bekleyen Haberler", style = MaterialTheme.typography.titleMedium)
            Text("Şu an bekleyen haber yok.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
