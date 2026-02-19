package com.eink.calendar.ui.month

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eink.calendar.data.repository.CalendarRepository
import com.eink.calendar.domain.model.MonthData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

/**
 * 月视图 ViewModel
 */
@HiltViewModel
class MonthViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository
) : ViewModel() {

    private val _monthData = MutableStateFlow<MonthData?>(null)
    val monthData: StateFlow<MonthData?> = _monthData.asStateFlow()

    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth: StateFlow<YearMonth> = _currentYearMonth.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    init {
        loadMonthData()
    }

    fun loadMonthData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                calendarRepository.getMonthData(_currentYearMonth.value).collect { monthData ->
                    _monthData.value = monthData
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun previousMonth() {
        _currentYearMonth.value = _currentYearMonth.value.minusMonths(1)
        loadMonthData()
    }

    fun nextMonth() {
        _currentYearMonth.value = _currentYearMonth.value.plusMonths(1)
        loadMonthData()
    }

    fun goToToday() {
        _currentYearMonth.value = YearMonth.now()
        _selectedDate.value = LocalDate.now()
        loadMonthData()
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }
}
