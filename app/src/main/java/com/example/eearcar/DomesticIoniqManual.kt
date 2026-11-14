package com.example.eearcar

import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
internal fun DomesticIoniqManual(query: String, close: () -> Unit) {
    val start = "file:///android_asset/ioniq5kr/index.html?q=" + android.net.Uri.encode(query)
    var viewer by remember { mutableStateOf<WebView?>(null) }
    fun back() {
        val web = viewer
        if (web != null && web.canGoBack()) web.goBack() else close()
    }
    BackHandler { back() }
    Column(Modifier.fillMaxSize().safeDrawingPadding().imePadding()) {
        Row {
            TextButton(onClick = { back() }) { Text("뒤로") }
            TextButton(onClick = { viewer?.loadUrl(start.substringBefore('?')) }) { Text("전체 목차") }
            TextButton(onClick = close) { Text("이어카 홈") }
        }
        AndroidView(modifier = Modifier.fillMaxWidth().weight(1f), factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.allowContentAccess = false
                settings.blockNetworkLoads = true
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                        val url = request.url
                        if (url.scheme == "https" && url.host == "ownersmanual.hyundai.com") {
                            try {
                                context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, url))
                            } catch (_: android.content.ActivityNotFoundException) {
                                android.widget.Toast.makeText(context, "웹 브라우저를 열 수 없습니다.", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                        return !url.toString().startsWith("file:///android_asset/ioniq5kr/")
                    }
                }
                viewer = this
                loadUrl(start)
            }
        })
    }
    DisposableEffect(Unit) { onDispose { viewer?.destroy() } }
}
