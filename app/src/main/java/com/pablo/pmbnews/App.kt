package com.pablo.pmbnews

import android.app.Application
import com.pablo.pmbnews.firecore.AuthManager
import com.pablo.pmbnews.firecore.FirestoreManager
import com.google.firebase.FirebaseApp

class App: Application() {

    lateinit var auth: AuthManager
    private lateinit var firestore: FirestoreManager

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        auth = AuthManager(this)
        firestore = FirestoreManager(this)
    }
}