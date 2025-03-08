package com.andy.common

import android.view.View

fun View.postDelay(delayMillis: Long, action: () -> Unit) {
    postDelayed(action, delayMillis)
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.gone(checkVisible: Boolean = true) {
    if (checkVisible && isVisible) {
        visibility = View.GONE
    } else {
        gone()
    }
}

fun View.inVisible() {
    visibility = View.INVISIBLE
}

val View.isVisible: Boolean
    get() = visibility == View.VISIBLE

val View.isInvisible: Boolean
    get() = visibility == View.INVISIBLE

val View.isGone: Boolean
    get() = visibility == View.GONE