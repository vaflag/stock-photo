package com.vf.photobank.util

import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar

fun showSnackBar(view: View, message: Int, actionMessage: Int, action: (View) -> Unit) {
    Snackbar.make(
        view,
        message,
        Snackbar.LENGTH_SHORT
    ).setAction(actionMessage) {
        action.invoke(it)
    }.setActionTextColor(Color.WHITE)
        .show()
}
