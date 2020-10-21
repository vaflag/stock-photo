package com.vf.photobank.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.vf.photobank.R
import com.vf.photobank.domain.entity.Photo
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Search fragment of the application.
 *
 * The search fragment allows the user to
 * retrieve a selection of images from a query.
 */
class SearchFragment(
    onPhotoClick: (Photo) -> Unit
) : Fragment() {
    private val viewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.photoPage.observe(
            viewLifecycleOwner,
            Observer {
                // Update UI
            }
        )
    }
}
