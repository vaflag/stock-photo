package com.vf.photobank.ui.search

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
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
            this.isVisible -> displayText()
            else -> this.hide(animated = true)
        }
    }

    private fun displayLoadingSpinner() {
        if (visibility != View.VISIBLE) {
            show(animated = true)
        }
        text_view_go.hide()
        progress_bar.show()
    }

    private fun displayText() {
        if (visibility != View.VISIBLE) {
            show(animated = true)
        }
        progress_bar.hide()
        text_view_go.show()
    }
}
