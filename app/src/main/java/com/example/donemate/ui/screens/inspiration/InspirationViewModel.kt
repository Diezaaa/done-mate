package com.example.donemate.ui.screens.inspiration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.map
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.donemate.data.local.db.QuotesDao
import com.example.donemate.data.local.db.RemoteKeyDao
import com.example.donemate.data.local.model.asExternalModel
import com.example.donemate.data.paging.InspirationRemoteMediator
import com.example.donemate.domain.local.QuotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// What does () in the ViewModel() mean?
@HiltViewModel
class InspirationViewModel @Inject constructor(
    private val quotesDao: QuotesDao,
    private val remoteKeyDao: RemoteKeyDao,
    private val quotesRepository: QuotesRepository
) : ViewModel() {
    @OptIn(ExperimentalPagingApi::class)
    val flow = Pager(
        config = PagingConfig(pageSize = 20),
        remoteMediator = InspirationRemoteMediator(
            quotesDao = quotesDao,
            remoteKeyDao = remoteKeyDao,
            quotesRepository = quotesRepository
        )
    ) {
        quotesDao.pagingSource()
    }.flow
        .map{ pagingData ->
            pagingData.map { quoteEntity -> quoteEntity.asExternalModel()}
        }
        .cachedIn(viewModelScope)

}