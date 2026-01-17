package com.example.donemate.di

import com.example.donemate.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
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
    fun auth(): FirebaseAuth {
        val auth = FirebaseAuth.getInstance()

        // Use emulator only for debug builds
        if (BuildConfig.DEBUG) {
            // On Android emulator, use 10.0.2.2 to reach host machine
            auth.useEmulator("10.0.2.2", 9099)
        }

        return auth
    }

    @Provides
    @Singleton
    fun firestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()

        if (BuildConfig.DEBUG) {
            firestore.useEmulator("10.0.2.2", 8080)
            // It's usually helpful to disable persistence when talking to emulator
            firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
                .build()
        }

        return firestore
    }

    @Provides
    @Singleton
    fun mediaStorage(): FirebaseStorage {
        val storage = FirebaseStorage.getInstance()

        if (BuildConfig.DEBUG) {
            storage.useEmulator("10.0.2.2", 9199)
        }

        return storage
    }
}