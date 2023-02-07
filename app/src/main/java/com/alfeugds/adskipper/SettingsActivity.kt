package com.alfeugds.adskipper

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.alfeugds.adskipper.delegate.PreferenceKey


private const val TAG = "SettingsActivity"
private const val REQUEST_CODE_ACCESSIBILITY_SETTINGS = 0

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            Log.v(TAG, "onCreatePreferences()")
        }

        override fun onResume() {
            super.onResume()
            refreshUIWithServiceStatus()
            promptServiceEnableDialog()
        }

        private fun refreshUIWithServiceStatus() {
            setEnableServiceSetting(
                isAccessibilityServiceEnabled()
            )
        }

        private fun setEnableServiceSetting(value: Boolean) {
            Log.d(TAG, "setEnableServiceSetting() called with: value = $value")
            findPreference<SwitchPreferenceCompat>(PreferenceKey.SETTINGS_ENABLE_SERVICE)
                ?.apply {
                    isChecked = value
                }
        }

        private fun setMuteAudioSetting(value: Boolean) {
            Log.d(TAG, "setMuteAudioSetting() called with: value = $value")
            findPreference<SwitchPreferenceCompat>(PreferenceKey.SETTINGS_MUTE_AUDIO)
                ?.apply {
                    isChecked = value
                }
        }

        private fun isAccessibilityServiceEnabled(): Boolean {
            val service = AdSkipperAccessibilityService::class.java
            val manager = requireContext().getSystemService(
                Context.ACCESSIBILITY_SERVICE
            ) as AccessibilityManager
            return manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
                .map { it.resolveInfo.serviceInfo }
                .any {
                    it.packageName == requireContext().packageName && it.name == service.name
                }
        }

        private fun promptServiceEnableDialog() {
            if (isAccessibilityServiceEnabled()) {
                Log.i(TAG, "service enabled.")
                return
            }
            AlertDialog.Builder(requireContext())
                .apply {
                    setTitle(R.string.dialog_open_accessibility_settings_title)
                    setMessage(R.string.dialog_open_accessibility_settings_message)
                    setCancelable(false)
                    setPositiveButton(
                        R.string.dialog_ok
                    ) { _, _ ->
                        // User clicked OK button
                        setEnableServiceSetting(true)
                        setMuteAudioSetting(true)
                        openAccessibilitySettings()
                    }
                    setNegativeButton(
                        R.string.dialog_cancel
                    ) { _, _ ->
                        // User cancelled the dialog
                        Log.i(TAG, "User cancelled action to open accessibility settings.")
                        setEnableServiceSetting(false)
                        activity?.onBackPressed()
                    }
                }
                .show()
        }

        private fun openAccessibilitySettings() {
            startActivityForResult(
                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS),
                REQUEST_CODE_ACCESSIBILITY_SETTINGS
            )
        }
    }
}