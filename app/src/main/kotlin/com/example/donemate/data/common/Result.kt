package com.example.donemate.data.common

import android.database.sqlite.SQLiteFullException
import android.util.Log

// TODO: Read about Kotlin's out and in modifiers. This code is from the Nia sample app
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val message: String) : Result<Nothing>
}

/* TODO: this code may handle coroutines incorrectly.
 Read this https://kotlinlang.org/docs/exception-handling.html#cancellation-and-exceptions and about coroutines in more depth
 */
suspend fun <T> safeCall(
    operation: String,
    repositoryTag: String,
    block: suspend () -> T
): Result<T> {
    return try {
        Result.Success(block())
    }
    catch(sqliteFullException: SQLiteFullException){
        Log.e(repositoryTag, "$operation: ${sqliteFullException.message}")
        return Result.Error("Device ran out of space. Please free up space")
    }
    catch(exception: Exception){
        Log.e(repositoryTag, "$operation: ${exception.message}")
        return Result.Error("Something went wrong. Please try again.")
    }
}
