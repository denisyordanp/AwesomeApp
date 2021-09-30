package com.okhome.awesomeapp.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.okhome.awesomeapp.databinding.PhotoGridViewHolderBinding
import com.okhome.awesomeapp.databinding.PhotoListViewHolderBinding
import com.okhome.awesomeapp.module.local.Photo

class PhotosAdapter : PagingDataAdapter<Photo, PhotosAdapter.PhotoViewHolder>(PHOTO_COMPARATOR) {

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

    var onClick: ((Int) -> Unit)? = null
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

    abstract class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(dataPhoto: Photo?, onClick: ((Int) -> Unit)?)
    }

    class PhotoGridViewHolder(private val binding: PhotoGridViewHolderBinding) :
        PhotoViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): PhotoGridViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = PhotoGridViewHolderBinding.inflate(inflater, parent, false)
                return PhotoGridViewHolder(binding)
            }
        }

        override fun bind(dataPhoto: Photo?, onClick: ((Int) -> Unit)?) {
            dataPhoto?.let {
                binding.apply {
                    photo = it
                    root.setOnClickListener {
                        onClick?.invoke(dataPhoto.id)
                    }
                    invalidateAll()
                }
            }
        }
    }

    class PhotoListViewHolder(private val binding: PhotoListViewHolderBinding) :
        PhotoViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): PhotoListViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = PhotoListViewHolderBinding.inflate(inflater, parent, false)
                return PhotoListViewHolder(binding)
            }
        }

        override fun bind(dataPhoto: Photo?, onClick: ((Int) -> Unit)?) {
            dataPhoto?.let {
                binding.apply {
                    photo = it
                    root.setOnClickListener {
                        onClick?.invoke(dataPhoto.id)
                    }
                    invalidateAll()
                }
            }
        }
    }
}

enum class ViewType {
    GRID, LIST
}