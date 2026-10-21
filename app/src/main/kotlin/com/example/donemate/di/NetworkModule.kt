package com.example.donemate.di

import com.example.donemate.network.ConnectivityManagerNetworkMonitor
import com.example.donemate.network.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {
    @Binds
    abstract fun bindsNetworkMonitor(
        connectivityManagerNetworkMonitor: ConnectivityManagerNetworkMonitor
    ): NetworkMonitor
}