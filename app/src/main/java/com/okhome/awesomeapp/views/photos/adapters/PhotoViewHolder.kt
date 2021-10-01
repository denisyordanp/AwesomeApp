package com.okhome.awesomeapp.views.photos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.okhome.awesomeapp.databinding.PhotoGridViewHolderBinding
import com.okhome.awesomeapp.databinding.PhotoListViewHolderBinding
import com.okhome.awesomeapp.module.local.Photo

abstract class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(dataPhoto: Photo?, onClick: ((Long) -> Unit)?)
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

    override fun bind(dataPhoto: Photo?, onClick: ((Long) -> Unit)?) {
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

    override fun bind(dataPhoto: Photo?, onClick: ((Long) -> Unit)?) {
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