package com.example.donemate

import android.net.Uri
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import com.example.donemate.ui.screens.verify_email.VerifyEmailScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberNavBackStack
import com.example.donemate.data.model.User
import com.example.donemate.ui.screens.change_email.ChangeEmailScreen
import com.example.donemate.ui.screens.change_email.ChangeEmailViewModel
import com.example.donemate.ui.screens.change_password.ChangePasswordScreen
import com.example.donemate.ui.screens.change_password.ChangePasswordViewModel
import com.example.donemate.ui.screens.inspiration.InspirationScreen
import com.example.donemate.ui.screens.inspiration.InspirationViewModel
import com.example.donemate.ui.screens.link_email.LinkEmailScreen
import com.example.donemate.ui.screens.link_email.LinkEmailViewModel
import com.example.donemate.ui.screens.permission.PermissionScreen
import com.example.donemate.ui.screens.preview.PreviewScreen
import com.example.donemate.ui.screens.preview.PreviewViewModel
import com.example.donemate.ui.screens.reset_password.ResetPasswordScreen
import com.example.donemate.ui.screens.reset_password.ResetPasswordViewModel
import com.example.donemate.ui.screens.settings.SettingsScreen
import com.example.donemate.ui.screens.settings.SettingsViewModel
import com.example.donemate.ui.screens.sign_in.SignInScreen
import com.example.donemate.ui.screens.sign_in.SignInViewModel
import com.example.donemate.ui.screens.sign_up.SignUpScreen
import com.example.donemate.ui.screens.sign_up.SignUpViewModel
import com.example.donemate.ui.screens.tasks.TasksScreen
import com.example.donemate.ui.screens.tasks.TasksViewModel
import com.example.donemate.ui.screens.task.TaskScreen
import com.example.donemate.ui.screens.task.TaskViewModel
import com.example.donemate.ui.screens.verify_email.VerifyEmailViewModel
import com.google.firebase.auth.EmailAuthProvider
import kotlin.collections.contains
import kotlin.collections.removeLastOrNull

@Serializable
data object SignUp : NavKey

@Serializable
data object SignIn : NavKey


@Serializable
data class VerifyEmail(val emailToVerify: String? = null) : NavKey

@Serializable
data object ChangePassword : NavKey

@Serializable
data object Tasks : TopLevelRoute() {
    override val icon = Icons.Default.Menu
}

@Serializable
data class Task(val id: String?, val uri: String? = null) : NavKey

@Serializable
data object LinkEmail : NavKey

@Serializable
sealed class TopLevelRoute : NavKey {
    abstract val icon: ImageVector
}


@Serializable
data object Settings : TopLevelRoute() {
    override val icon = Icons.Default.Settings
}

@Serializable
data object ResetPassword : NavKey

@Serializable
data class Permission(val taskTitle: String) : NavKey

@Serializable
data class Preview(val taskTitle: String) : NavKey

@Serializable
data object ChangeEmail : NavKey

@Serializable
data object Inspiration : TopLevelRoute() {
    override val icon = Icons.Default.Lightbulb
}


private val TOP_LEVEL_ROUTES: List<TopLevelRoute> = listOf(Tasks, Inspiration, Settings)

// Move from list to a class extended with NavKey
val ALLOWED_WHEN_UNVERIFIED_USER: List<NavKey> =
    listOf(SignUp, SignIn, LinkEmail, ChangeEmail, VerifyEmail())

@Composable
fun DoneMateApp(
    user: User?,
    onOfflineDialogDismiss: () -> Unit,
    offlineDialogShownOnce: Boolean,
) {
    var sessionCapturedUri by rememberSaveable { mutableStateOf<String?>(null) }
    val backStack = rememberNavBackStack(if (user != null) Tasks else SignUp)
    val currentDestination = backStack.lastOrNull()
    val showBottomBar = currentDestination in TOP_LEVEL_ROUTES
    val snackBarHostState = remember { SnackbarHostState() }


    LaunchedEffect(user) {

        if (user != null && !user.isVerified && user.authProviders.contains(EmailAuthProvider.PROVIDER_ID)) {
            deleteAllEmailUnrelatedScreenAndNavigateToVerifyEmail(backStack, user.email ?: null)
        }
    }

    LaunchedEffect(currentDestination) {
        if (currentDestination == null) return@LaunchedEffect

        when (currentDestination) {
            // Keep the URI alive while on these screens
            is Task, is Preview, is Permission -> {
                /* Do nothing */
            }
            // Wipe the URI for any other screen (Tasks, Settings, SignIn, etc.)
            else -> {
                sessionCapturedUri = null
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    TOP_LEVEL_ROUTES.forEach { topLevelRoute ->
                        val isSelected = topLevelRoute == currentDestination
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { backStack.add(topLevelRoute) },
                            label = { Text(topLevelRoute.toString()) },
                            icon = {
                                Icon(
                                    imageVector = topLevelRoute.icon,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                snackBarHostState, modifier = Modifier.windowInsetsPadding(
                    WindowInsets.ime.exclude(WindowInsets.navigationBars)
                )
            )
        },
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 7.dp),
            entryProvider = entryProvider {
                entry<SignIn> {
                    val viewModel = hiltViewModel<SignInViewModel>()
                    SignInScreen(
                        vm = viewModel,
                        navigateToResetPassword = {
                            backStack.add(ResetPassword)
                        },
                        navigateToSignUp = {
                            backStack.add(SignUp)
                        },
                        navigateToTasks = {
                            backStack.add(Tasks)
                        },
                        onShowSnackbar = { message: String ->
                            snackBarHostState.showSnackbar(message)
                        }
                    )
                }
                entry<SignUp> {
                    val viewModel = hiltViewModel<SignUpViewModel>()
                    SignUpScreen(
                        navigateToSignIn = { backStack.add(SignIn) },
                        vm = viewModel,
                        navigateToTasks = {
                            backStack.add(Tasks)
                        },
                        onShowSnackbar = { message: String ->
                            snackBarHostState.showSnackbar(message)
                        }
                    )
                }
                entry<Tasks> {
                    val viewModel = hiltViewModel<TasksViewModel>()
                    TasksScreen(
                        navigateToAdd = {
                            backStack.add(
                                Task(
                                    id = null
                                )
                            )
                        },
                        navigateToEdit = { id ->
                            backStack.add(
                                Task(
                                    id = id
                                )
                            )
                        },
                        vm = viewModel,
                        isOfflineDialogShownOnce = offlineDialogShownOnce,
                        onOfflineDialogDismiss = onOfflineDialogDismiss
                    )
                }
                entry<Task> { key ->
                    val vm = hiltViewModel<TaskViewModel>()
                    TaskScreen(
                        navigateToTasks = {
                            backStack.removeLastOrNull()
                        },
                        navigateToPermission = { taskTitle ->
                            backStack.add(Permission(taskTitle))
                        },
                        navigateToPreview = { taskTitle ->
                            backStack.add(Preview(taskTitle))
                        },
                        id = key.id,
                        cameraImageUri = sessionCapturedUri ?: key.uri,
                        vm = vm
                    )
                }
                entry<Settings> {
                    val vm = hiltViewModel<SettingsViewModel>()
                    SettingsScreen(
                        navigateToLinkEmail = {
                            backStack.add(LinkEmail)
                        },
                        navigateToChangePassword = {
                            backStack.add(ChangePassword)
                        },
                        navigateToChangeEmail = {
                            backStack.add(ChangeEmail)
                        },
                        vm = vm,
                        onShowSnackbar = { message: String ->
                            snackBarHostState.showSnackbar(message)
                        },
                        navigateToSignUp = {
                            setEntryAsRoot(backStack, SignUp)
                        }
                    )
                }
                entry<LinkEmail> {
                    val vm = hiltViewModel<LinkEmailViewModel>()
                    LinkEmailScreen(
                        navigateToTasks = {
                            backStack.add(Tasks)
                        },
                        vm = vm,
                        onShowSnackbar = { message: String ->
                            snackBarHostState.showSnackbar(message)
                        }
                    )
                }
                entry<ResetPassword> {
                    val vm = hiltViewModel<ResetPasswordViewModel>()
                    ResetPasswordScreen(
                        vm = vm
                    )
                }
                entry<ChangePassword> {
                    val vm = hiltViewModel<ChangePasswordViewModel>()
                    ChangePasswordScreen(
                        vm = vm,
                        onShowSnackbar = { message: String ->
                            snackBarHostState.showSnackbar(message)
                        }
                    )
                }
                entry<Permission> { key ->
                    PermissionScreen(
                        onNavigateToPreview = {
                            backStack.add(Preview(key.taskTitle))
                        }
                    )
                }
                entry<Preview> { key ->
                    val vm = hiltViewModel<PreviewViewModel>()
                    PreviewScreen(
                        taskTitle = key.taskTitle,
                        viewModel = vm,
                        onNavigateToTask = { uri: Uri ->
                            sessionCapturedUri = uri.toString()
                            backStack.removeLastOrNull()
                        })
                }
                entry<Inspiration> {
                    val vm = hiltViewModel<InspirationViewModel>()
                    InspirationScreen(vm = vm)
                }
                entry<ChangeEmail> {
                    val vm = hiltViewModel<ChangeEmailViewModel>()
                    ChangeEmailScreen(
                        vm = vm,
                        onShowSnackbar = { message: String ->
                            snackBarHostState.showSnackbar(message)
                        },
                        navigateToVerifyEmail = {
                            backStack.add(VerifyEmail(it))
                        }
                    )
                }
                entry<VerifyEmail> { key ->
                    val vm = hiltViewModel<VerifyEmailViewModel>()
                    VerifyEmailScreen(
                        vm = vm,
                        onNavigateAfterVerification = {
                            backStack.add(Tasks)
                        },
                        onNavigateBack = {
                            if (backStack.size > 1) backStack.removeLastOrNull()
                        },
                        emailToVerify = key.emailToVerify,
                        onNavigateToSignUp = {
                            setEntryAsRoot(backStack, SignUp)
                        },
                        shouldDisplayBackButton = backStack.size > 1
                    )
                }
            }
        )
    }
}

fun setEntryAsRoot(backStack: NavBackStack<NavKey>, entry: NavKey) {
    while (backStack.size > 1) {
        backStack.removeFirstOrNull()
    }
    backStack.add(entry)
    backStack.removeFirstOrNull()
}

fun deleteAllEmailUnrelatedScreenAndNavigateToVerifyEmail(
    backStack: NavBackStack<NavKey>,
    email: String?
) {
    while (backStack.size > 1 && backStack.firstOrNull() !in ALLOWED_WHEN_UNVERIFIED_USER) {
        backStack.removeFirstOrNull()
    }

    if (backStack.lastOrNull() !is VerifyEmail) {
        backStack.add(VerifyEmail(email))
    }
    if (backStack.firstOrNull() !in ALLOWED_WHEN_UNVERIFIED_USER) {
        backStack.removeFirstOrNull()
    }
}