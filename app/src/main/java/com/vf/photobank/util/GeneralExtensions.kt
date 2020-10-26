package com.vf.photobank.util

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

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

fun View.showKeyboard() {
    val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun View.hideKeyboard() {
    val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}
