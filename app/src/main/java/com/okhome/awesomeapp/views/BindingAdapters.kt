package com.okhome.awesomeapp.views

import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import coil.load
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
}