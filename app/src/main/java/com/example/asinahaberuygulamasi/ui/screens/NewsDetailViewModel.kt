package com.example.asinahaberuygulamasi.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.asinahaberuygulamasi.data.CommentRepository
import com.example.asinahaberuygulamasi.model.Comment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsDetailViewModel : ViewModel() {

    private val repository = CommentRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    private val _commentText = MutableStateFlow("")
    val commentText: StateFlow<String> = _commentText

    // Login ekranından gelen isim buraya set edilir
    private var _kullaniciIsim: String = ""

    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    val currentUserName: String
        get() = when {
            _kullaniciIsim.isNotBlank() -> _kullaniciIsim
            !auth.currentUser?.displayName.isNullOrBlank() -> auth.currentUser!!.displayName!!
            else -> auth.currentUser?.email?.substringBefore("@") ?: "Anonim"
        }

    fun setKullaniciIsim(isim: String) {
        _kullaniciIsim = isim
    }

    fun loadComments(newsId: String) {
        viewModelScope.launch {
            repository.getComments(newsId).collect {
                _comments.value = it
            }
        }
    }

    fun onCommentTextChange(text: String) {
        _commentText.value = text
    }

    fun sendComment(newsId: String) {
        val text = _commentText.value.trim()
        if (text.isBlank()) return
        viewModelScope.launch {
            val comment = Comment(
                newsId = newsId,
                userId = currentUserId,
                userName = currentUserName,
                text = text,
                timestamp = System.currentTimeMillis()
            )
            repository.sendComment(comment)
            _commentText.value = ""
        }
    }

    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            repository.deleteComment(commentId)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NewsDetailViewModel() as T
            }
        }
    }
}