package com.example.donemate.ui.screens.inspiration

import android.content.Intent
import android.content.Intent.ACTION_SEND
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwipeDown
import androidx.compose.material.icons.filled.SwipeUp
import androidx.compose.material3.Button
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.donemate.data.model.Quote
import com.example.donemate.ui.component.CustomErrorIcon
import com.example.donemate.ui.component.CustomLoadingIndicator
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun InspirationScreen(viewModel: InspirationViewModel) {
    val likedQuotesUiState by viewModel.likedQuotesUiState.collectAsStateWithLifecycle()
    val pagingItems = viewModel.pagingData.collectAsLazyPagingItems()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()

    val sourceRefresh = pagingItems.loadState.source.refresh
    val sourceAppend = pagingItems.loadState.source.append

    val mediatorRefresh = pagingItems.loadState.mediator?.refresh
    val mediatorAppend = pagingItems.loadState.mediator?.append


    //TODO: This logic should be in the quotes repository because it's not UI logic. Cancel the coroutine with the syncing when there's no internet connection and mark syncedOnce to true. If there's no internet initially mark syncedOnce as true. I think the Job class must be involved.
    // Learn async programming in more depth and then fix. Inject network monitor to the repository
    LaunchedEffect(isOnline) {
        if (isOnline == true) {
            while (true) {
                viewModel.sync()
                delay(3_500.milliseconds)
            }
        } else if (isOnline == false){
            viewModel.markSyncedOnceAsTrue()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 13.dp)
    ) {
        if (mediatorRefresh is LoadState.Loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CustomLoadingIndicator()
            }
        } else if (
            sourceRefresh is LoadState.Error ||
            mediatorRefresh is LoadState.Error
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CustomErrorIcon()
                Text(
                    "Something went wrong. Check internet connection",
                    modifier = Modifier.width(200.dp)
                )
                if (sourceRefresh !is LoadState.Error) {
                    Button(onClick = { pagingItems.retry() }) {
                        Text("Retry")
                    }
                }
            }

        } else {
            var expanded by rememberSaveable { mutableStateOf(false) }

            // TODO: Find a built-in way to show a dropdown menu
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .padding(top = 20.dp)
                        .clickable { expanded = true }
                        .align(Alignment.Center)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(15.dp)
                            )
                    ) {
                        Icon(
                            imageVector = if (likedQuotesUiState.shouldShowLikedQuotes) Icons.Default.Favorite else Icons.Default.Explore,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 26.dp)
                                .align(Alignment.CenterStart)
                        )
                        Text(
                            text = if (likedQuotesUiState.shouldShowLikedQuotes) "Liked" else "Explore",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.Center)
                        )

                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Dropdown",
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 5.dp)
                        )
                    }

                    // Read about DropdownMenu
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.width(200.dp),
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Liked",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                viewModel.showLikedQuotes()
                                expanded = false
                            },
                            enabled = !likedQuotesUiState.shouldShowLikedQuotes,
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Explore",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Explore,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                viewModel.showAllQuotes()
                                expanded = false
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            enabled = likedQuotesUiState.shouldShowLikedQuotes
                        )
                    }
                }
            }

            if (likedQuotesUiState.shouldShowLikedQuotes) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    likedQuotesUiState.likedQuotesState.let {
                        when (it) {
                            is LikedQuotesState.Loading -> {
                                CustomLoadingIndicator()
                            }

                            is LikedQuotesState.Error -> {
                                CustomErrorIcon()
                                Text("Something went wrong.")
                            }

                            is LikedQuotesState.Success -> {
                                val pagerState =
                                    rememberPagerState(pageCount = { it.likedQuotes.size })
                                VerticalPager(
                                    state = pagerState,
                                    pageSize = threePagesPerViewport,
                                    modifier = Modifier.padding(top = 25.dp),
                                    pageSpacing = 10.dp
                                ) { pageIndex ->
                                    val quote = it.likedQuotes[pageIndex]
                                    QuoteCard(
                                        item = quote,
                                        pageIndex = pageIndex,
                                        currentPage = pagerState.currentPage,
                                        pageCount = pagerState.pageCount,
                                        onLikeClicked = viewModel::markAsLiked,
                                        onUnlikeClicked = viewModel::unlike
                                    )
                                }
                            }
                        }
                    }
                }

            } else {
                val pagerState = rememberPagerState(pageCount = {
                    if (
                        shouldShowLoadingFooter(
                            sourceAppend,
                            mediatorAppend
                        )
                        || shouldShowErrorFooter(
                            sourceAppend,
                            mediatorAppend
                        )
                    ) {
                        pagingItems.itemCount + 1
                    } else {
                        pagingItems.itemCount
                    }
                })
                VerticalPager(
                    state = pagerState,
                    pageSize = threePagesPerViewport,
                    modifier = Modifier.padding(top = 25.dp),
                    pageSpacing = 10.dp
                ) { pageIndex ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (shouldShowErrorFooter(
                                sourceAppend,
                                mediatorAppend
                            ) && pageIndex == pagingItems.itemCount
                        ) {
                            Text(
                                "Something went wrong. Check internet connection",
                                modifier = Modifier.width(200.dp)
                            )
                            Button(onClick = { pagingItems.retry() }) {
                                Text("Retry")
                            }
                        } else if (
                            (sourceAppend is LoadState.Loading ||
                                    mediatorAppend is LoadState.Loading)
                            && pageIndex == pagingItems.itemCount
                        ) {
                            CustomLoadingIndicator()
                        } else {
                            val item =
                                if (pageIndex < pagingItems.itemCount) pagingItems[pageIndex] else null
                            if (item != null) {
                                QuoteCard(
                                    item = item,
                                    pageIndex = pageIndex,
                                    currentPage = pagerState.currentPage,
                                    pageCount = pagerState.pageCount,
                                    onLikeClicked = viewModel::markAsLiked,
                                    onUnlikeClicked = viewModel::unlike
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuoteCard(
    item: Quote,
    pageIndex: Int,
    onLikeClicked: (String) -> Unit,
    onUnlikeClicked: (String) -> Unit,
    pageCount: Int,
    currentPage: Int
) {
    Card(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (pageIndex == 1 && currentPage == 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .wrapContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Spacer(Modifier.height(15.dp))

                        Icon(
                            imageVector = Icons.Default.SwipeDown,
                            contentDescription = "",
                            modifier = Modifier.size(45.dp)
                        )

                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Swipe down for previous quote",
                            textAlign = TextAlign.Center
                        )
                    }

                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.fillMaxWidth(0.9f)) {
                        Column(
                            modifier = Modifier.wrapContentSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null,
                                modifier = Modifier.size(50.dp)
                            )
                            Text(
                                item.content,
                                fontSize = 30.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 30.sp
                            )
                            Text(
                                item.author,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )

                            // TODO: Not sure it's good to get context here. Read about this
                            val context = LocalContext.current

                            Row(modifier = Modifier.padding(top = 14.dp)) {
                                // TODO: Add tooltip

                                IconButton(
                                    onClick = {
                                        val sendIntent: Intent =
                                            Intent().apply {
                                                action = ACTION_SEND
                                                putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    "“${item.content}”\n\n― ${item.author}"
                                                )
                                                type = "text/plain"
                                            }


                                        val shareIntent =
                                            Intent.createChooser(
                                                sendIntent,
                                                null
                                            )
                                        context.startActivity(
                                            shareIntent
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null,
                                        modifier = Modifier.size(27.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        if (item.isLiked) {
                                            onUnlikeClicked(item.id)
                                        } else {
                                            onLikeClicked(item.id)
                                        }
                                    }
                                ) {
                                    if (item.isLiked) {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = null,
                                            modifier = Modifier.size(31.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.FavoriteBorder,
                                            contentDescription = null,
                                            modifier = Modifier.size(31.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (currentPage == 0 && pageCount > 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.wrapContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Swipe up for next quote",
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(8.dp))
                        Icon(
                            imageVector = Icons.Default.SwipeUp,
                            contentDescription = "",
                            modifier = Modifier.size(45.dp)
                        )
                        Spacer(Modifier.height(15.dp))
                    }
                }
            }
        }
    }
}

private val threePagesPerViewport = object : PageSize {
    override fun Density.calculateMainAxisPageSize(
        availableSpace: Int,
        pageSpacing: Int
    ): Int {
        return (availableSpace * 0.9).roundToInt()
    }
}

fun shouldShowErrorFooter(
    sourceAppend: LoadState,
    mediatorAppend: LoadState?
): Boolean {
    return sourceAppend is LoadState.Error ||
            mediatorAppend is LoadState.Error
}

fun shouldShowLoadingFooter(
    sourceAppend: LoadState,
    mediatorAppend: LoadState?
): Boolean {
    return sourceAppend is LoadState.Loading ||
            mediatorAppend is LoadState.Loading
}
