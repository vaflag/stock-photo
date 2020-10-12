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
import com.vf.photobank.util.PEXELS_WEBSITE_URL
import com.vf.photobank.util.PHOTOS_CORNER_RADIUS
import kotlinx.android.synthetic.main.item_header.view.*
import kotlinx.android.synthetic.main.item_photo.view.*

/**
 * Adapter for Photo items.
 *
 * Binds a set of Photo objects to views.
 * Adds a header view and a footer view.
 *
 * @param hasHeader indicates whether a header should be added.
 */
class PhotosAdapter(
    private val hasHeader: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
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

    private var photos: MutableList<Photo> = mutableListOf()
    private val addedViews =
        if (hasHeader) 2 else 1 // Number of views added (header and footer) in addition to photos

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_ITEM -> instantiateViewHolder(
                parent,
                PhotoViewHolder.LAYOUT_ID,
                ::PhotoViewHolder
            )
            ITEM_VIEW_TYPE_FOOTER -> instantiateViewHolder(
                parent,
                FooterViewHolder.LAYOUT_ID,
                ::FooterViewHolder
            )
            else -> instantiateViewHolder(
                parent,
                HeaderViewHolder.LAYOUT_ID,
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
            else -> if (hasHeader) ITEM_VIEW_TYPE_HEADER else ITEM_VIEW_TYPE_ITEM
        }
    }

    /**
     * Adds every photo from [photos] and updates the UI.
     *
     * @param photos the photos to be added.
     */
    fun addPhotos(photos: List<Photo>) {
        val insertionStartingPosition = this.photos.size + 1
        this.photos.addAll(photos)
        notifyItemRangeInserted(insertionStartingPosition, photos.size)
    }

    /**
     * Clears current photos, adds new ones and update the UI.
     *
     * @param updatedPhotos the new photos to be added.
     */
    fun updatePhotos(updatedPhotos: List<Photo>) {
        photos.clear()
        photos.addAll(updatedPhotos)
        notifyDataSetChanged()
    }

    fun hasItems(): Boolean {
        return photos.size > 0
    }

    private fun getIndexFromPosition(position: Int): Int {
        return if (hasHeader) position - 1 else position
    }

    /**
     * ViewHolder for Photo item.
     *
     * @param photoItemView View of the item.
     */
    class PhotoViewHolder(photoItemView: View) : RecyclerView.ViewHolder(photoItemView) {

        companion object {
            const val LAYOUT_ID = R.layout.item_photo
        }

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
        }
    }

    /**
     * ViewHolder for header item.
     *
     * @param headerView View of the header.
     */
    class HeaderViewHolder(headerView: View) : RecyclerView.ViewHolder(headerView) {

        companion object {
            const val LAYOUT_ID = R.layout.item_header
        }

        fun bind() {
            (itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
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
    }

    /**
     * ViewHolder for footer item.
     *
     * @param footerView View of the footer.
     */
    class FooterViewHolder(footerView: View) : RecyclerView.ViewHolder(footerView) {

        companion object {
            const val LAYOUT_ID = R.layout.item_footer
        }

        fun bind() {
            (itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
        }
    }
}
