package com.example.donemate.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomLoadingIndicator(){
    val lifecycleOwner = LocalLifecycleOwner.current

    LoadingIndicator(modifier = Modifier.size(80.dp))
}