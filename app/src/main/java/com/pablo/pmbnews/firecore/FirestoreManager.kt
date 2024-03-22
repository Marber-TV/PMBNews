package com.pablo.pmbnews.firecore

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.pablo.pmbnews.App
import com.pablo.pmbnews.Note
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreManager(context: Context) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = (context.applicationContext as App).auth
    val userId = auth.getCurrentUser()?.uid
    val COLECCION = "notas"

    suspend fun addNote(note: Note){
        note.id =userId.toString()
        firestore.collection(COLECCION).add(note).await()
    }

    suspend fun updateNote(note: Note) {
        val noteRef = note.id?.let {
            firestore.collection("notes").document(it)
        }
        noteRef?.set(note)?.await()
    }

    suspend fun deleteNoteById(noteId: String) {
        firestore.collection("notes").document(noteId).delete().await()
    }

    fun getNotesFlow(): Flow<List<Note>> = callbackFlow {
        val notesRef = firestore.collection("notes")
            .whereEqualTo("userId", userId)
            .orderBy("title")
        val subscription = notesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            snapshot?.let{ QuerySnapshot ->
                val notes = mutableListOf<Note>()
                for (document in QuerySnapshot.documents) {
                    val note = document.toObject(Note::class.java)
                    note?.id = document.id
                    note?.let { notes.add(note) }
                }
                trySend(notes ?: emptyList()).isSuccess
            }
        }
        awaitClose { subscription.remove() }
    }
}