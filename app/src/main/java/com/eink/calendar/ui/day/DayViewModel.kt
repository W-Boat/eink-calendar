package com.eink.calendar.ui.day

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eink.calendar.data.repository.CalendarRepository
import com.eink.calendar.domain.model.DayData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * 日视图 ViewModel
 */
@HiltViewModel
class DayViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository
) : ViewModel() {

    private val _dayData = MutableStateFlow<DayData?>(null)
    val dayData: StateFlow<DayData?> = _dayData.asStateFlow()

    private val _currentDate = MutableStateFlow(LocalDate.now())
    val currentDate: StateFlow<LocalDate> = _currentDate.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadDayData()
    }

    fun loadDayData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                calendarRepository.getDayData(_currentDate.value).collect { dayData ->
                    _dayData.value = dayData
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun previousDay() {
        _currentDate.value = _currentDate.value.minusDays(1)
        loadDayData()
    }

    fun nextDay() {
        _currentDate.value = _currentDate.value.plusDays(1)
        loadDayData()
    }

    fun goToToday() {
        _currentDate.value = LocalDate.now()
        loadDayData()
    }

    fun setDate(date: LocalDate) {
        _currentDate.value = date
        loadDayData()
    }
}
