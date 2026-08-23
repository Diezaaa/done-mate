package com.example.donemate.ui.screens.inspiration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.donemate.ui.component.CustomErrorIcon
import com.example.donemate.ui.component.CustomLoadingIndicator

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InspirationScreen(vm: InspirationViewModel) {
    val items = vm.flow.collectAsLazyPagingItems()
    val sourceRefresh = items.loadState.source.refresh
    val sourceAppend = items.loadState.source.append

    val mediatorRefresh = items.loadState.mediator?.refresh
    val mediatorAppend = items.loadState.mediator?.append

    val isSyncing = mediatorRefresh is LoadState.Loading



    Column {
        Row {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isSyncing) {
                    item {
                        Column {
                            CustomLoadingIndicator()
                        }
                    }
                }

                items(count = items.itemCount) { index ->
                    val item = items[index]
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null
                            )
                            Text("${item?.content}", fontSize = 20.sp)
                            Text("${item?.author}", fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (sourceAppend is LoadState.Loading ||
                    mediatorAppend is LoadState.Loading
                ) {
                    item {
                        CustomLoadingIndicator()
                    }
                }
                if (
                    sourceRefresh is LoadState.Error ||
                    mediatorRefresh is LoadState.Error
                ) {
                    item {
                        CustomErrorIcon()
                        Text(
                            "Something went wrong. Check internet connection",
                            modifier = Modifier.width(200.dp)
                        )
                        if (sourceRefresh !is LoadState.Error) {
                            Button(onClick = { items.retry() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                if (
                    sourceAppend is LoadState.Error ||
                    mediatorAppend is LoadState.Error
                ) {
                    item {
                        Text(
                            "Something went wrong. Check internet connection",
                            modifier = Modifier.width(200.dp)
                        )
                        Button(onClick = { items.retry() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}