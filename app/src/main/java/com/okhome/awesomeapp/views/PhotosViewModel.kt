package com.okhome.awesomeapp.views

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.okhome.awesomeapp.data.repository.PhotosRepository
import com.okhome.awesomeapp.module.local.Photo
import com.okhome.awesomeapp.utils.Event
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
class PhotosViewModel
@Inject constructor(
    private val repository: PhotosRepository
) : ViewModel() {

    private val _detailPhotoEvent = MutableLiveData<Event<Int>>()
    val detailPhotoEvent: LiveData<Event<Int>> = _detailPhotoEvent

    private val _loadStateAdapter = MutableLiveData<CombinedLoadStates>()
    val loadStateAdapter: LiveData<CombinedLoadStates> = _loadStateAdapter

    fun requestPhotos(): Flow<PagingData<Photo>> {
        return repository.getPhotosStream()
            .cachedIn(viewModelScope)
    }

    fun navigateToDetailPhoto(id: Int) {
        _detailPhotoEvent.value = Event(id)
    }

    fun updateLoadState(loadState: CombinedLoadStates) {
        _loadStateAdapter.value = loadState
    }
}