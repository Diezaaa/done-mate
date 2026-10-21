package com.example.donemate

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Required setup for dependency injection with Dagger Hilt
@HiltAndroidApp
class DoneMateApplication : Application()