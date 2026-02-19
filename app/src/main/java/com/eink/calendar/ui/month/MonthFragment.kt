package com.eink.calendar.ui.month

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.eink.calendar.databinding.FragmentMonthBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.YearMonth

/**
 * 月视图 Fragment
 * 显示整月日历，支持上/下月导航
 */
@AndroidEntryPoint
class MonthFragment : Fragment() {

    private var _binding: FragmentMonthBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MonthViewModel by viewModels()
    private lateinit var adapter: MonthCalendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()
        setupListeners()
    }

    private fun setupUI() {
        // 设置网格布局（7列，表示一周）
        adapter = MonthCalendarAdapter(viewModel::selectDate)
        binding.calendarRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 7)
            adapter = this@MonthFragment.adapter
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            // 观察月份数据
            viewModel.monthData.collect { monthData ->
                monthData?.let {
                    binding.monthYearText.text = "${it.year}年${it.month}月"
                    adapter.submitList(it.days)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // 观察加载状态
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setupListeners() {
        // 上一月
        binding.prevMonthButton.setOnClickListener {
            viewModel.previousMonth()
        }

        // 下一月
        binding.nextMonthButton.setOnClickListener {
            viewModel.nextMonth()
        }

        // 回到今天
        binding.todayButton.setOnClickListener {
            viewModel.goToToday()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
