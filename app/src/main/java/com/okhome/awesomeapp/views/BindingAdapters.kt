package com.okhome.awesomeapp.views

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import coil.load
import com.facebook.shimmer.ShimmerFrameLayout
import com.okhome.awesomeapp.R

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
    fun setupShimmerLoading(view: ShimmerFrameLayout, isLoading: Boolean) {
        if (isLoading)
            view.startShimmer()
        else
            view.stopShimmer()
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
            state.refresh is LoadState.Error -> {
                errorText.text = view.context.getString(R.string.something_wrong)
                view.isVisible = true
            }
            else -> {
                view.isVisible = false
            }
        }
    }
}