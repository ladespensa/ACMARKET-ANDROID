package com.gesdes.acmarket.utils

import android.os.Build
import android.os.Handler
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import java.util.*

object AnimationUtils {

    fun startSlideEffect(containerTransition: ViewGroup, views: ArrayList<out View?>, gravity: Int = Gravity.RIGHT, delay: Long = 500) {
        for (view in views) {
            view?.visibility = View.INVISIBLE
        }
        Handler().postDelayed({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                TransitionManager.beginDelayedTransition(containerTransition, Slide(gravity))
            }
            for (view in views) {
                view?.visibility = View.VISIBLE
            }
        }, delay)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun startSlideEffectOneByOne(containerTransition: ViewGroup, views: ArrayList<out View?>, gravity: Int = Gravity.RIGHT, delay: Long = 500) {
        for (view in views) {
            view?.visibility = View.INVISIBLE
        }
        var i = 1
        for (view in views) {
            Handler().postDelayed({
                TransitionManager.beginDelayedTransition(containerTransition, Slide(gravity))
                view?.visibility = View.VISIBLE
            }, delay * i)
            i = i.plus(1)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun startSlideEffectToView(containerTransition: ViewGroup, view: View?, gravity: Int = Gravity.RIGHT, delay: Long = 500) {
        Handler().postDelayed({
            TransitionManager.beginDelayedTransition(containerTransition, Slide(gravity))
            view?.visibility = View.VISIBLE
        }, delay)
    }
}