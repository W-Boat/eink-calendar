package com.eink.calendar.ui.month

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eink.calendar.databinding.ItemDayCellBinding
import com.eink.calendar.domain.model.DayCell
import java.time.LocalDate

/**
 * 月视图日期单元格适配器
 */
class MonthCalendarAdapter(
    private val onDateClick: (LocalDate) -> Unit
) : ListAdapter<DayCell, MonthCalendarAdapter.DayViewHolder>(DayDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        return DayViewHolder(
            ItemDayCellBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onDateClick
        )
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DayViewHolder(
        private val binding: ItemDayCellBinding,
        private val onDateClick: (LocalDate) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(dayCell: DayCell) {
            binding.apply {
                // 显示日期
                dateText.text = dayCell.date.dayOfMonth.toString()

                // 设置透明度（非当前月份的日期显示为灰色）
                root.alpha = if (dayCell.isCurrentMonth) 1.0f else 0.4f

                // 标记今天
                if (dayCell.isToday) {
                    root.setBackgroundColor(0xFF000000.toInt())
                    dateText.setTextColor(0xFFFFFFFF.toInt())
                }

                // 显示事件计数
                eventCountText.text = if (dayCell.eventCount > 0) {
                    "●".repeat(minOf(dayCell.eventCount, 3))
                } else {
                    ""
                }

                // 点击事件
                root.setOnClickListener {
                    if (dayCell.isCurrentMonth) {
                        onDateClick(dayCell.date)
                    }
                }
            }
        }
    }

    class DayDiffCallback : DiffUtil.ItemCallback<DayCell>() {
        override fun areItemsTheSame(oldItem: DayCell, newItem: DayCell) =
            oldItem.date == newItem.date

        override fun areContentsTheSame(oldItem: DayCell, newItem: DayCell) =
            oldItem == newItem
    }
}
