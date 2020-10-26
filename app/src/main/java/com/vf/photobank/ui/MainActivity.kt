package com.vf.photobank.ui

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.iammert.library.AnimatedTabLayout
import com.ortiz.touchview.TouchImageView
import com.vf.photobank.R
import com.vf.photobank.domain.entity.Photo
import com.vf.photobank.ui.home.HomeFragment
import com.vf.photobank.ui.search.SearchFragment
import com.vf.photobank.util.checkStoragePermission
import com.vf.photobank.util.hide
import com.vf.photobank.util.saveImageInGallery
import com.vf.photobank.util.show
import com.vf.photobank.util.showSnackBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.layout_full_screen_photo_view.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: AnimatedTabLayout
    private val fragments = listOf<Fragment>(
        HomeFragment(onPhotoClick = ::showFullScreenPhotoView),
        SearchFragment(onPhotoClick = ::showFullScreenPhotoView)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPager = view_pager
        tabLayout = tab_layout
        tabLayout.setupViewPager(viewPager)
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        setUpTabs()
    }

    inner class ViewPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int) =
            fragments.getOrNull(position) ?: HomeFragment(::showFullScreenPhotoView)

        override fun getCount() = fragments.size
    }

    /**
     * Sets click listeners on navigation tabs.
     */
    private fun setUpTabs() {
        for ((index, tab) in tabLayout.tabs.withIndex()) {
            tab.setOnClickListener {
                onTabClicked(index)
            }
        }
        (image_view_full_screen as TouchImageView).setOnClickListener { onFullScreenPhotoClick() }
        button_back.setOnClickListener { hideFullScreenPhotoView() }
    }

    private fun onTabClicked(position: Int) {
        if (viewPager.currentItem == position) {
            fragments.getOrNull(position)?.let {
                if (it is ScrollableFragment) {
                    it.scrollToTop()
                }
            }
        } else {
            viewPager.setCurrentItem(position, true)
        }
    }

    /**
     * Shows a full screen photo view displaying a large format
     * version of the [photo] as well as details and options.
     */
    private fun showFullScreenPhotoView(photo: Photo) {
        frame_layout_top.show(animated = true)
        relative_layout_bottom.show(animated = true)
        progress_bar_center.show(animated = true)
        (image_view_full_screen as TouchImageView).resetZoom()
        text_view_photographer.text = photo.photographer
        full_screen_photo_view.show(animated = true)
        loadFullScreenPhoto(photo)
    }

    /**
     * Hides the full screen photo view.
     */
    private fun hideFullScreenPhotoView() {
        button_save_photo.isClickable = false
        text_view_error.hide()
        full_screen_photo_view.hide(animated = true)
    }

    /**
     * Loads a large format of the [photo] inside
     * the ImageView of the full screen photo view.
     */
    private fun loadFullScreenPhoto(photo: Photo) {
        Glide.with(this).load(photo.src.large2x)
            .thumbnail(0.2f)
            .transition(
                DrawableTransitionOptions.withCrossFade(100)
            )
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progress_bar_center.hide()
                    button_save_photo?.setOnClickListener {
                        it.isClickable = true
                        val fileName =
                            resources.getString(R.string.photo_saved_file_name, photo.id)
                        savePhoto(image_view_full_screen.drawable.toBitmap(), fileName)
                    }
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progress_bar_center?.hide()
                    text_view_error?.show()
                    return false
                }
            })
            .into(image_view_full_screen)
    }

    /**
     * Action to be performed when the full screen
     * photo is clicked : top and bottom layouts
     * hide or show depending on their current state.
     */
    private fun onFullScreenPhotoClick() {
        if (relative_layout_bottom.isVisible) {
            relative_layout_bottom.hide(animated = true)
            frame_layout_top.hide(animated = true)
        } else {
            relative_layout_bottom.show(animated = true)
            frame_layout_top.show(animated = true)
        }
    }

    /**
     * Saves a photo in user's gallery.
     *
     * @param bitmap the photo to save, in Bitmap format
     * @param fileName the name of the created file in device's storage
     */
    private fun savePhoto(bitmap: Bitmap, fileName: String) {
        if (checkStoragePermission(this)) {
            if (saveImageInGallery(this, bitmap, fileName)) {
                showSnackBar(
                    constraint_layout_main,
                    R.string.photo_saved_success,
                    R.string.photo_snack_bar_hide
                ) { it.hide() }
            } else {
                showSnackBar(
                    constraint_layout_main,
                    R.string.photo_saved_failure,
                    R.string.photo_snack_bar_hide
                ) { it.hide() }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            // If storage permission is granted, perform "save" button's action
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                button_save_photo?.callOnClick()
            }
        }
    }

    override fun onBackPressed() {
        if (full_screen_photo_view.isVisible) hideFullScreenPhotoView()
        else super.onBackPressed()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            if (currentFocus is EditText) {
                // Clears focus when outside of the search bar is touched
                val searchBarRect = Rect()
                layout_search_bar?.getGlobalVisibleRect(searchBarRect)
                // Checks if touch is outside of search bar
                if (!searchBarRect.contains(it.rawX.toInt(), it.rawY.toInt())) {
                    val searchButtonRect = Rect()
                    search_button?.getGlobalVisibleRect(searchButtonRect)
                    // Checks if touch is outside of search button
                    if (!searchButtonRect.contains(it.rawX.toInt(), it.rawY.toInt())) {
                        (currentFocus as EditText).clearFocus()
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
