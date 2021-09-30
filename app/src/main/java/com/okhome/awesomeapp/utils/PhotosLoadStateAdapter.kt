package com.okhome.awesomeapp.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.okhome.awesomeapp.databinding.PhotoLoadStateFooterViewBinding

class PhotosLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<PhotosLoadStateAdapter.PhotosLoadStateViewHolder>() {

    override fun onBindViewHolder(holder: PhotosLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): PhotosLoadStateViewHolder {
        return PhotosLoadStateViewHolder.create(parent, retry)
    }

    class PhotosLoadStateViewHolder(
        private val binding: PhotoLoadStateFooterViewBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMsg.text = loadState.error.localizedMessage
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.errorMsg.isVisible = loadState is LoadState.Error
        }

        companion object {
            fun create(parent: ViewGroup, retry: () -> Unit): PhotosLoadStateViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = PhotoLoadStateFooterViewBinding.inflate(inflater, parent, false)
                return PhotosLoadStateViewHolder(binding, retry)
            }
        }
    }

}