package com.okhome.awesomeapp.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.ExperimentalPagingApi
import com.okhome.awesomeapp.databinding.FragmentPhotosBinding
import com.okhome.awesomeapp.utils.PhotosLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPagingApi
@AndroidEntryPoint
class PhotosFragment : Fragment() {

    private lateinit var binding: FragmentPhotosBinding
    private val photosViewModel by viewModels<PhotosViewModel>()
    private lateinit var photoAdapter: PhotosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotosBinding.inflate(inflater, container, false).setupBindings()
        return binding.root
    }

    private fun FragmentPhotosBinding.setupBindings(): FragmentPhotosBinding {
        return this.apply {
            viewModel = photosViewModel
            lifecycleOwner = viewLifecycleOwner

            setupAdapter()
            adapter = photoAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupAdapter() {
        val loadState = PhotosLoadStateAdapter { photoAdapter.retry() }
        photoAdapter.withLoadStateHeaderAndFooter(
            header = loadState,
            footer = loadState
        )
        photoAdapter.addLoadStateListener {
            photosViewModel.updateLoadState(it)
        }
    }
}