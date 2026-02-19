package com.eink.calendar.ui

import androidx.lifecycle.ViewModel
import com.eink.calendar.utils.EinkDisplayOptimizer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 主应用 ViewModel
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val einkDisplayOptimizer: EinkDisplayOptimizer
) : ViewModel() {

    fun initializeEinkDisplay() {
        einkDisplayOptimizer.enableFullRefresh()
    }

    fun setRefreshMode(mode: String) {
        einkDisplayOptimizer.setRefreshMode(mode)
    }

    fun requestFullRefresh() {
        einkDisplayOptimizer.triggerFullRefresh()
    }
}
