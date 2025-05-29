@file:Suppress("DEPRECATION")

package com.nmheir.kanicard.ui.component.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.nmheir.kanicard.constants.StoragePathKey
import com.nmheir.kanicard.core.presentation.components.Constants
import com.nmheir.kanicard.extensions.rememberCustomTabsIntent
import com.nmheir.kanicard.extensions.toHexColor
import com.nmheir.kanicard.ui.theme.linkColor
import com.nmheir.kanicard.utils.MediaCache
import com.nmheir.kanicard.utils.dataStore
import com.nmheir.kanicard.utils.get
import com.nmheir.kanicard.utils.rememberPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

data class MarkdownStyles(
    val hexTextColor: String,
    val hexCodeBackgroundColor: String,
    val hexPreBackgroundColor: String,
    val hexQuoteBackgroundColor: String,
    val hexLinkColor: String,
    val hexBorderColor: String,
    val backgroundColor: Int
) {
    companion object {
        fun fromColorScheme(colorScheme: ColorScheme) = MarkdownStyles(
            hexTextColor = colorScheme.onSurface.toArgb().toHexColor(),
            hexCodeBackgroundColor = colorScheme.surfaceVariant.toArgb().toHexColor(),
            hexPreBackgroundColor = colorScheme.surfaceColorAtElevation(1.dp).toArgb().toHexColor(),
            hexQuoteBackgroundColor = colorScheme.secondaryContainer.toArgb().toHexColor(),
            hexLinkColor = linkColor.toArgb().toHexColor(),
            hexBorderColor = colorScheme.outline.toArgb().toHexColor(),
            backgroundColor = colorScheme.surface.toArgb()
        )
    }
}

// TODO: Need implement audio, video, image
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RenderHtmlContent(
    modifier: Modifier = Modifier,
    html: String,
) {

    var template by remember { mutableStateOf("") }
    val colorScheme = MaterialTheme.colorScheme
    val markdownStyles = remember(colorScheme) {
        MarkdownStyles.fromColorScheme(colorScheme)
    }
    val customTabsIntent = rememberCustomTabsIntent()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            template = try {
                context.assets.open("template.html").bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }

    val data by remember(template, html) {
        mutableStateOf(
            processHtml(
                html = html,
                markdownStyles = markdownStyles,
                template = template
            )
        )
    }

    LaunchedEffect(data, html) {
//        Timber.d("Data: %s", data)
        Timber.d("html: %s", html)
    }

    val rootUri by rememberPreference(StoragePathKey, "")

    var imagesDir by remember { mutableStateOf<DocumentFile?>(null) }
    var audioDir by remember { mutableStateOf<DocumentFile?>(null) }
    var videosDir by remember { mutableStateOf<DocumentFile?>(null) }
    var webView by remember { mutableStateOf<WebView?>(null) }

    LaunchedEffect(rootUri) {
        withContext(Dispatchers.IO) {
            MediaCache.clearCaches()
            imagesDir = try {
                DocumentFile.fromTreeUri(context.applicationContext, rootUri.toUri())
                    ?.findFile(Constants.File.KANI_CARD)
                    ?.findFile(Constants.File.KANI_CARD_IMAGE)
            } catch (_: Exception) {
                null
            }
            audioDir = try {
                DocumentFile.fromTreeUri(context.applicationContext, rootUri.toUri())
                    ?.findFile(Constants.File.KANI_CARD)
                    ?.findFile(Constants.File.KANI_CARD_AUDIO)
            } catch (_: Exception) {
                null
            }
            videosDir = try {
                DocumentFile.fromTreeUri(context.applicationContext, rootUri.toUri())
                    ?.findFile(Constants.File.KANI_CARD)
                    ?.findFile(Constants.File.KANI_CARD_VIDEO)
            } catch (_: Exception) {
                null
            }
            // 目录加载完成后，重新触发媒体处理
            withContext(Dispatchers.Main) {
                webView?.evaluateJavascript(
                    """
                    (function() {
                        if (handlers && typeof handlers.processMediaItems === 'function') {
                            handlers.processMediaItems();
                        }
                    })();
                """.trimIndent(), null
                )
            }
        }
    }

    AndroidView(
        modifier = modifier
            .clickable {
                Timber.d("Android view clicked !")
            }
            .fillMaxHeight(),
        factory = {
            WebView(it).also { webView = it }.apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest
                    ): Boolean {
                        val url = request.url.toString()
                        if (url.startsWith("http://") || url.startsWith("https://")) {
                            customTabsIntent.launchUrl(it, url.toUri())
                        }
                        return true
                    }
                }
                addJavascriptInterface(
                    object {
                        @JavascriptInterface
                        fun onImageClick(urlStr: String) {
                            Timber.d("WebView Image clicked: $urlStr")
//                            clickedImageUrl = urlStr
//                            showDialog = true
                        }
                    },
                    "imageInterface"
                )
                addJavascriptInterface(
                    object {
                        @JavascriptInterface
                        fun processMedia(mediaName: String, id: String, mediaType: String) {
                            scope.launch(Dispatchers.IO) {
                                when (mediaType) {
                                    "image" -> {
                                        // Check cache first for images
                                        MediaCache.getImageUri(mediaName)?.let {
                                            updateImageInWebView(id, it)
                                            return@launch
                                        }

                                        val file =
                                            imagesDir?.listFiles()?.find { it.name == mediaName }
                                        val uri = file?.uri?.toString().orEmpty()
                                        if (uri.isNotEmpty()) {
                                            MediaCache.cacheImageUri(mediaName, uri)
                                            updateImageInWebView(id, uri)
                                        }
                                    }

                                    "video" -> {
                                        val file =
                                            videosDir?.listFiles()?.find { it.name == mediaName }
                                        val uri = file?.uri ?: return@launch
                                        val uriString = uri.toString()

                                        // Get thumbnail from cache or generate new one
                                        val thumbnail =
                                            MediaCache.getVideoThumbnail(mediaName) ?: run {
                                                val newThumbnail =
                                                    MediaCache.generateVideoThumbnail(context, uri)
                                                if (newThumbnail.isNotEmpty()) {
                                                    MediaCache.cacheVideoThumbnail(
                                                        mediaName,
                                                        newThumbnail
                                                    )
                                                }
                                                newThumbnail
                                            }

                                        updateVideoInWebView(id, uriString, thumbnail)
                                    }

                                    "audio" -> {
                                        val file =
                                            audioDir?.listFiles()?.find { it.name == mediaName }
                                        val uri = file?.uri?.toString().orEmpty()
                                        if (uri.isNotEmpty()) {
                                            updateAudioInWebView(id, uri)
                                        }
                                    }
                                }
                            }
                        }

                        private suspend fun updateImageInWebView(id: String, uri: String) {
                            withContext(Dispatchers.Main) {
                                webView?.evaluateJavascript(
                                    """
                    (function() {
                        const img = document.querySelector('img[data-id="$id"]');
                        if (img) img.src = '$uri';
                    })();
                    """.trimIndent(), null
                                )
                            }
                        }

                        private suspend fun updateVideoInWebView(
                            id: String,
                            uri: String,
                            thumbnail: String
                        ) {
                            withContext(Dispatchers.Main) {
                                webView?.evaluateJavascript(
                                    """
                    (function() {
                        const video = document.querySelector('video[data-id="$id"]');
                        if (video) {
                            video.src = '$uri';
                            video.poster = '$thumbnail';
                        }
                    })();
                    """.trimIndent(), null
                                )
                            }
                        }

                        private suspend fun updateAudioInWebView(id: String, uri: String) {
                            withContext(Dispatchers.Main) {
                                webView?.evaluateJavascript(
                                    """
                    (function() {
                        const audio = document.querySelector('audio[data-id="$id"]');
                        if (audio) audio.src = '$uri';
                    })();
                    """.trimIndent(), null
                                )
                            }
                        }
                    },
                    "mediaPathHandler"
                )

                settings.allowFileAccess = true
                settings.allowContentAccess = true
                settings.allowFileAccessFromFileURLs = true
                settings.allowUniversalAccessFromFileURLs = true
                setLayerType(View.LAYER_TYPE_HARDWARE, null)
                settings.domStorageEnabled = true
                settings.javaScriptEnabled = true
                settings.loadsImagesAutomatically = true
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                settings.setSupportZoom(true)
                settings.builtInZoomControls = true
                settings.displayZoomControls = false
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
            }
        },
        update = {
            it.setBackgroundColor(markdownStyles.backgroundColor)
            it.loadDataWithBaseURL(
                null,
                data,
                "text/html",
                "UTF-8",
                null
            )
        },
        onReset = {
            it.clearHistory()
            it.stopLoading()
            it.destroy()
            webView = null
        }
    )
}

private fun processHtml(
    html: String,
    markdownStyles: MarkdownStyles,
    isAppInDarkMode: Boolean = false,
    template: String
): String {
    return template
        .replace("{{CONTENT}}", html)
        .replace("{{TEXT_COLOR}}", markdownStyles.hexTextColor)
        .replace("{{BACKGROUND_COLOR}}", markdownStyles.backgroundColor.toHexColor())
        .replace("{{CODE_BACKGROUND}}", markdownStyles.hexCodeBackgroundColor)
        .replace("{{PRE_BACKGROUND}}", markdownStyles.hexPreBackgroundColor)
        .replace("{{QUOTE_BACKGROUND}}", markdownStyles.hexQuoteBackgroundColor)
        .replace("{{LINK_COLOR}}", markdownStyles.hexLinkColor)
        .replace("{{BORDER_COLOR}}", markdownStyles.hexBorderColor)
        .replace("{{COLOR_SCHEME}}", if (isAppInDarkMode) "dark" else "light")
}