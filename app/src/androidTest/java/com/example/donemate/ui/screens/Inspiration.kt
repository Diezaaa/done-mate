package com.example.donemate.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import com.example.donemate.ui.screens.inspiration.InspirationScreen
import com.example.donemate.ui.theme.DoneMateTheme
import org.junit.Rule
import org.junit.Test

class Inspiration {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test_fun() {
        composeTestRule.setContent {
            DoneMateTheme() {
                InspirationScreen()
            }
        }
    }
}