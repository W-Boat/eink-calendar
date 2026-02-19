package com.eink.calendar.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.WindowManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 墨水屏显示优化器
 * 针对电子墨水屏的显示特性进行优化
 */
@Singleton
class EinkDisplayOptimizer @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var partialRefreshCount = 0
    private val maxPartialRefreshBeforeFull = 8
    private var currentMode = RefreshMode.SMART

    enum class RefreshMode {
        FULL,     // 完全刷新 - 清晰但慢
        PARTIAL,  // 部分刷新 - 快速但可能残影
        SMART     // 智能切换 - 自动在一定次数部分刷新后完全刷新
    }

    /**
     * 启用完全刷新模式（初始化时使用）
     */
    fun enableFullRefresh() {
        currentMode = RefreshMode.FULL
        partialRefreshCount = 0
    }

    /**
     * 设置刷新模式
     */
    fun setRefreshMode(mode: String) {
        currentMode = when (mode.uppercase()) {
            "FULL" -> RefreshMode.FULL
            "PARTIAL" -> RefreshMode.PARTIAL
            else -> RefreshMode.SMART
        }
    }

    /**
     * 根据内容类型决定刷新模式
     */
    fun getOptimalRefreshMode(contentType: ContentType): RefreshMode {
        return when (contentType) {
            ContentType.STATIC_TEXT -> RefreshMode.PARTIAL
            ContentType.IMAGE_HEAVY -> RefreshMode.FULL
            ContentType.ANIMATION -> RefreshMode.PARTIAL
            ContentType.FULL_SCREEN_CHANGE -> RefreshMode.FULL
            ContentType.NAVIGATION -> RefreshMode.SMART
        }
    }

    /**
     * 触发完全刷新
     */
    fun triggerFullRefresh() {
        partialRefreshCount = 0
        // 在实际设备上可通过系统 API 触发完全刷新
    }

    /**
     * 记录部分刷新，并在需要时触发完全刷新
     */
    fun onPartialRefresh(): Boolean {
        partialRefreshCount++
        if (currentMode == RefreshMode.SMART && partialRefreshCount >= maxPartialRefreshBeforeFull) {
            triggerFullRefresh()
            return true // 表示执行了完全刷新
        }
        return false
    }

    /**
     * 禁用 View 的动画（墨水屏上不需要动画）
     */
    fun disableAnimationsForView(view: View) {
        view.animate().cancel()
        view.clearAnimation()
    }

    /**
     * 为窗口优化显示
     */
    fun optimizeWindow(window: android.view.Window) {
        // 墨水屏设备通常需要防止频繁刷新
        window.setWindowAnimationDuration(0)
    }

    enum class ContentType {
        STATIC_TEXT,       // 纯文本内容
        IMAGE_HEAVY,       // 图像较多
        ANIMATION,         // 动画内容
        FULL_SCREEN_CHANGE,// 整页内容改变
        NAVIGATION         // 页面导航
    }
}

/**
 * 扩展函数：设置窗口动画时长
 */
fun android.view.Window.setWindowAnimationDuration(duration: Long) {
    // 通过反射或系统属性设置动画时长为0
    try {
        val layoutParams = attributes
        layoutParams?.windowAnimations = 0
        attributes = layoutParams
    } catch (e: Exception) {
        // 忽略不支持的设备
    }
}
