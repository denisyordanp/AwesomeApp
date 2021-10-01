package com.okhome.awesomeapp.views.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.okhome.awesomeapp.data.repository.PhotosRepository
import com.okhome.awesomeapp.module.local.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@ExperimentalPagingApi
class DetailViewModel
@Inject constructor(
    private val repository: PhotosRepository
) : ViewModel() {

    private val _currentPhoto = MutableLiveData<Photo>()
    val currentPhoto: LiveData<Photo> = _currentPhoto

    fun getPhoto(photoId: Long) = viewModelScope.launch {
        _currentPhoto.value = repository.getPhotoById(photoId)?.generateToLocal()
    }
}