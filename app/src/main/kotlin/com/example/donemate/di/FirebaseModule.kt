package com.example.donemate.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.storage.FirebaseStorage
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
    fun providesFirebaseAuth(): FirebaseAuth {
        val auth = FirebaseAuth.getInstance()
        return auth
    }

    @Provides
    @Singleton
    fun providesFirebaseFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()

        val firestoreSettings = firestoreSettings {
            // Use memory cache
            setLocalCacheSettings(memoryCacheSettings {})
        }
        firestore.firestoreSettings = firestoreSettings

        return firestore
    }

    @Provides
    @Singleton
    fun providesFirebaseStorage(): FirebaseStorage {
        val storage = FirebaseStorage.getInstance()
        return storage
    }
}