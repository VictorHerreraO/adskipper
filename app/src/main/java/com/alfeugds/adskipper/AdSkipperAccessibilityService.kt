package com.alfeugds.adskipper

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.alfeugds.adskipper.binding.AccessibilityBinding
import com.alfeugds.adskipper.delegate.AudioManagerDelegate
import com.alfeugds.adskipper.delegate.PreferenceManagerDelegate


private const val TAG = "AdSkipperService"

class AdSkipperAccessibilityService : AccessibilityService() {

    private var _binding: AccessibilityBinding? = null
    private val binding: AccessibilityBinding get() = _binding!!

    var isRunning = false

    private lateinit var audioManager: AudioManagerDelegate
    private lateinit var prefsManager: PreferenceManagerDelegate
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate fired")
        prefsManager = PreferenceManagerDelegate(this)
        audioManager = AudioManagerDelegate(this) {
            prefsManager.isMuteAdEnabled
        }
        _binding = AccessibilityBinding(this)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.i(TAG, "onRebind fired")
    }

    override fun onServiceConnected() {
        Log.v(TAG, "accessibility onServiceConnected(). Ad skipping service connected.")
        isRunning = true
        super.onServiceConnected()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.i(TAG, "onTaskRemoved fired")
        disable()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        Log.v(TAG, "onDestroy fired")
        isRunning = false
        _binding = null
        super.onDestroy()
    }

    override fun onInterrupt() {
        Log.v(TAG, "onInterrupt fired")
        isRunning = false
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "onUnbind called. " + intent?.dataString)
        return super.onUnbind(intent)
    }

    override fun onLowMemory() {
        Log.w(TAG, "onLowMemory")
        super.onLowMemory()
    }

    fun disable(){
        Log.i(TAG, "Disabling service with stopSelf")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopSelf()
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!prefsManager.isServiceEnabled) {
            Log.i(TAG, "Service is not supposed to be enabled.")
            return
        }
        try {
            if (binding.hasNoBindings) {
                audioManager.unMuteMedia()
                Log.v(TAG, "No ads yet...")
                return
            }
            Log.i(TAG, "player_learn_more_button or skipAdButton or adProgressText are visible. Trying to skip ad...")

            audioManager.muteMedia()

            binding.skipAdButton
                ?.takeIf { it.isClickable }
                ?.run {
                    Log.v(TAG, "skipAdButton is clickable! Trying to click it...")
                    performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    Log.i(TAG, "Clicked skipAdButton!")
                }
                ?: Log.v(TAG, "skipAdButton is null... returning...")
        } catch (error: Exception) {
            Log.e(TAG, "Something went wrong...", error)
        }
    }

}