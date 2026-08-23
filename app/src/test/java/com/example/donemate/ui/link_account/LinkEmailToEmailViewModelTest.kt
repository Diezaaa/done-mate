package com.example.donemate.ui.link_account

import com.example.donemate.ui.link_account.fake.remote.AccountRepositoryFake
import com.example.donemate.ui.screens.link_email.LinkEmailViewModel
import junit.framework.TestCase.assertEquals
import org.junit.Test

class LinkEmailToEmailViewModelTest {
    private val viewModel = LinkEmailViewModel(AccountRepositoryFake())

    @Test
    fun linkAccount_ToEmail_WhenCallerChangesPassword_passwordIsUpdated() {
        var currentPassword = viewModel.uiState.value.password

        val newValue = "12345678ABC"
        viewModel.updatePassword(newValue)

        currentPassword = viewModel.uiState.value.password
        assertEquals(newValue, currentPassword)

    }
}