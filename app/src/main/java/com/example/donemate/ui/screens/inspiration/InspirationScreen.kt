package com.example.donemate.ui.screens.inspiration

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.core.net.toUri

@Composable
fun InspirationScreen (vm: InspirationViewModel){
    val items = vm.flow.collectAsLazyPagingItems()

    val sourceRefresh = items.loadState.source.refresh
    val sourceAppend  = items.loadState.source.append

    val mediatorRefresh = items.loadState.mediator?.refresh
    val mediatorAppend  = items.loadState.mediator?.append

    val isInitialEmpty = items.itemCount == 0 &&
            sourceRefresh is LoadState.Loading &&
            (mediatorRefresh == null || mediatorRefresh is LoadState.Loading)

    val context = LocalContext.current

    Column {
        Row {
            Column {
                Text("Inspirational songs")
                // I think I shouldn't use intent here \: I should stick to Compose
                Button(onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "https://www.youtube.com/playlist?list=PLhGO2bt0EkwvRUioaJMLxrMNhU44lRWg8".toUri()
                    )
                    context.startActivity(intent)
                }) {
                    Text("Go to playlist (link)")
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(
            thickness = 1.dp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isInitialEmpty) {
                    item() {
                        Column() {
                            CircularProgressIndicator(
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }

                items(count = items.itemCount) { index ->
                    val item = items[index]
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
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
                        CircularProgressIndicator(
                            modifier =
                                Modifier.fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
                if (sourceAppend is LoadState.Error ||
                    mediatorAppend is LoadState.Error
                ) {
                    item {
                        Text("Error")
                    }
                }
            }
        }
    }
}