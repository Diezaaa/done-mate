package com.example.donemate

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// מפעיל את מנגנון הזרקת התלויות של היישומון.
@HiltAndroidApp
class DoneMateApplication : Application()