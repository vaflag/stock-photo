package com.vf.photobank.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.vf.photobank.R
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Home fragment of the application.
 *
 * The home fragment displays a list of image suggestions.
 */
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(
            view,
            savedInstanceState
        )
        viewModel.photoPage.observe(
            viewLifecycleOwner,
            Observer {
                // Update UI
            }
        )
        viewModel.getCuratedPhotos()
    }
}
