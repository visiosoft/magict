package com.example.magictricks.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class VideoTrickItemAnimator : DefaultItemAnimator() {
    private val pendingAdditions = mutableListOf<RecyclerView.ViewHolder>()
    private val pendingRemovals = mutableListOf<RecyclerView.ViewHolder>()
    private val pendingMoves = mutableListOf<MoveInfo>()
    private val pendingChanges = mutableListOf<ChangeInfo>()

    private val addAnimations = mutableListOf<RecyclerView.ViewHolder>()
    private val removeAnimations = mutableListOf<RecyclerView.ViewHolder>()
    private val moveAnimations = mutableListOf<RecyclerView.ViewHolder>()
    private val changeAnimations = mutableListOf<RecyclerView.ViewHolder>()

    private data class MoveInfo(
        val holder: RecyclerView.ViewHolder,
        val fromX: Int,
        val fromY: Int,
        val toX: Int,
        val toY: Int
    )

    private data class ChangeInfo(
        val oldHolder: RecyclerView.ViewHolder,
        val newHolder: RecyclerView.ViewHolder,
        val fromX: Int,
        val fromY: Int,
        val toX: Int,
        val toY: Int
    )

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        holder.itemView.alpha = 0f
        holder.itemView.translationY = holder.itemView.height * 0.3f
        pendingAdditions.add(holder)
        return true
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {
        pendingRemovals.add(holder)
        return true
    }

    override fun animateMove(
        holder: RecyclerView.ViewHolder,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Boolean {
        val view = holder.itemView
        var startX = fromX + holder.itemView.translationX.toInt()
        var startY = fromY + holder.itemView.translationY.toInt()
        pendingMoves.add(MoveInfo(holder, startX, startY, toX, toY))
        return true
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Boolean {
        pendingChanges.add(ChangeInfo(oldHolder, newHolder, fromX, fromY, toX, toY))
        return true
    }

    override fun runPendingAnimations() {
        val additions = pendingAdditions.toList()
        val removals = pendingRemovals.toList()
        val moves = pendingMoves.toList()
        val changes = pendingChanges.toList()

        pendingAdditions.clear()
        pendingRemovals.clear()
        pendingMoves.clear()
        pendingChanges.clear()

        // Run additions
        additions.forEach { holder ->
            animateAddImpl(holder)
        }

        // Run removals
        removals.forEach { holder ->
            animateRemoveImpl(holder)
        }

        // Run moves
        moves.forEach { moveInfo ->
            animateMoveImpl(moveInfo)
        }

        // Run changes
        changes.forEach { changeInfo ->
            animateChangeImpl(changeInfo)
        }
    }

    private fun animateAddImpl(holder: RecyclerView.ViewHolder) {
        val view = holder.itemView
        addAnimations.add(holder)

        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.alpha = 1f
                    view.translationY = 0f
                    addAnimations.remove(holder)
                    dispatchAddFinished(holder)
                }
            })
            .start()
    }

    private fun animateRemoveImpl(holder: RecyclerView.ViewHolder) {
        val view = holder.itemView
        removeAnimations.add(holder)

        view.animate()
            .alpha(0f)
            .translationY(view.height * 0.3f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    removeAnimations.remove(holder)
                    dispatchRemoveFinished(holder)
                }
            })
            .start()
    }

    private fun animateMoveImpl(moveInfo: MoveInfo) {
        val view = moveInfo.holder.itemView
        moveAnimations.add(moveInfo.holder)

        view.animate()
            .translationX(0f)
            .translationY(0f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    moveAnimations.remove(moveInfo.holder)
                    dispatchMoveFinished(moveInfo.holder)
                }
            })
            .start()
    }

    private fun animateChangeImpl(changeInfo: ChangeInfo) {
        val oldView = changeInfo.oldHolder.itemView
        val newView = changeInfo.newHolder.itemView
        changeAnimations.add(changeInfo.oldHolder)
        changeAnimations.add(changeInfo.newHolder)

        oldView.animate()
            .alpha(0f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    oldView.alpha = 0f
                    changeAnimations.remove(changeInfo.oldHolder)
                    dispatchChangeFinished(changeInfo.oldHolder, true)
                }
            })
            .start()

        newView.alpha = 0f
        newView.animate()
            .alpha(1f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    newView.alpha = 1f
                    changeAnimations.remove(changeInfo.newHolder)
                    dispatchChangeFinished(changeInfo.newHolder, false)
                }
            })
            .start()
    }

    override fun endAnimation(holder: RecyclerView.ViewHolder) {
        holder.itemView.animate().cancel()
        if (pendingAdditions.remove(holder)) {
            dispatchAddFinished(holder)
        }
        if (pendingRemovals.remove(holder)) {
            dispatchRemoveFinished(holder)
        }
        if (pendingMoves.removeAll { it.holder == holder }) {
            dispatchMoveFinished(holder)
        }
        if (pendingChanges.removeAll { it.oldHolder == holder || it.newHolder == holder }) {
            dispatchChangeFinished(holder, true)
        }
        addAnimations.remove(holder)
        removeAnimations.remove(holder)
        moveAnimations.remove(holder)
        changeAnimations.remove(holder)
    }

    override fun endAnimations() {
        val additions = pendingAdditions.toList()
        val removals = pendingRemovals.toList()
        val moves = pendingMoves.toList()
        val changes = pendingChanges.toList()

        pendingAdditions.clear()
        pendingRemovals.clear()
        pendingMoves.clear()
        pendingChanges.clear()

        additions.forEach { holder ->
            holder.itemView.alpha = 1f
            holder.itemView.translationY = 0f
            dispatchAddFinished(holder)
        }

        removals.forEach { holder ->
            holder.itemView.alpha = 1f
            holder.itemView.translationY = 0f
            dispatchRemoveFinished(holder)
        }

        moves.forEach { moveInfo ->
            moveInfo.holder.itemView.translationX = 0f
            moveInfo.holder.itemView.translationY = 0f
            dispatchMoveFinished(moveInfo.holder)
        }

        changes.forEach { changeInfo ->
            changeInfo.oldHolder.itemView.alpha = 1f
            changeInfo.newHolder.itemView.alpha = 1f
            dispatchChangeFinished(changeInfo.oldHolder, true)
            dispatchChangeFinished(changeInfo.newHolder, false)
        }

        addAnimations.clear()
        removeAnimations.clear()
        moveAnimations.clear()
        changeAnimations.clear()
    }

    override fun isRunning(): Boolean {
        return !pendingAdditions.isEmpty() ||
                !pendingRemovals.isEmpty() ||
                !pendingMoves.isEmpty() ||
                !pendingChanges.isEmpty() ||
                !addAnimations.isEmpty() ||
                !removeAnimations.isEmpty() ||
                !moveAnimations.isEmpty() ||
                !changeAnimations.isEmpty()
    }
} 