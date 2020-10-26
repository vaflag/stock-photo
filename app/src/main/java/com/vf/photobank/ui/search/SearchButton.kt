package com.vf.photobank.ui.search

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.vf.photobank.R
import com.vf.photobank.util.hide
import com.vf.photobank.util.show
import kotlinx.android.synthetic.main.layout_search_button.view.*

/**
 * A button that contains either text or a loading spinner.
 */
class SearchButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_search_button, this, true)
    }

    var isLoading = false
        set(value) {
            field = value
            displayContent()
        }

    var isVisible = false
        set(value) {
            field = value
            displayContent()
        }

    /**
     * Displays the content of the button according
     * to its state.
     *
     * Loading state has priority on any other state.
     */
    private fun displayContent() {
        when {
            isLoading -> displayLoadingSpinner()
            isVisible -> displayText()
            else -> this.hide(animated = true)
        }
    }

    private fun displayLoadingSpinner() {
        if (visibility != View.VISIBLE) {
            show(animated = true)
        }
        if (text_view_go.isVisible) text_view_go.hide()
        if (!progress_bar.isVisible) progress_bar.show()
    }

    private fun displayText() {
        if (visibility != View.VISIBLE) {
            show(animated = true)
        }
        if (progress_bar.isVisible) progress_bar.hide()
        if (!text_view_go.isVisible) text_view_go.show()
    }
}
