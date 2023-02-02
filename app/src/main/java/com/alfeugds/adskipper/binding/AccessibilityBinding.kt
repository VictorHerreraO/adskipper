package com.alfeugds.adskipper.binding

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityNodeInfo


private const val AD_LEARN_MORE_BUTTON_ID = "com.google.android.youtube:id/player_learn_more_button"
private const val SKIP_AD_BUTTON_ID = "com.google.android.youtube:id/skip_ad_button"
private const val AD_PROGRESS_TEXT = "com.google.android.youtube:id/ad_progress_text"
private const val APP_PROMO_AD_CTA_OVERLAY =
    "com.google.android.youtube:id/app_promo_ad_cta_overlay"
private const val AD_COUNTDOWN = "com.google.android.youtube:id/ad_countdown"

class AccessibilityBinding(
    private val service: AccessibilityService
) {
    val adLearnMoreElement: AccessibilityNodeInfo?
        get() = findAccessibilityNodeInfosByViewId(AD_LEARN_MORE_BUTTON_ID)

    val skipAdButton: AccessibilityNodeInfo?
        get() = findAccessibilityNodeInfosByViewId(SKIP_AD_BUTTON_ID)

    val adProgressText: AccessibilityNodeInfo?
        get() = findAccessibilityNodeInfosByViewId(AD_PROGRESS_TEXT)

    val appPromoAdCTAOverlay: AccessibilityNodeInfo?
        get() = findAccessibilityNodeInfosByViewId(APP_PROMO_AD_CTA_OVERLAY)

    val adCountdown: AccessibilityNodeInfo?
        get() = findAccessibilityNodeInfosByViewId(AD_COUNTDOWN)

    val hasNoBindings: Boolean
        get() = adLearnMoreElement == null
                && skipAdButton == null
                && adProgressText == null
                && appPromoAdCTAOverlay == null
                && adCountdown == null

    private fun findAccessibilityNodeInfosByViewId(
        id: String
    ): AccessibilityNodeInfo? = service
        .rootInActiveWindow
        .findAccessibilityNodeInfosByViewId(id)
        ?.getOrNull(0)
}