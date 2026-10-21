package com.example.donemate

import android.net.Uri
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
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
import com.example.donemate.ui.screens.archive.ArchiveScreen
import com.example.donemate.ui.screens.archive.ArchiveViewModel
import com.example.donemate.ui.screens.update_email.UpdateEmailScreen
import com.example.donemate.ui.screens.update_email.UpdateEmailViewModel
import com.example.donemate.ui.screens.update_password.UpdatePasswordScreen
import com.example.donemate.ui.screens.update_password.UpdatePasswordViewModel
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
import kotlin.collections.contains
import kotlin.collections.removeLastOrNull

@Serializable
data object SignUp : NavKey

@Serializable
enum class VerifyEmailPreviousEntry {
    SIGN_IN,
    SIGN_UP,
    LINK_EMAIL,
    UPDATE_EMAIL,
}

@Serializable
data object SignIn : NavKey


@Serializable
data class VerifyEmail(
    val emailToVerify: String,
    val previousEntry: VerifyEmailPreviousEntry
) : NavKey

@Serializable
data object UpdatePassword : NavKey

@Serializable
data object Tasks : TopLevelRoute() {
    override val icon = Icons.Default.Menu
}

@Serializable
data object Archive : NavKey

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
data object UpdateEmail : NavKey

@Serializable
data object Inspiration : TopLevelRoute() {
    override val icon = Icons.Default.Lightbulb
}


private val TOP_LEVEL_ROUTES: List<TopLevelRoute> = listOf(Tasks, Inspiration, Settings)

@Composable
fun DoneMateApp(
    userProfile: User?,
    isUserVerified: Boolean?,
    onOfflineDialogDismiss: () -> Unit,
    wasOfflineDialogShown: Boolean
) {
    var sessionCapturedUri by rememberSaveable { mutableStateOf<String?>(null) }
    val backStack = rememberNavBackStack(
        when (isUserVerified) {
            null -> SignIn
            true -> Tasks
            false -> {
                userProfile?.email.let {
                    // TODO: Consider combining isUserVerified into userProfile in order to eliminate checking for null userProfile when isUserVerified is false
                    if (it == null) SignIn
                    else {
                        VerifyEmail(
                            emailToVerify = it,
                            previousEntry = VerifyEmailPreviousEntry.SIGN_IN
                        )
                    }
                }
            }
        }
    )

    val currentDestination = backStack.lastOrNull()
    val snackBarHostState = remember { SnackbarHostState() }

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
            if (currentDestination in TOP_LEVEL_ROUTES) {
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
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            transitionSpec = {
                EnterTransition.None togetherWith ExitTransition.None
            },
            popTransitionSpec = {
                EnterTransition.None togetherWith ExitTransition.None
            },
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 7.dp),
            entryProvider = entryProvider {
                entry<SignIn> {
                    val viewModel = hiltViewModel<SignInViewModel>()

                    SignInScreen(
                        viewModel = viewModel,
                        onNavigateToResetPassword = {
                            backStack.add(ResetPassword)
                        },
                        onNavigateToSignUp = {
                            backStack.add(SignUp)
                        },
                        onNavigateToTasks = {
                            setEntryAsRoot(backStack, Tasks)
                        },
                        onNavigateToVerifyEmail = {
                            deleteEntriesExceptVerifyEmail(
                                backStack,
                                VerifyEmailPreviousEntry.SIGN_IN, it
                            )
                        },
                        onShowSnackbar = { message: String ->
                            snackBarHostState.showSnackbar(message)
                        }
                    )
                }
                entry<SignUp> {
                    val viewModel = hiltViewModel<SignUpViewModel>()

                    SignUpScreen(
                        onNavigateToSignIn = { backStack.add(SignIn) },
                        viewModel = viewModel,
                        onNavigateToTasks = {
                            setEntryAsRoot(backStack, Tasks)
                        },
                        onNavigateToVerifyEmail = {
                            deleteEntriesExceptVerifyEmail(
                                backStack,
                                VerifyEmailPreviousEntry.SIGN_UP, it
                            )
                        },
                        onShowSnackbar = { message: String ->
                            snackBarHostState.showSnackbar(message)
                        }
                    )
                }
                entry<Tasks> {
                    val viewModel = hiltViewModel<TasksViewModel>()

                    TasksScreen(
                        onNavigateToAdd = {
                            backStack.add(
                                Task(
                                    id = null
                                )
                            )
                        },
                        onNavigateToEdit = { id ->
                            backStack.add(
                                Task(
                                    id = id
                                )
                            )
                        },
                        viewModel = viewModel,
                        onNavigateToArchive = {
                            backStack.add(Archive)
                        },
                        wasOfflineDialogShown = wasOfflineDialogShown,
                        onOfflineDialogDismiss = onOfflineDialogDismiss
                    )
                }
                entry<Task> { key ->
                    val viewModel = hiltViewModel<TaskViewModel>()

                    TaskScreen(
                        onNavigateToTasks = {
                            backStack.removeLastOrNull()
                        },
                        onNavigateToPermission = { taskTitle ->
                            backStack.add(Permission(taskTitle))
                        },
                        onNavigateToPreview = { taskTitle ->
                            backStack.add(Preview(taskTitle))
                        },
                        id = key.id,
                        cameraImageUri = sessionCapturedUri ?: key.uri,
                        viewModel = viewModel
                    )
                }
                entry<Settings> {
                    val viewModel = hiltViewModel<SettingsViewModel>()

                    SettingsScreen(
                        onNavigateToLinkEmail = {
                            backStack.add(LinkEmail)
                        },
                        onNavigateToUpdatePassword = {
                            backStack.add(UpdatePassword)
                        },
                        onNavigateToUpdateEmail = {
                            backStack.add(UpdateEmail)
                        },
                        viewModel = viewModel,
                        onShowSnackbar = { message: String ->
                            snackBarHostState.showSnackbar(message)
                        },
                        onNavigateToSignUp = {
                            setEntryAsRoot(backStack, SignUp)
                        }
                    )
                }
                entry<LinkEmail> {
                    val viewModel = hiltViewModel<LinkEmailViewModel>()

                    LinkEmailScreen(
                        viewModel = viewModel,
                        onShowSnackbar = { message: String ->
                            snackBarHostState.showSnackbar(message)
                        },
                        onNavigateToVerifyEmail = {
                            deleteEntriesExceptVerifyEmail(
                                backStack,
                                VerifyEmailPreviousEntry.LINK_EMAIL, it
                            )
                        }
                    )
                }
                entry<ResetPassword> {
                    val viewModel = hiltViewModel<ResetPasswordViewModel>()

                    ResetPasswordScreen(
                        viewModel = viewModel,
                        onShowSnackbar = { message: String ->
                            snackBarHostState.showSnackbar(message)
                        }
                    )
                }
                entry<UpdatePassword> {
                    val viewModel = hiltViewModel<UpdatePasswordViewModel>()

                    UpdatePasswordScreen(
                        viewModel = viewModel,
                        onShowSnackbar = { message: String ->
                            snackBarHostState.showSnackbar(message)
                        },
                        onNavigateToSettings = {
                            backStack.removeLastOrNull()
                        },
                        onNavigateBack = {
                            backStack.removeLastOrNull()
                        }
                    )
                }
                entry<Permission> { key ->
                    PermissionScreen(
                        onNavigateToPreview = {
                            backStack.add(Preview(key.taskTitle))
                            backStack.removeAt(backStack.size - 2)
                        }
                    )
                }
                entry<Preview> { key ->
                    val viewModel = hiltViewModel<PreviewViewModel>()

                    PreviewScreen(
                        taskTitle = key.taskTitle,
                        viewModel = viewModel,
                        onNavigateToTask = { uri: Uri ->
                            sessionCapturedUri = uri.toString()
                            backStack.removeLastOrNull()
                        })
                }
                entry<Inspiration> {
                    val viewModel = hiltViewModel<InspirationViewModel>()

                    InspirationScreen(viewModel = viewModel)
                }
                entry<UpdateEmail> {
                    val viewModel = hiltViewModel<UpdateEmailViewModel>()

                    UpdateEmailScreen(
                        viewModel = viewModel,
                        onShowSnackbar = { message: String ->
                            snackBarHostState.showSnackbar(message)
                        },
                        onNavigateToVerifyEmail = {
                            deleteEntriesExceptVerifyEmail(
                                backStack,
                                VerifyEmailPreviousEntry.UPDATE_EMAIL, it
                            )
                        }
                    )
                }
                entry<VerifyEmail> { key ->
                    val viewModel = hiltViewModel<VerifyEmailViewModel>()

                    VerifyEmailScreen(
                        viewModel = viewModel,
                        onNavigateToTasks = {
                            backStack.add(Tasks)
                        },
                        onNavigateBack = {
                            val previousEntry = when (key.previousEntry) {
                                VerifyEmailPreviousEntry.SIGN_IN -> SignIn
                                VerifyEmailPreviousEntry.SIGN_UP -> SignUp
                                VerifyEmailPreviousEntry.LINK_EMAIL -> LinkEmail
                                VerifyEmailPreviousEntry.UPDATE_EMAIL -> UpdateEmail
                            }
                            backStack.add(previousEntry)
                            while (backStack.size > 1) backStack.removeFirstOrNull()
                        },
                        emailToVerify = key.emailToVerify,
                        onNavigateToSignUp = {
                            backStack.add(SignUp)
                        }
                    )
                }
                entry<Archive> {
                    val viewModel = hiltViewModel<ArchiveViewModel>()

                    ArchiveScreen(
                        onNavigateToEdit = { id ->
                            backStack.add(
                                Task(
                                    id = id
                                )
                            )
                        },
                        viewModel = viewModel,
                        onNavigateToTasks = {
                            backStack.removeLastOrNull()
                        },
                        wasOfflineDialogShown = wasOfflineDialogShown,
                        onOfflineDialogDismiss = onOfflineDialogDismiss,
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

fun deleteEntriesExceptVerifyEmail(
    backStack: NavBackStack<NavKey>,
    previousEntry: VerifyEmailPreviousEntry,
    email: String
) {
    while (backStack.size > 1) backStack.removeFirstOrNull()
    if (backStack.lastOrNull() !is VerifyEmail) {
        backStack.add(VerifyEmail(email, previousEntry))
    }
    backStack.removeFirstOrNull()
}
