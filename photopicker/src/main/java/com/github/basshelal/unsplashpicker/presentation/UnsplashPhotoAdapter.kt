@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.unsplashpicker.presentation

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.basshelal.unsplashpicker.R
import com.github.basshelal.unsplashpicker.data.UnsplashPhoto
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_unsplash_photo.view.*


/**
 * The photos recycler view adapter.
 * This is using the Android paging library to display an infinite list of photos.
 * This deals with either a single or multiple selection list.
 */
internal class UnsplashPhotoAdapter(
        isMultipleSelection: Boolean = false,
        onPhotoSelectedListener: OnPhotoSelectedListener? = null,
        photoSize: PhotoSize = PhotoSize.SMALL,
        placeHolderDrawable: Drawable? = null,
        errorDrawable: Drawable? = null
) : PagedListAdapter<UnsplashPhoto, UnsplashPhotoAdapter.PhotoViewHolder>(COMPARATOR) {

    internal var isMultipleSelection: Boolean = isMultipleSelection
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    internal var onPhotoSelectedListener: OnPhotoSelectedListener? = onPhotoSelectedListener
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    internal var photoSize: PhotoSize = photoSize
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    internal var placeHolderDrawable: Drawable? = placeHolderDrawable
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    internal var errorDrawable: Drawable? = errorDrawable
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private val selectedP = LinkedHashMap<Int, UnsplashPhoto>()

    // Key is the index of the selected photo, value is the photo itself

    private val selected = ArrayList<UnsplashPhoto>()
    private val mSelectedIndexes = ArrayList<Int>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_unsplash_photo, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        // item
        getItem(position)?.also { photo ->
            holder.apply {
                // image
                photoImageView.aspectRatio = photo.height.D / photo.width.D
                itemView.setBackgroundColor(Color.parseColor(photo.color))
                itemView.setBackgroundResource(R.drawable.ef_folder_placeholder)
               // photoImageView.background=R.drawable.ef_folder_placeholder
                photoImageView.setBackgroundResource(R.drawable.ef_folder_placeholder)

                val request = Picasso.get().load(photoSize.get(photo.urls))
                placeHolderDrawable?.also { request.placeholder(it) }
                errorDrawable?.also { request.error(it) }
                request.into(photoImageView)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                    photoImageView.contentDescription = photo.description
                }
                holder.sponsored.isVisible = photo.isSponsored

                // photograph name
                nameTextView.text = photo.user.name

                // selected controls visibility
                //checkedImageView.isVisible = adapterPosition in selected.keys
                //overlay.isVisible = adapterPosition in selected.keys

                // click listeners
                itemView.setOnLongClickListener {
                    onPhotoSelectedListener?.onLongClickPhoto(photo, photoImageView)
                    true
                }
                itemView.setOnClickListener {
                    onPhotoSelectedListener?.onClickPhoto(photo, photoImageView)

                }
            }
        }
    }

    fun getImages(): ArrayList<UnsplashPhoto> {
        selected.clear()
        for (index in mSelectedIndexes) {
            selectedP[index]?.let {
                selected.add(it)
            }
        }
        return selected
    }

    fun clearSelection() {
        selected.clear()
        mSelectedIndexes.clear()
    }

    internal fun selectPhoto(photo: UnsplashPhoto) {
        val adapterPosition = currentList?.indexOf(photo) ?: return
        if (!isMultipleSelection) {
            // single selection mode
            if (adapterPosition in selectedP.keys) {
                clearSelectedPhotos()
            } else {
                clearSelectedPhotos()
                selectedP[adapterPosition] = photo
                notifyItemChanged(adapterPosition)
            }
        } else {
            // multi selection mode
            if (adapterPosition in selectedP.keys) {
                selectedP.remove(adapterPosition)
            } else {
                selectedP[adapterPosition] = photo
            }
            notifyItemChanged(adapterPosition)
        }
    }




    internal inline fun getSelectedPhotos() = selectedP.values.toList()

    internal inline fun clearSelectedPhotos() {
        val originalKeys = selectedP.keys.toList()
        selectedP.clear()
        originalKeys.forEach { notifyItemChanged(it) }
    }



    companion object {
        internal val COMPARATOR = object : DiffUtil.ItemCallback<UnsplashPhoto>() {
            override fun areItemsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) = oldItem == newItem
            override fun areContentsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) = oldItem == newItem
        }
    }

    internal class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val photoImageView: AspectRatioImageView = view.item_imageView

        val nameTextView: TextView = view.item_unsplash_photo_text_view
        val checkedImageView: ImageView = view.item_unsplash_photo_checked_image_view
        val overlay: View = view.item_unsplash_photo_overlay
        val sponsored: LinearLayout = view.sponsored_linearLayout
    }


}