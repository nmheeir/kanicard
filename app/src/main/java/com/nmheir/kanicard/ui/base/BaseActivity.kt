package com.nmheir.kanicard.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity :
    AppCompatActivity(),
    ThemingDelegate by ThemingDelegateImpl() {

    override fun onCreate(savedInstanceState: Bundle?) {
        applyAppTheme(this)
        super.onCreate(savedInstanceState)
    }
}