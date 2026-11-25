package com.clipworks.bolaclip

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.app.NotificationCompat

class FloatingService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var layoutParams: WindowManager.LayoutParams? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundNotification()
        setupOverlayView()
    }

    private fun startForegroundNotification() {
        val channelId = "bolaclip_overlay_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "BolaClip Flutuante",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("BolaClip ativo")
            .setContentText("Sobreposição sobre outros aplicativos em execução.")
            .setSmallIcon(android.R.drawable.ic_menu_add)
            .setOngoing(true)
            .build()

        startForeground(1001, notification)
    }

    private fun setWindowFocusable(focusable: Boolean, targetView: View?) {
        val wm = windowManager ?: return
        val params = layoutParams ?: return
        val currentFlags = params.flags
        params.flags = if (focusable) {
            currentFlags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
        } else {
            currentFlags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        }
        try {
            wm.updateViewLayout(overlayView, params)
        } catch (_: Exception) {
        }
        if (focusable && targetView != null) {
            targetView.post {
                targetView.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.showSoftInput(targetView, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    private fun setupOverlayView() {
        if (overlayView != null) return

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        }

        layoutParams = params

        val webView = WebView(this).apply {
            setBackgroundColor(0x00000000)
            isFocusable = true
            isFocusableInTouchMode = true
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            webViewClient = WebViewClient()
            loadUrl("file:///android_asset/index.html?overlay=1")
            setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        setWindowFocusable(true, view)
                    }
                    MotionEvent.ACTION_OUTSIDE -> {
                        setWindowFocusable(false, null)
                    }
                }
                false
            }
        }

        overlayView = webView
        windowManager?.addView(overlayView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayView?.let {
            windowManager?.removeView(it)
            overlayView = null
        }
        layoutParams = null
    }
}
