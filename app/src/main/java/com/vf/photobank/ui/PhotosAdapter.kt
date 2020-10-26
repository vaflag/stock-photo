package com.vf.photobank.ui.home

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.vf.photobank.R
import com.vf.photobank.domain.entity.Photo
import com.vf.photobank.util.MAX_NUMBER_OF_RESULTS
import com.vf.photobank.util.PEXELS_WEBSITE_URL
import com.vf.photobank.util.PHOTOS_CORNER_RADIUS
import kotlinx.android.synthetic.main.item_header_home.view.*
import kotlinx.android.synthetic.main.item_header_search.view.*
import kotlinx.android.synthetic.main.item_photo.view.*

/**
 * Adapter for Photo items.
 *
 * Binds a set of Photo objects to views.
 * Adds a header view and a footer view.
 *
 * @param headerType type of the list's header.
 * @param onPhotoClick action performed when a photo is clicked.
 */
class PhotosAdapter(
    private val headerType: HeaderType,
    private val onPhotoClick: (Photo) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val HEADER_HOME_LAYOUT_ID = R.layout.item_header_home
        const val HEADER_SEARCH_LAYOUT_ID = R.layout.item_header_search
        const val PHOTO_LAYOUT_ID = R.layout.item_photo
        const val FOOTER_LAYOUT_ID = R.layout.item_footer

        private const val ITEM_VIEW_TYPE_HEADER = 0
        private const val ITEM_VIEW_TYPE_ITEM = 1
        private const val ITEM_VIEW_TYPE_FOOTER = 2

        fun <T : RecyclerView.ViewHolder> instantiateViewHolder(
            parent: ViewGroup,
            layoutId: Int,
            factory: (View) -> T
        ): T {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(layoutId, parent, false)
            return factory(view)
        }
    }

    enum class HeaderType(val layoutId: Int) {
        HOME(HEADER_HOME_LAYOUT_ID),
        SEARCH(HEADER_SEARCH_LAYOUT_ID)
    }

    private var photos: MutableList<Photo> = mutableListOf()
    private val addedViews = 2 // Number of views added (header and footer) in addition to photos
    var numberOfResults = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_ITEM -> instantiateViewHolder(
                parent,
                PHOTO_LAYOUT_ID,
                ::PhotoViewHolder
            )
            ITEM_VIEW_TYPE_FOOTER -> instantiateViewHolder(
                parent,
                FOOTER_LAYOUT_ID,
                ::FooterViewHolder
            )
            else -> instantiateViewHolder(
                parent,
                headerType.layoutId,
                ::HeaderViewHolder
            )
        }
    }

    override fun getItemCount(): Int {
        return when {
            photos.size > 0 -> photos.size + addedViews
            else -> 0
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind()
            is PhotoViewHolder -> holder.bind(photos[getIndexFromPosition(position)])
            is FooterViewHolder -> holder.bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == itemCount - 1 -> ITEM_VIEW_TYPE_FOOTER
            position > 0 -> ITEM_VIEW_TYPE_ITEM
            else -> ITEM_VIEW_TYPE_HEADER
        }
    }

    /**
     * Clears current photos, adds every photo
     * from [newPhotos] and update the UI.
     *
     * @param newPhotos the new photos to be added.
     */
    fun setPhotos(
        newPhotos: List<Photo>
    ) {
        photos.clear()
        photos.addAll(newPhotos)
        notifyDataSetChanged()
    }

    /**
     * Adds every photo from [newPhotos] and updates the UI.
     *
     * @param newPhotos the photos to be added.
     */
    fun addPhotos(newPhotos: List<Photo>) {
        val insertionStartingPosition = this.photos.size + 1
        photos.addAll(newPhotos)
        notifyItemRangeInserted(insertionStartingPosition, newPhotos.size)
    }

    fun hasItems(): Boolean {
        return photos.size > 0
    }

    private fun getIndexFromPosition(position: Int) = position - 1

    /**
     * ViewHolder for Photo item.
     *
     * @param photoItemView View of the item.
     */
    inner class PhotoViewHolder(photoItemView: View) : RecyclerView.ViewHolder(photoItemView) {

        fun bind(photo: Photo) {
            // Sets ImageView's dimension ratio
            (itemView.image_view.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                "H,${photo.width}:${photo.height}"

            // Loads the photo from its URL
            Glide.with(itemView)
                .load(photo.src.medium)
                .thumbnail(0.4f)
                .apply(
                    RequestOptions.bitmapTransform(
                        RoundedCorners(
                            PHOTOS_CORNER_RADIUS
                        )
                    )
                )
                .transition(
                    DrawableTransitionOptions.withCrossFade(
                        100
                    )
                )
                .error(R.drawable.shape_placeholder_error)
                .into(itemView.image_view)

            itemView.image_view.setOnClickListener {
                onPhotoClick(photo)
            }
        }
    }

    /**
     * ViewHolder for header item.
     *
     * @param headerView View of the header.
     */
    inner class HeaderViewHolder(headerView: View) : RecyclerView.ViewHolder(headerView) {

        fun bind() {
            (itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
            when (headerType) {
                HeaderType.HOME -> bindHomeHeader()
                HeaderType.SEARCH -> bindSearchHeader()
            }
        }

        private fun bindHomeHeader() {
            itemView.image_view_header.apply {
                clipToOutline = true
                setOnClickListener {
                    val browserIntent =
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(PEXELS_WEBSITE_URL)
                        )
                    ContextCompat.startActivity(
                        itemView.context,
                        browserIntent,
                        null
                    )
                }
            }
        }

        private fun bindSearchHeader() {
            (itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).topMargin = 0
            val context = itemView.context
            itemView.text_view_results.text = when (numberOfResults) {
                MAX_NUMBER_OF_RESULTS -> context.getString(
                    R.string.search_max_number_of_results,
                    numberOfResults
                )
                1 -> context.getString(R.string.search_singular_number_of_results, numberOfResults)
                else -> context.getString(R.string.search_number_of_results, numberOfResults)
            }
        }
    }

    /**
     * ViewHolder for footer item.
     *
     * @param footerView View of the footer.
     */
    inner class FooterViewHolder(footerView: View) : RecyclerView.ViewHolder(footerView) {

        fun bind() {
            (itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
        }
    }
}
