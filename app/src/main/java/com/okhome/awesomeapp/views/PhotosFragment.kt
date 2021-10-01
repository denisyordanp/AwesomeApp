package com.okhome.awesomeapp.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import com.okhome.awesomeapp.databinding.FragmentPhotosBinding
import com.okhome.awesomeapp.utils.PhotosLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalPagingApi
@AndroidEntryPoint
class PhotosFragment : Fragment() {

    private lateinit var binding: FragmentPhotosBinding
    private val photosViewModel by viewModels<PhotosViewModel>()
    private lateinit var photoAdapter: PhotosAdapter

    private var loadJob: Job? = null

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

    private fun setupAdapter() {
        val loadState = PhotosLoadStateAdapter { photoAdapter.retry() }
        photoAdapter.withLoadStateHeaderAndFooter(
            header = loadState,
            footer = loadState
        )
        photoAdapter.addLoadStateListener {
            photosViewModel.updateLoadState(it)
        }
        photoAdapter.onClick = {
            toDetailPhoto(id)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        loadPhotos()
    }

    private fun setupListener() {
        binding.tryAgainButton.setOnClickListener {
            photoAdapter.retry()
        }
    }

    private fun toDetailPhoto(id: Int) {
        val action = PhotosFragmentDirections.actionPhotosFragmentToDetailFragment(id.toLong())
        findNavController().navigate(action)
    }

    private fun loadPhotos() {
        loadJob?.cancel()
        loadJob = lifecycleScope.launch {
            photosViewModel.requestPhotos().collectLatest {
                photoAdapter.submitData(it)
            }
        }
    }
}