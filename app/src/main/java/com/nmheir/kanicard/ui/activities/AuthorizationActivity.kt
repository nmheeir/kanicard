package com.nmheir.kanicard.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nmheir.kanicard.ui.theme.KaniCardTheme
import com.nmheir.kanicard.utils.startNewActivity

class AuthorizationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KaniCardTheme {
                // TODO: Kiểm tra token của user
                startNewActivity(MainActivity::class.java)
            }
        }
    }
}