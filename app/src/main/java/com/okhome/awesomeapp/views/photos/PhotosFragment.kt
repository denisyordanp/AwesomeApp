package com.okhome.awesomeapp.views.photos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.okhome.awesomeapp.databinding.FragmentPhotosBinding
import com.okhome.awesomeapp.views.photos.adapters.PhotosAdapter
import com.okhome.awesomeapp.views.photos.adapters.PhotosLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalPagingApi
@AndroidEntryPoint
class PhotosFragment : Fragment() {

    private var binding: FragmentPhotosBinding? = null
    private val photosViewModel by viewModels<PhotosViewModel>()
    private val photoAdapter: PhotosAdapter = PhotosAdapter()

    private var loadJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null) {
            binding = FragmentPhotosBinding.inflate(inflater, container, false).setupBindings()
        }
        return binding!!.root
    }

    private fun FragmentPhotosBinding.setupBindings(): FragmentPhotosBinding {
        return this.apply {
            viewModel = photosViewModel
            lifecycleOwner = viewLifecycleOwner

            adapter = photoAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupListener()
        loadPhotos()
    }

    private fun setupAdapter() {
        binding?.run {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = photoAdapter.withLoadStateHeaderAndFooter(
                header = PhotosLoadStateAdapter { photoAdapter.retry() },
                footer = PhotosLoadStateAdapter { photoAdapter.retry() }
            )
            photoAdapter.addLoadStateListener {
                photosViewModel.updateLoadState(it)
            }
            photoAdapter.onClick = {
                toDetailPhoto(it)
            }
        }
    }

    private fun setupListener() {
        binding?.tryAgainButton?.setOnClickListener {
            photoAdapter.retry()
        }
    }

    private fun toDetailPhoto(id: Long) {
        val action = PhotosFragmentDirections.actionPhotosFragmentToDetailFragment(id)
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