package com.vf.photobank.util

import android.view.View

fun View.show(animated: Boolean = false) {
    if (animated) {
        animate()
            .setDuration(
                context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
            )
            .alpha(1f)
            .withStartAction {
                alpha = 0f
                visibility = View.VISIBLE
            }
            .start()
    } else {
        visibility = View.VISIBLE
    }
}

fun View.hide(animated: Boolean = false) {
    if (animated) {
        animate()
            .setDuration(
                context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
            )
            .alpha(0f)
            .withEndAction { visibility = View.GONE }
            .start()
    } else {
        visibility = View.GONE
    }
}
