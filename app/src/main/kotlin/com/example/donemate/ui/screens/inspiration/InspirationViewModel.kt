package com.example.donemate.ui.screens.inspiration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.donemate.data.paging.InspirationRemoteMediator
import com.example.donemate.data.repository.offline_first.QuotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import androidx.paging.map
import com.example.donemate.data.model.Quote
import com.example.donemate.data.model.asExternalModel
import com.example.donemate.data.repository.local.RemoteKeyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import com.example.donemate.data.common.Result
import com.example.donemate.network.NetworkMonitor
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class InspirationViewModel @Inject constructor(
    private val quotesRepository: QuotesRepository,
    private val remoteKeyRepository: RemoteKeyRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {
    // The UI needs PagingData as flow in order to consume pages, so I didn't merge this variable into uiState using combine function
    @OptIn(ExperimentalPagingApi::class)
    val pagingData = Pager(
        config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
            prefetchDistance = 20,
        ),
        remoteMediator = InspirationRemoteMediator(
            quotesRepository = quotesRepository,
            remoteKeyRepository = remoteKeyRepository
        )
    ) {
        quotesRepository.getAllQuotesPagingSource()
    }
        .flow
        .map { pagingData ->
            pagingData.map { quoteEntity -> quoteEntity.asExternalModel() }
        }
        .cachedIn(viewModelScope)


    private val likedQuotesState: StateFlow<LikedQuotesState> = quotesRepository.likedQuotes
        .map { result: Result<List<Quote>> ->
            if (result is Result.Success) {
                LikedQuotesState.Success(result.data)
            } else {
                LikedQuotesState.Error("Something went wrong.")
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LikedQuotesState.Loading
        )

    val isOnline = networkMonitor.isOnline
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val _shouldShowLikedQuotes: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val syncedOnce = MutableStateFlow(false)

    fun markSyncedOnceAsTrue() {
        syncedOnce.value = true
    }

    val likedQuotesUiState: StateFlow<LikedQuotesUiState> =
        combine(
            likedQuotesState,
            _shouldShowLikedQuotes,
            syncedOnce
        ) { likedQuotesState, shouldShowLikedQuotes, syncedOnce ->
            if (!syncedOnce && likedQuotesState is LikedQuotesState.Success && likedQuotesState.likedQuotes.isEmpty()) {
                LikedQuotesUiState(
                    likedQuotesState = LikedQuotesState.Loading,
                    shouldShowLikedQuotes = shouldShowLikedQuotes
                )
            } else {
                LikedQuotesUiState(
                    likedQuotesState = likedQuotesState,
                    shouldShowLikedQuotes = shouldShowLikedQuotes
                )
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = LikedQuotesUiState(
                    shouldShowLikedQuotes = false,
                    likedQuotesState = LikedQuotesState.Loading
                )
            )


    fun markAsLiked(id: String) {
        viewModelScope.launch {
            quotesRepository.markAsLiked(id)
        }
    }

    fun unlike(id: String) {
        viewModelScope.launch {
            quotesRepository.markAsUnliked(id)
        }
    }

    fun showLikedQuotes() {
        _shouldShowLikedQuotes.value = true
    }

    fun showAllQuotes() {
        _shouldShowLikedQuotes.value = false
    }

    fun sync() {
        viewModelScope.launch {
            quotesRepository.sync()
            syncedOnce.value = true
        }
    }
}

data class LikedQuotesUiState(
    val likedQuotesState: LikedQuotesState = LikedQuotesState.Loading,
    val shouldShowLikedQuotes: Boolean = false
)

sealed interface LikedQuotesState {
    data object Loading : LikedQuotesState
    data class Success(val likedQuotes: List<Quote>) : LikedQuotesState
    data class Error(val message: String) : LikedQuotesState
}