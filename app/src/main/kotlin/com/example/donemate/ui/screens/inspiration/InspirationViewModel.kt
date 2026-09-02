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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import com.example.donemate.data.common.Result
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

@HiltViewModel
class InspirationViewModel @Inject constructor(
    private val quotesRepository: QuotesRepository,
    private val remoteKeyRepository: RemoteKeyRepository,
) : ViewModel() {

    // TODO: URGENT. I should make one uiState that exposes all the things that the screen needs
    @OptIn(ExperimentalPagingApi::class)
    val pagingData = Pager(
        config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
            prefetchDistance = 0
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



    val likedQuotes: StateFlow<List<Quote>> = quotesRepository.likedQuotes
        .map { result ->
            if (result is Result.Success) {
                result.data
            } else {
                emptyList()
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _shouldShowLikedQuotes: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val shouldShowLikedQuotes: StateFlow<Boolean> = _shouldShowLikedQuotes.asStateFlow()

    fun markAsLiked(id: String) {
        viewModelScope.launch {
            quotesRepository.markAsLiked(id)
        }
    }

    fun unlike(id: String){
        viewModelScope.launch {
            quotesRepository.unlike(id)
        }
    }

    fun showLikedQuotes () {
        _shouldShowLikedQuotes.value = true
    }

    fun showAllQuotes() {
        _shouldShowLikedQuotes.value = false
    }

    fun sync() {
        viewModelScope.launch {
            quotesRepository.sync()
        }
    }
}
