@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.unsplashpicker.presentation

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView


class UnsplashPhotoPickerRecyclerView
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attributeSet, defStyle) {

    private var savedState: SavedState? = null

    inline val verticalScrollOffset: Int get() = computeVerticalScrollOffset()

    inline val maxVerticalScroll: Int get() = computeVerticalScrollRange() - computeVerticalScrollExtent()

    var flingVelocityY: Int = 0
        private set

    var verticalScrollSpeed: Int = 0
        private set

    var oldVerticalScrollOffset: Int = 0
    private var oldTime: Long = 0L

    override fun setLayoutManager(layoutManager: LayoutManager?) {
        super.setLayoutManager(layoutManager)

        setUpOverScroller()
    }

    private inline fun setUpOverScroller() {


        addVelocityTrackerOnFlingListener()

        addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                    val dY = verticalScrollOffset.D - oldVerticalScrollOffset.D
                    val dSecs = (System.currentTimeMillis() - oldTime).D / 1E3.D

                    verticalScrollSpeed = (dY / dSecs).I

                    if (dy != 0 && scrollState == SCROLL_STATE_SETTLING &&
                        (verticalScrollOffset == 0 || verticalScrollOffset == maxVerticalScroll)
                    ) {
                    }

                    oldVerticalScrollOffset = verticalScrollOffset
                    oldTime = System.currentTimeMillis()
                }
            }
        )
    }

    fun addVelocityTrackerOnFlingListener() {
        val originalOnFlingListener = onFlingListener
        onFlingListener = object : OnFlingListener() {
            override fun onFling(velocityX: Int, velocityY: Int): Boolean {
                flingVelocityY = velocityY
                return originalOnFlingListener?.onFling(velocityX, velocityY) ?: false
            }
        }
    }

    fun saveState(): SavedState? {
        savedState = this.onSaveInstanceState() as? SavedState?
        return savedState
    }

    fun restoreState(state: SavedState? = savedState) {
        this.onRestoreInstanceState(state)
    }

}

private const val overScrollThreshold = 20.0


