package com.example.donemate.ui.screens.inspiration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun InspirationScreen (vm: InspirationViewModel){
    val lazyPagingItems = vm.flow.collectAsLazyPagingItems()

    LazyColumn(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        if (lazyPagingItems.loadState.refresh == LoadState.Loading) {
            item() {
                Column() {
                    CircularProgressIndicator(
                        modifier =
                            Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        items(count = lazyPagingItems.itemCount) { index ->
            val item = lazyPagingItems[index]
            Spacer(modifier = Modifier.height(10.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)) {
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

        if (lazyPagingItems.loadState.append == LoadState.Loading) {
            item {
                CircularProgressIndicator(
                    modifier =
                        Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    }
}