package com.tjcg.habitapp

import android.view.View
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation

object Animator {

    private const val distance = 100f

    fun slideUpShow(v: View, duration: Long) {
        v.visibility = View.VISIBLE
        TranslateAnimation(0f, 0f, distance, 0f).apply {
            this.duration = duration
            v.startAnimation(this)

        }
    }

    fun slideDownGone(v: View, duration: Long) {
        TranslateAnimation(0f, 0f, 0f, distance).apply {
            this.duration = duration
            v.startAnimation(this)
            v.postOnAnimation {
                v.visibility = View.GONE
            }
        }
    }

    fun rotateClockwise(v:View, duration: Long) {
        RotateAnimation(0f, 90f, v.width.toFloat()/2, v.height.toFloat()/2).apply {
            this.duration = duration
            v.startAnimation(this)
        }
    }

    fun rotateAntiClock(v: View, duration: Long) {
        RotateAnimation(0f, -90f, v.width.toFloat()/2, v.height.toFloat()/2).apply {
            this.duration = duration
            v.startAnimation(this)
        }
    }

    fun scaleUp(v:View, duration: Long) {
        ScaleAnimation(1f, 1f, 1f, 0f).apply {
            this.duration = duration
            v.startAnimation(this)
        }
    }

    fun scaleDown(v: View, duration: Long) {
        ScaleAnimation(1f, 1f, 0f, 1f).apply {
            this.duration = duration
            v.startAnimation(this)
        }
    }

    fun fadeOut(v:View, duration:Long) {
        v.animate().alpha(0f).duration = duration
    }
    fun fadeIn(v:View, duration: Long) {
        v.animate().alpha(1f).duration = duration
    }

    fun zoomIn(v:View, duration: Long) {
        v.visibility = View.VISIBLE
        ScaleAnimation(0f, 1f, 0f, 1f,
            v.width.toFloat()/2, v.height.toFloat()/2).apply {
            this.duration = duration
            v.startAnimation(this)
        }
    }

    fun zoomOut(v: View, duration: Long) {
        ScaleAnimation(1f, 0f, 1f, 0f,
            v.width.toFloat()/2, v.height.toFloat()/2).apply {
            this.duration = duration
            v.startAnimation(this)
            v.postOnAnimation {
                v.visibility = View.GONE
            }
        }
    }
}