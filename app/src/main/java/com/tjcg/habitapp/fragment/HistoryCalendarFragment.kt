package com.tjcg.habitapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.tjcg.habitapp.MainActivity
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.data.HabitDataSource
import com.tjcg.habitapp.databinding.FragmentHistoryCalendarBinding
import com.tjcg.habitapp.databinding.OtherWeekdayGraph2Binding
import com.tjcg.habitapp.viewmodel.HabitViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HistoryCalendarFragment : Fragment() {

    @Inject lateinit var dataSource : HabitDataSource
    private lateinit var habitViewModel : HabitViewModel
    private lateinit var binding : FragmentHistoryCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.currentPage = Constant.PAGE_IN
        binding = FragmentHistoryCalendarBinding.inflate(
            inflater, container, false
        )
        habitViewModel = dataSource.provideViewModel()
        CoroutineScope(Dispatchers.Main).launch {
            val fullCalendar = dataSource.getFullCalendarAsync().await()
            var finishedHabits = 0
            var totalHabits = 0
            var perfectDays = 0
            var currentStreak = 0
            var bestStreak = 0
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
                    currentStreak += 1
                    if (bestStreak < currentStreak) {
                        bestStreak = currentStreak
                    }
                } else {
                    currentStreak = 0
                }
            }
            binding.habitCountText.text = finishedHabits.toString()
            binding.finishText.text = finishedHabits.toString()
            binding.completionText.text = "$finishedHabits/$totalHabits Habits"
            val completionRate = (finishedHabits.toFloat() / totalHabits) *100
            binding.completionRateCount.text = completionRate.toInt().toString()
            binding.avgCompletionText2.text = "${completionRate.toInt()}%"
            binding.perfectDaysCountText.text = perfectDays.toString()
            binding.streakCountText.text = currentStreak.toString()
            binding.bestStreakText.text = "Best Streak : $bestStreak"
            binding.fullCalendarView.setOnDateChangeListener { _, _, i2, i3 ->
                habitViewModel.selectedWeekCalendarDate.value = arrayOf(i3, i2)
                Log.d("SelectedWeekCalendarDate", "${habitViewModel.selectedWeekCalendarDate.value?.get(0)}")
                habitViewModel.selectedAppPage.value = Constant.PAGE_1 // this will select the page 1 of the app or first tab
            }
            getWeekdayInfo()
        }
        return binding.root
    }

    private suspend fun getWeekdayInfo() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK,   1)
        val barChartOpt = Array(7) { 0 }
        val barChartDates = Array(7) { "0" }
        val thatDayFinished = Array(7) { 0 }
        val thatDayScheduled = Array(7) { 0 }
        var habitsFinishedInWholeWeek = 0 // will be set in habit finish card
        var perfectDaysThisWeek = 0 // will be set in perfect days card
        for (i in 0..6) {
            val habitCalendar = dataSource.getByCalendarAsync(Constant.generateDateString(cal)).await()
            if (habitCalendar ==  null) {
                Log.d("HabitCalendarNull", "Null for date ${cal.get(Calendar.DAY_OF_MONTH)}")
            }
            barChartOpt[i] = habitCalendar?.completed ?: 0
            var habitCount = 0
            var habitFinished = 0
            for (habit in (habitCalendar?.habitsInADay ?: emptyList())) {
                habitCount += 1
                if (habit.isFinished) {
                    habitFinished+= 1
                    habitsFinishedInWholeWeek += 1
                }
            }
            if ((habitCount > 0) && (habitFinished == habitCount)) {
                perfectDaysThisWeek += 1
            }
            thatDayFinished[i] = habitFinished
            thatDayScheduled[i] = habitCount
            barChartDates[i] = cal.get(Calendar.DAY_OF_MONTH).toString()
            Log.d("CompletionRate", "${barChartOpt[i]}")
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        // update remaining top card info s which are related to week
        updateHabitInfoAsPerWeek(habitsFinishedInWholeWeek, perfectDaysThisWeek)

        val wBinding = binding.weekGraphLayout
    //    wBinding.bars.visibility = View.VISIBLE
    //    wBinding.barDates.visibility = View.VISIBLE
        val barHeight = wBinding.day1bar.height
        val commonMargin = 50

        Log.d("TodayString", "${Constant.todayString}")
        val todayDateOfMonth = Constant.todayString.split("-")[2]
        // for bar 1
        if (barChartOpt[0] == 0) {
            wBinding.day1bar.visibility = View.INVISIBLE
        } else {
            val layoutParams1 = LinearLayout.LayoutParams(Constant.BAR_WIDTH_360,
                (barHeight*(barChartOpt[0].toFloat()/100)).toInt())
            layoutParams1.marginStart = commonMargin
            layoutParams1.marginEnd = commonMargin
            wBinding.day1bar.layoutParams = layoutParams1
        }
        wBinding.day1Date.text = barChartDates[0]
        wBinding.day1Date.setOnClickListener {
            binding.finishText.text = thatDayFinished[0].toString()
            binding.scheduledText.text = thatDayScheduled[0].toString()
            unselectAllBarChartDates(wBinding)
            wBinding.day1Date.isSelected = true
        }
        if (todayDateOfMonth == barChartDates[0]) {
            wBinding.day1Date.isSelected = true
            wBinding.day1Date.performClick()
        }

        // for bar 2
        if (barChartOpt[1] == 0) {
            wBinding.day2bar.visibility = View.INVISIBLE
        } else {
            val layoutParams2 = LinearLayout.LayoutParams(Constant.BAR_WIDTH_360,
                (barHeight*(barChartOpt[1].toFloat()/100)).toInt())
            layoutParams2.marginStart = commonMargin
            layoutParams2.marginEnd = commonMargin
            wBinding.day2bar.layoutParams = layoutParams2
        }
        wBinding.day2Date.text = barChartDates[1]
        wBinding.day2Date.setOnClickListener {
            binding.finishText.text = thatDayFinished[1].toString()
            binding.scheduledText.text = thatDayScheduled[1].toString()
            unselectAllBarChartDates(wBinding)
            wBinding.day2Date.isSelected = true
        }
        if (todayDateOfMonth  == barChartDates[1]) {
            wBinding.day2Date.isSelected = true
            wBinding.day2Date.performClick()
        }

        // for bar 3
        if (barChartOpt[2] == 0) {
            wBinding.day3bar.visibility = View.INVISIBLE
        } else {
            val layoutParams2 = LinearLayout.LayoutParams(Constant.BAR_WIDTH_360,
                (barHeight*(barChartOpt[2].toFloat()/100)).toInt())
      //      layoutParams2.marginStart = commonMargin
     //       layoutParams2.marginEnd = commonMargin
            wBinding.day3bar.layoutParams = layoutParams2
        }
        wBinding.day3Date.text = barChartDates[2]
        wBinding.day3Date.setOnClickListener {
            binding.finishText.text = thatDayFinished[2].toString()
            binding.scheduledText.text = thatDayScheduled[2].toString()
            unselectAllBarChartDates(wBinding)
            wBinding.day3Date.isSelected = true
        }
        if (todayDateOfMonth == barChartDates[2]) {
            wBinding.day3Date.isSelected = true
            wBinding.day3Date.performClick()
        }

        // for bar 4
        if (barChartOpt[3] == 0) {
            wBinding.day4bar.visibility = View.INVISIBLE
        } else {
            val layoutParams2 = LinearLayout.LayoutParams(Constant.BAR_WIDTH_360,
                (barHeight*(barChartOpt[3].toFloat()/100)).toInt())
            layoutParams2.marginStart = commonMargin
            layoutParams2.marginEnd = commonMargin
            wBinding.day4bar.layoutParams = layoutParams2
            Log.d("BarHeight", "$barHeight / ${barChartOpt[3]}")
        }
        wBinding.day4Date.text = barChartDates[3]
        wBinding.day4Date.setOnClickListener {
            binding.finishText.text = thatDayFinished[3].toString()
            binding.scheduledText.text = thatDayScheduled[3].toString()
            unselectAllBarChartDates(wBinding)
            wBinding.day4Date.isSelected = true
        }
        if (todayDateOfMonth == barChartDates[3]) {
            wBinding.day4Date.isSelected = true
            wBinding.day4Date.performClick()
        }

        // for bar 5
        if (barChartOpt[4] == 0) {
            wBinding.day5bar.visibility = View.INVISIBLE
        } else {
            val layoutParams2 = LinearLayout.LayoutParams(Constant.BAR_WIDTH_360,
                (barHeight*(barChartOpt[4].toFloat()/100)).toInt())
            layoutParams2.marginStart = commonMargin
            layoutParams2.marginEnd = commonMargin
            wBinding.day5bar.layoutParams = layoutParams2
        }
        wBinding.day5Date.text = barChartDates[4]
        wBinding.day5Date.setOnClickListener {
            binding.finishText.text = thatDayFinished[4].toString()
            binding.scheduledText.text = thatDayScheduled[4].toString()
            unselectAllBarChartDates(wBinding)
            wBinding.day5Date.isSelected = true
        }
        if (todayDateOfMonth == barChartDates[4]) {
            wBinding.day5Date.isSelected = true
            wBinding.day5Date.performClick()
        }

        // for bar 6
        if (barChartOpt[5] == 0) {
            wBinding.day6bar.visibility = View.INVISIBLE
        } else {
            val layoutParams2 = LinearLayout.LayoutParams(Constant.BAR_WIDTH_360,
                (barHeight*(barChartOpt[5].toFloat()/100)).toInt())
            layoutParams2.marginStart = commonMargin
            layoutParams2.marginEnd = commonMargin
            wBinding.day6bar.layoutParams = layoutParams2
            Log.d("BarHeight6", "$barHeight / ${barChartOpt[5]}")
        }
        wBinding.day6Date.text = barChartDates[5]
        wBinding.day6Date.setOnClickListener {
            binding.finishText.text = thatDayFinished[5].toString()
            binding.scheduledText.text = thatDayScheduled[5].toString()
            unselectAllBarChartDates(wBinding)
            wBinding.day6Date.isSelected = true
        }
        if (todayDateOfMonth == barChartDates[5]) {
            wBinding.day6Date.isSelected = true
            wBinding.day6Date.performClick()
        }

        // for bar 7
        if (barChartOpt[6] == 0) {
            wBinding.day7bar.visibility = View.INVISIBLE
        } else {
            val layoutParams2 = LinearLayout.LayoutParams(Constant.BAR_WIDTH_360,
                (barHeight*(barChartOpt[6].toFloat()/100)).toInt())
            layoutParams2.marginStart = commonMargin
            layoutParams2.marginEnd = commonMargin
            wBinding.day7bar.layoutParams = layoutParams2
        }
        wBinding.day7Date.text = barChartDates[6]
        wBinding.day7Date.setOnClickListener {
            binding.finishText.text = thatDayFinished[6].toString()
            binding.scheduledText.text = thatDayScheduled[6].toString()
            unselectAllBarChartDates(wBinding)
            wBinding.day7Date.isSelected = true
        }
        if (todayDateOfMonth == barChartDates[6]) {
            wBinding.day7Date.isSelected = true
            wBinding.day7Date.performClick()
        }
    }

    private fun unselectAllBarChartDates(wBinding: OtherWeekdayGraph2Binding) {
        wBinding.day1Date.isSelected = false
        wBinding.day2Date.isSelected = false
        wBinding.day3Date.isSelected = false
        wBinding.day4Date.isSelected = false
        wBinding.day5Date.isSelected = false
        wBinding.day6Date.isSelected = false
        wBinding.day7Date.isSelected = false
    }

    private fun updateHabitInfoAsPerWeek(finishedInWeek: Int, perfectDays: Int) {
        binding.thisWeekText.text = "This Week: $finishedInWeek"
        binding.thisWeekText2.text = "This Week: $perfectDays"
    }

    companion object {

        fun getInstance() : HistoryCalendarFragment {
            return HistoryCalendarFragment()
        }
    }
}