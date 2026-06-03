package com.example.asinahaberuygulamasi.data

import com.example.asinahaberuygulamasi.model.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CommentRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getComments(newsId: String): Flow<List<Comment>> = callbackFlow {
        val listener = db.collection("comments")
            .whereEqualTo("newsId", newsId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val comments = snapshot?.documents?.mapNotNull {
                    it.toObject(Comment::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(comments)
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendComment(comment: Comment) {
        db.collection("comments").add(comment).await()
    }

    suspend fun deleteComment(commentId: String) {
        db.collection("comments").document(commentId).delete().await()
    }
}