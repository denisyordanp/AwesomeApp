package com.okhome.awesomeapp.views.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.paging.ExperimentalPagingApi
import com.okhome.awesomeapp.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPagingApi
@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var binding: FragmentDetailBinding? = null
    private val args by navArgs<DetailFragmentArgs>()

    private val detailViewModel by viewModels<DetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDetailBinding.inflate(inflater, container, false).setupBindings()
        this.binding = binding
        return binding.root
    }

    private fun FragmentDetailBinding.setupBindings(): FragmentDetailBinding {
        return this.apply {
            viewModel = detailViewModel
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPhoto(args.idPhoto)
    }

    private fun getPhoto(photoId: Long) {
        detailViewModel.getPhoto(photoId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}