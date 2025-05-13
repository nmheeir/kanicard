package com.nmheir.kanicard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.room.util.TableInfo
import com.nmheir.kanicard.extensions.md.MarkdownWithParametersParser
import com.nmheir.kanicard.ui.activities.LocalAwareWindowInset
import com.nmheir.kanicard.ui.component.ReviewFlashCard
import com.nmheir.kanicard.ui.theme.KaniTheme
import timber.log.Timber

@Composable
fun TestScreen(modifier: Modifier = Modifier) {
    val markdown = """
    # 👋 Welcome, **{{userName}}**

    Thank you for joining *{{platformName}}*! Here's a quick overview of your profile and preferences.

    ## 🧑 Profile

    - **Full Name:** {{fullName}}
    - **Email:** {{email}}
    - **Subscription Plan:** {{plan}}
    - **Joined On:** {{joinDate}}

    ## 📊 Usage Summary

    Below is a summary of your usage this month:

    | Feature           | Usage Count | Limit       |
    |-------------------|-------------|-------------|
    | Projects Created  | *{{projects}}*        | 10          |
    | API Calls         | {{apiCalls}}        | 1000        |
    | Team Members      | {{teamMembers}}     | 5           |

    ## 🔧 Settings

    You have enabled the following features:
    - [x] Dark Mode
    - [x] Email Notifications
    - [ ] Two-Factor Authentication

    > If you have any questions, reach out to our support team at **support@{{platformDomain}}**.
""".trimIndent()

    val params = mapOf(
        "userName" to "alice_dev",
        "fullName" to "Alice Wonderland",
        "email" to "alice@example.com",
        "plan" to "Pro",
        "joinDate" to "2023-11-10",
        "projects" to "7",
        "apiCalls" to "845",
        "teamMembers" to "3",
        "platformName" to "DevPortalX",
        "platformDomain" to "devportalx.com"
    )

/*
    // 1. Template Markdown
    val markdown = """
    # Xin chào, **{{name}}**

    Chào mừng bạn đến với **{{app}}**! Hãy bắt đầu trải nghiệm ngay thôi.
""".trimIndent()

// 2. Map các giá trị thay thế
    val params = mapOf(
        "name" to "Minh",
        "app"  to "SimpleApp"
    )
*/


    val htmlContent = MarkdownWithParametersParser.parseToHtml(markdown, params)

    KaniTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(LocalAwareWindowInset.current.asPaddingValues())
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = htmlContent,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
            )
            ReviewFlashCard(
                html = htmlContent,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}