package com.nmheir.kanicard.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle

fun <A : Activity> Activity.startNewActivity(activity: Class<A>, bundle: Bundle? = null) {
    Intent(this, activity).also {
        bundle?.let { intent.putExtras(it) }
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}