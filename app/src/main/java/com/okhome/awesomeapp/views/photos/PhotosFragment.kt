package com.okhome.awesomeapp.views.photos

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.okhome.awesomeapp.R
import com.okhome.awesomeapp.databinding.FragmentPhotosBinding
import com.okhome.awesomeapp.views.photos.adapters.PhotosAdapter
import com.okhome.awesomeapp.views.photos.adapters.PhotosLoadStateAdapter
import com.okhome.awesomeapp.views.photos.adapters.ViewType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalPagingApi
@AndroidEntryPoint
class PhotosFragment : Fragment() {

    private var binding: FragmentPhotosBinding? = null
    private val photosViewModel by viewModels<PhotosViewModel>()
    private val photoAdapter: PhotosAdapter = PhotosAdapter()

    private var isBack = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isBack = true
        if (binding == null) {
            isBack = false
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
        if (!isBack) loadPhotos()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.grid_view -> {
                applyView(ViewType.GRID)
            }
            R.id.list_view -> {
                applyView(ViewType.LIST)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun ViewType.getLayoutManager(): RecyclerView.LayoutManager {
        return when (this) {
            ViewType.LIST -> {
                LinearLayoutManager(requireContext())
            }
            ViewType.GRID -> {
                GridLayoutManager(requireContext(), 2)
            }
        }
    }

    private fun setupAdapter() {
        binding?.run {
            recyclerView.layoutManager = photoAdapter.showViewType.getLayoutManager()
            recyclerView.adapter = photoAdapter
            photoAdapter.withLoadStateFooter(
                PhotosLoadStateAdapter { photoAdapter.retry() }
            )
            photoAdapter.addLoadStateListener {
                photosViewModel.updateLoadState(it)
            }
            photoAdapter.onClick = {
                toDetailPhoto(it)
            }
        }
    }

    private fun applyView(viewType: ViewType) {
        val currentView = photoAdapter.showViewType
        if (currentView == viewType) return

        binding?.run {
            photoAdapter.showViewType = viewType
            recyclerView.layoutManager = viewType.getLayoutManager()
            recyclerView.adapter = photoAdapter
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
        lifecycleScope.launch {
            photosViewModel.requestPhotos()
                .collectLatest {
                    photoAdapter.submitData(it)
                }
        }
    }
}