package com.okhome.awesomeapp.views

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.okhome.awesomeapp.R
import com.okhome.awesomeapp.views.photos.adapters.PhotosAdapter

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("loadImage")
    fun loadImageUrl(view: ImageView, url: String?) {
        val context = view.context
        if (url.isNullOrEmpty()) {
            val errorImage =
                ResourcesCompat.getDrawable(context.resources, R.drawable.ic_broken_image, null)
            view.setImageDrawable(errorImage)
        } else {
            view.load(url) {
                crossfade(true)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("isShimmerLoading")
    fun setupShimmerLoading(view: ShimmerFrameLayout, state: CombinedLoadStates) {
        val isLoading = state.refresh is LoadState.Loading
        view.isVisible = isLoading
        if (isLoading) {
            view.startShimmer()
        } else {
            view.hideShimmer()
        }
    }

    @JvmStatic
    @BindingAdapter("setContentVisibility")
    fun setupContent(view: RecyclerView, state: CombinedLoadStates) {
        val isVisible = state.mediator?.refresh is LoadState.NotLoading
        view.isVisible = isVisible
    }

    @JvmStatic
    @BindingAdapter("loadState", "stateAdapter", requireAll = true)
    fun setupErrorData(view: ConstraintLayout, state: CombinedLoadStates, adapter: PhotosAdapter) {
        val isEmpty = state.refresh is LoadState.NotLoading && adapter.itemCount == 0
        val errorText = view.findViewById<TextView>(R.id.error_textView)

        when {
            isEmpty -> {
                errorText.text = view.context.getString(R.string.data_not_found)
                view.isVisible = true
            }
            state.mediator?.refresh is LoadState.Error -> {
                errorText.text = view.context.getString(R.string.something_wrong)
                view.isVisible = true
            }
            else -> {
                view.isVisible = false
            }
        }
    }

    @JvmStatic
    @BindingAdapter("setContrastTitle")
    fun setupTitleColor(view: CollapsingToolbarLayout, avgColor: String?) {
        try {
            avgColor?.let { view.setExpandedTitleColor(getContrastColor(it)) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getContrastColor(color: String): Int {
        val r = color.substring(0, 2).toInt(16) // 16 for hex
        val g = color.substring(2, 4).toInt(16) // 16 for hex
        val b = color.substring(4, 6).toInt(16) // 16 for hex
        val yiq = ((r * 299) + (g * 587) + (b * 114)) / 1000;
        return if (yiq >= 128) Color.BLACK else Color.WHITE
    }
}