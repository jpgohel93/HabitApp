package com.tjcg.habitapp.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.data.HabitDataSource
import com.tjcg.habitapp.databinding.FragmentHistoryCalendarBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class HistoryCalendarFragment : Fragment() {

    @Inject lateinit var dataSource : HabitDataSource
    lateinit var binding : FragmentHistoryCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryCalendarBinding.inflate(
            inflater, container, false
        )
        CoroutineScope(Dispatchers.Main).launch {
            val fullCalendar = dataSource.getFullCalendarAsync().await()
            var finishedHabits = 0
            var totalHabits = 0
            var perfectDays = 0
            for (calendar in (fullCalendar ?: emptyList())) {
                val allHabits = calendar.habitsInADay
                Log.d("Habits", "${allHabits.size}")
                var perfectDay = allHabits.isNotEmpty()
                for (habit in allHabits) {
                    totalHabits += 1
                    if (habit.isFinished) {
                        finishedHabits += 1
                    } else {
                        perfectDay = false
                    }
                }
                if (perfectDay) {
                    perfectDays += 1
                }
            }
            binding.habitCountText.text = finishedHabits.toString()
            binding.finishText.text = finishedHabits.toString()
            binding.completionText.text = "$finishedHabits/$totalHabits Habits"
            val completionRate = (finishedHabits.toFloat() / totalHabits) *100
            binding.completionRateCount.text = completionRate.toInt().toString()
            binding.avgCompletionText2.text = "${completionRate.toInt()}%"
            binding.perfectDaysCountText.text = perfectDays.toString()
            getWeekdayInfo()
        }
        return binding.root
    }

    private suspend fun getWeekdayInfo() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, 1)
        val barChartOpt = Array(7) { 0 }
        val barChartDates = Array(7) { "0" }
        val thatDayFinished = Array(7) { 0 }
        val thatDayScheduled = Array(7) { 0 }
        for (i in 0..6) {
            val habitCalendar = dataSource.getByCalendarAsync(Constant.generateDateString(cal)).await()
       /*     var totalHabits = 0
            var finishedHabit = 0
            for (habit in (habitCalendar?.habitsInADay ?: emptyList())) {
                totalHabits += 1
                if (habit.isFinished)  { finishedHabit += 1 }
            }
            val completionRate =if (totalHabits > 0) {
                (finishedHabit.toFloat() / totalHabits) * 100
            } else {
                0f
            }
            barChartOpt[i] = completionRate.toInt()
            Log.d("CompletionRate", "${Constant.generateDateString(cal)} - $completionRate")  */
            barChartOpt[i] = if (habitCalendar != null) {
                habitCalendar.completed
            } else  {
                0
            }
            var habitCount = 0
            var habitFinished = 0
            for (habit in (habitCalendar?.habitsInADay ?: emptyList())) {
                habitCount += 1
                if (habit.isFinished) { habitFinished+= 1}
            }
            thatDayFinished[i] = habitFinished
            thatDayScheduled[i] = habitCount
            barChartDates[i] = cal.get(Calendar.DAY_OF_MONTH).toString()
            Log.d("CompletionRate", "${barChartOpt[i]}")
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        val wBinding = binding.weekGraphLayout
        wBinding.bars.visibility = View.VISIBLE
        wBinding.barDates.visibility = View.VISIBLE
        val barHeight = wBinding.day1bar.height
        val commonMargin = 50

        // for bar 1
        if (barChartOpt[0] == 0) {
            wBinding.day1bar.visibility = View.INVISIBLE
        } else {
            val layoutParams1 = LinearLayout.LayoutParams(0,
                (barHeight*(barChartOpt[0].toFloat()/100)).toInt(), 1f)
            layoutParams1.marginStart = commonMargin
            layoutParams1.marginEnd = commonMargin
            wBinding.day1bar.layoutParams = layoutParams1
        }
        wBinding.day1Date.text = barChartDates[0]
        wBinding.day1Date.setOnClickListener {
            binding.finishText.text = thatDayFinished[0].toString()
            binding.scheduledText.text = thatDayScheduled[0].toString()
        }

        // for bar 2
        if (barChartOpt[1] == 0) {
            wBinding.day2bar.visibility = View.INVISIBLE
        } else {
            val layoutParams2 = LinearLayout.LayoutParams(0,
                (barHeight*(barChartOpt[1].toFloat()/100)).toInt(), 1f)
            layoutParams2.marginStart = commonMargin
            layoutParams2.marginEnd = commonMargin
            wBinding.day2bar.layoutParams = layoutParams2
        }
        wBinding.day2Date.text = barChartDates[1]
        wBinding.day2Date.setOnClickListener {
            binding.finishText.text = thatDayFinished[1].toString()
            binding.scheduledText.text = thatDayScheduled[1].toString()
        }

        // for bar 3
        if (barChartOpt[2] == 0) {
            wBinding.day3bar.visibility = View.INVISIBLE
        } else {
            val layoutParams2 = LinearLayout.LayoutParams(0,
                (barHeight*(barChartOpt[2].toFloat()/100)).toInt(), 1f)
            layoutParams2.marginStart = commonMargin
            layoutParams2.marginEnd = commonMargin
            wBinding.day3bar.layoutParams = layoutParams2
        }
        wBinding.day3Date.text = barChartDates[2]
        wBinding.day3Date.setOnClickListener {
            binding.finishText.text = thatDayFinished[2].toString()
            binding.scheduledText.text = thatDayScheduled[2].toString()
        }

        // for bar 4
        if (barChartOpt[3] == 0) {
            wBinding.day4bar.visibility = View.INVISIBLE
        } else {
            val layoutParams2 = LinearLayout.LayoutParams(0,
                (barHeight*(barChartOpt[3].toFloat()/100)).toInt(), 1f)
            layoutParams2.marginStart = commonMargin
            layoutParams2.marginEnd = commonMargin
            wBinding.day4bar.layoutParams = layoutParams2
            Log.d("BarHeight", "$barHeight / ${barChartOpt[3]}")
        }
        wBinding.day4Date.text = barChartDates[3]
        wBinding.day4Date.setOnClickListener {
            binding.finishText.text = thatDayFinished[3].toString()
            binding.scheduledText.text = thatDayScheduled[3].toString()
        }

        // for bar 5
        if (barChartOpt[4] == 0) {
            wBinding.day5bar.visibility = View.INVISIBLE
        } else {
            val layoutParams2 = LinearLayout.LayoutParams(0,
                (barHeight*(barChartOpt[4].toFloat()/100)).toInt(), 1f)
            layoutParams2.marginStart = commonMargin
            layoutParams2.marginEnd = commonMargin
            wBinding.day5bar.layoutParams = layoutParams2
        }
        wBinding.day5Date.text = barChartDates[4]
        wBinding.day5Date.setOnClickListener {
            binding.finishText.text = thatDayFinished[4].toString()
            binding.scheduledText.text = thatDayScheduled[4].toString()
        }

        // for bar 6
        if (barChartOpt[5] == 0) {
            wBinding.day6bar.visibility = View.INVISIBLE
        } else {
            val layoutParams2 = LinearLayout.LayoutParams(0,
                (barHeight*(barChartOpt[5].toFloat()/100)).toInt(), 1f)
            layoutParams2.marginStart = commonMargin
            layoutParams2.marginEnd = commonMargin
            wBinding.day6bar.layoutParams = layoutParams2
            Log.d("BarHeight6", "$barHeight / ${barChartOpt[5]}")
        }
        wBinding.day6Date.text = barChartDates[5]
        wBinding.day6Date.setOnClickListener {
            binding.finishText.text = thatDayFinished[5].toString()
            binding.scheduledText.text = thatDayScheduled[5].toString()
        }

        // for bar 7
        if (barChartOpt[6] == 0) {
            wBinding.day7bar.visibility = View.INVISIBLE
        } else {
            val layoutParams2 = LinearLayout.LayoutParams(0,
                (barHeight*(barChartOpt[6].toFloat()/100)).toInt(), 1f)
            layoutParams2.marginStart = commonMargin
            layoutParams2.marginEnd = commonMargin
            wBinding.day7bar.layoutParams = layoutParams2
        }
        wBinding.day7Date.text = barChartDates[6]
        wBinding.day7Date.setOnClickListener {
            binding.finishText.text = thatDayFinished[6].toString()
            binding.scheduledText.text = thatDayScheduled[6].toString()
        }
    }

    private fun updateBottomHabitCards(dateStr : String) {

    }

    companion object {

        fun getInstance() : HistoryCalendarFragment {
            return HistoryCalendarFragment()
        }
    }
}