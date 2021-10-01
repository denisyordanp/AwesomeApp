package com.okhome.awesomeapp.views.photos.adapters

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.okhome.awesomeapp.module.local.Photo

class PhotosAdapter : PagingDataAdapter<Photo, PhotoViewHolder>(PHOTO_COMPARATOR) {

    companion object {

        val DEFAULT_VIEW_TYPE = ViewType.LIST

        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean =
                oldItem == newItem
        }
    }

    var onClick: ((Long) -> Unit)? = null
    var showViewType: ViewType = DEFAULT_VIEW_TYPE

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = getItem(position)
        holder.bind(photo, onClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return when (showViewType) {
            ViewType.LIST -> PhotoListViewHolder.from(parent)
            ViewType.GRID -> PhotoGridViewHolder.from(parent)
        }
    }
}

enum class ViewType {
    GRID, LIST
}