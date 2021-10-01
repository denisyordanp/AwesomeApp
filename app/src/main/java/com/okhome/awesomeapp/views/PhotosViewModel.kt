package com.okhome.awesomeapp.views

import androidx.lifecycle.*
import androidx.paging.*
import com.okhome.awesomeapp.data.repository.PhotosRepository
import com.okhome.awesomeapp.module.local.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class PhotosViewModel
@Inject constructor(
    private val repository: PhotosRepository
) : ViewModel() {

    private val _loadState = MutableLiveData<CombinedLoadStates>()
    val loadState: LiveData<CombinedLoadStates> = _loadState
    val isLoading: LiveData<Boolean> = Transformations.map(loadState) {
        it.mediator?.refresh is LoadState.Loading
    }

    fun requestPhotos(): Flow<PagingData<Photo>> {
        return repository.getPhotosStream()
            .cachedIn(viewModelScope)
    }

    fun updateLoadState(loadState: CombinedLoadStates) {
        _loadState.value = loadState
    }
}