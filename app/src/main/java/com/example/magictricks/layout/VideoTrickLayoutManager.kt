package com.example.magictricks.layout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import com.example.magictricks.adapter.VideoTrickAdapter
import com.example.magictricks.animation.VideoTrickItemAnimator
import androidx.media3.common.util.UnstableApi

@UnstableApi
class VideoTrickLayoutManager(
    context: Context,
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    private val adapter: VideoTrickAdapter
) : LinearLayoutManager(context, orientation, reverseLayout) {

    private var extraLayoutSpace = 0
    private var smoothScrollEnabled = true

    init {
        // Remove item animator setup from LayoutManager
    }

    override fun calculateExtraLayoutSpace(state: RecyclerView.State, extraLayoutSpace: IntArray) {
        this.extraLayoutSpace = extraLayoutSpace[0]
        super.calculateExtraLayoutSpace(state, extraLayoutSpace)
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
        if (!smoothScrollEnabled) {
            recyclerView.scrollToPosition(position)
            return
        }

        val firstVisiblePosition = findFirstVisibleItemPosition()
        val lastVisiblePosition = findLastVisibleItemPosition()

        when {
            position <= firstVisiblePosition -> {
                // Scroll up
                recyclerView.smoothScrollBy(0, -recyclerView.height)
            }
            position >= lastVisiblePosition -> {
                // Scroll down
                recyclerView.smoothScrollBy(0, recyclerView.height)
            }
            else -> {
                // Already visible, just ensure it's fully visible
                val targetView = findViewByPosition(position)
                targetView?.let {
                    val top = it.top
                    val bottom = it.bottom
                    val viewHeight = it.height
                    val parentHeight = recyclerView.height

                    when {
                        top < 0 -> recyclerView.smoothScrollBy(0, top)
                        bottom > parentHeight -> recyclerView.smoothScrollBy(0, bottom - parentHeight)
                    }
                }
            }
        }
    }

    override fun onLayoutCompleted(state: RecyclerView.State) {
        super.onLayoutCompleted(state)
        checkVisibility()
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        // Enable/disable smooth scrolling based on scroll state
        smoothScrollEnabled = state != RecyclerView.SCROLL_STATE_SETTLING
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            checkVisibility()
        }
    }

    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        // Add scroll listener to handle visibility changes
        view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // Trigger layout to update visibility percentages
                requestLayout()
            }
        })
    }

    private fun checkVisibility() {
        val firstVisible = findFirstVisibleItemPosition()
        val lastVisible = findLastVisibleItemPosition()

        for (i in firstVisible..lastVisible) {
            val view = findViewByPosition(i) ?: continue
            val visibleHeight = view.height - view.top.coerceAtLeast(0) - (view.bottom - height).coerceAtLeast(0)
            val visiblePercentage = visibleHeight.toFloat() / view.height

            adapter.checkVisibility(i, visiblePercentage)
        }
    }
} 