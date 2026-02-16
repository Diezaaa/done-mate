package com.example.donemate.ui.link_account

import com.example.donemate.ui.link_account.fake.remote.AccountRepositoryFake
import com.example.donemate.ui.screens.link_account.LinkAccountViewModel
import junit.framework.TestCase.assertEquals
import org.junit.Test

class LinkAccountViewModelTest {
    private val viewModel = LinkAccountViewModel(AccountRepositoryFake())

    @Test
    fun linkAccount_WhenCallerChangesPassword_passwordIsUpdated() {
        var currentPassword = viewModel.uiState.value.password

        val newValue = "12345678ABC"
        viewModel.onPasswordChange(newValue)

        currentPassword = viewModel.uiState.value.password
        assertEquals(newValue, currentPassword)

    }
}