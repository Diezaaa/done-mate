package com.example.donemate.model.service.module

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun auth(): FirebaseAuth {
        val auth = FirebaseAuth.getInstance()
        return auth
    }
    @Provides
    @Singleton
    fun firestore(): FirebaseFirestore{
        val firestore = FirebaseFirestore.getInstance()
        return firestore
    }
}