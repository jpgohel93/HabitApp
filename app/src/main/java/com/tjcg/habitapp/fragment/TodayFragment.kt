package com.tjcg.habitapp.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.habitapp.MainActivity
import com.tjcg.habitapp.R
import com.tjcg.habitapp.adapter.WeekCalendarAdapter
import com.tjcg.habitapp.data.*
import com.tjcg.habitapp.databinding.FragmentTodayBinding
import com.tjcg.habitapp.databinding.OtherMorningCardsBinding
import com.tjcg.habitapp.databinding.RecyclerItemRegularHabitBinding
import com.tjcg.habitapp.viewmodel.HabitViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

const val EXTRA_OPTIONS_TYPE_GOAL_COUNTER = 1
const val EXTRA_OPTIONS_TYPE_GOAL_TIMER = 2
const val EXTRA_OPTIONS_TYPE_GOAL_COUNTER_FINISHED = 3
const val EXTRA_OPTIONS_TYPE_GOAL_TIMER_FINISHED = 4

@AndroidEntryPoint
class TodayFragment : Fragment() {

    @Inject lateinit var dataSource: HabitDataSource
    private lateinit var binding : FragmentTodayBinding
    private lateinit var ctx : Context
//    private lateinit var habitAdapter: HabitAdapter
    private lateinit var allHabits : ArrayList<Habit>
    private lateinit var todayHabits : ArrayList<HabitInADay>
    private lateinit var habitsToShow : ArrayList<TodayHabit>
    private lateinit var todayInCalendar : HabitCalendar
    private var selectedDate : String = ""
    private var selectedWeekDay : Int = 1
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private lateinit var weekCalendar : RecyclerView
    private lateinit var viewModel: HabitViewModel
    private var finishedHabitCountInDay = 0f
    private var totalHabitOfDay = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ctx = findNavController().context
        if (!MainActivity.isNavShowing) {
            MainActivity.showBottomNavigation()
        }
        MainActivity.currentPage = Constant.PAGE_1
        dataSource.setupViewModel(ctx)
        viewModel = dataSource.provideViewModel()
        binding = FragmentTodayBinding.inflate(inflater, container, false)
        binding.todayHabitRecycler.layoutManager = LinearLayoutManager(ctx)
        binding.todayHabitRecycler.recycledViewPool.setMaxRecycledViews(0,0)

        //set up week calendar
        val dates = Constant.generateYearCalendar()
        weekCalendar = binding.includedWeekCalendar.weekCalendarRecycler
        weekCalendar.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        val weekCalendarAdapter = WeekCalendarAdapter(ctx, dates, viewModel)
        weekCalendar.adapter = weekCalendarAdapter
        val cal = Calendar.getInstance()
        Constant.todayString = Constant.generateDateString(cal)
        val defaultPosition = cal.get(Calendar.WEEK_OF_YEAR) - 1
        weekCalendar.scrollToPosition(defaultPosition)
        val displayWidth = Constant.getDisplayWidth(ctx)
        weekCalendar.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            var totalScroll = 0
            var currentPosition = defaultPosition

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when(newState) {
                    1 -> {
                        Log.d("Recycler", "Scroll Started")
                        totalScroll = 0
                    }
                    0 -> {
                        Log.d("Recycler", "Scroll finshed $totalScroll")
                        if (totalScroll > displayWidth / 2) {
                            val positionMovement = (totalScroll / (displayWidth / 2))
                            currentPosition += positionMovement
                        } else if (totalScroll < -(displayWidth/2)) {
                            val positionMovement = (totalScroll / (-displayWidth / 2))
                            currentPosition -= positionMovement
                        }
                        Log.d("Switching to position", "$currentPosition")
                        recyclerView.smoothScrollToPosition(currentPosition)
                        totalScroll = 0
                    }
                    2 -> {
                        Log.d("Recycler", "In Velocity  $totalScroll")
                        if (totalScroll > displayWidth / 2) {
                            val positionMovement = (totalScroll / (displayWidth / 2))
                            currentPosition += positionMovement
                        } else if (totalScroll < -(displayWidth/2)) {
                            val positionMovement = (totalScroll / (-displayWidth / 2))
                            currentPosition -= positionMovement
                        }
                        Log.d("Switching to position", "$currentPosition")
                        recyclerView.smoothScrollToPosition(currentPosition)
                        totalScroll = 0
                    }
                }

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalScroll += dx
            }
        })

        // set today Calendar View
        binding.mainDateText.text = cal.get(Calendar.DAY_OF_MONTH).toString()
        binding.mainDayText.text = Constant.provideShotDay(cal.get(Calendar.DAY_OF_WEEK))
        binding.todayCalendarView.setOnClickListener {
            viewModel.selectedWeekCalendarDate.value = arrayOf(cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.MONTH))
        }

        viewModel.selectedWeekCalendarDate.observe(viewLifecycleOwner) { calArray ->
            val cal1 = Calendar.getInstance()
            cal1.set(Calendar.MONTH, calArray[1])
            cal1.set(Calendar.DAY_OF_MONTH, calArray[0])
            updateHabitData(cal1)
        }
        if (viewModel.selectedWeekCalendarDate.value ==  null) {
            weekCalendarAdapter.setSelectionMark(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH))
        }
        return binding.root
    }

    private fun updateHabitData(cal: Calendar) {
        // generate today string
        selectedDate = Constant.generateDateString(cal)
        selectedWeekDay = cal.get(Calendar.DAY_OF_WEEK)
        Log.d("SelectedWeekday", "$selectedWeekDay")
        // search if there is any data in the calendar if no data then generate new data
        val todayDateString = Constant.generateDateString(Calendar.getInstance())
        mainScope.launch {
            val habitCalendar = dataSource.getByCalendarAsync(selectedDate).await()
            if (isCurrentSelectionIsFuture()) {
                mainScope.launch {
                    val habitsInADay = ArrayList<HabitInADay>()
                    val habits = dataSource.getAllHabitsAsync().await()
                    for (habit in habits) {
                        if ((habit.repetitionType == Constant.HABIT_REPEAT_AS_WEEKDAY) &&
                            habit.repetitionDaysArray.contains(selectedWeekDay.toString())) {
                            habitsInADay.add(HabitInADay(habit.id))
                        }
                        if (habit.repetitionType != Constant.HABIT_REPEAT_AS_WEEKDAY) {
                            habitsInADay.add(HabitInADay(habit.id))
                        }
                    }
                    todayInCalendar = HabitCalendar(selectedDate, selectedWeekDay, habitsInADay)
                    generateTodayHabitList()
                }
                return@launch
            }
            if (habitCalendar == null && (selectedDate==todayDateString)) {
                mainScope.launch {
                    val habits = dataSource.getAllHabitsAsync().await()
                    val weekDayInt = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                    val dayHabits = ArrayList<HabitInADay>()
                    for (habit in habits) {
                        if ((habit.repetitionType == Constant.HABIT_REPEAT_AS_WEEKDAY) &&
                            (habit.repetitionDaysArray.contains(selectedWeekDay.toString()))) {
                            val newDayHabit = HabitInADay(habit.id, false)
                            dayHabits.add(newDayHabit)
                        } else if (habit.repetitionType != Constant.HABIT_REPEAT_AS_WEEKDAY){
                            val newDayHabit = HabitInADay(habit.id, false)
                            dayHabits.add(newDayHabit)
                        }
                    }
                    todayInCalendar = HabitCalendar(selectedDate, weekDayInt, dayHabits)
                    dataSource.addInCalendar(todayInCalendar)
                    Log.e("availableToday1", "${todayInCalendar.habitsInADay.size}")
                    generateTodayHabitList()
                }
                return@launch
            }
            if (habitCalendar != null && (selectedDate==todayDateString)) {
                todayInCalendar = habitCalendar
                todayHabits = todayInCalendar.habitsInADay as ArrayList<HabitInADay>
                mainScope.launch {
                    val habits = dataSource.getAllHabitsAsync().await()
                    var anyUpdate = false
                    for (habit in habits) {
                        val available = todayHabits.find { it.habitId == habit.id }
                        if (available == null) {
                            val thisHabit = dataSource.getHabitByIdAsync(habit.id ?: 0).await()
                            Log.d("trying to getHabit", "${available?.habitId}")
                            Log.d("results", "$thisHabit, ${thisHabit?.repetitionType}, " +
                                    "${thisHabit?.repetitionDaysArray}")
                            if (thisHabit != null &&
                                (thisHabit.repetitionType == Constant.HABIT_REPEAT_AS_WEEKDAY) &&
                                (thisHabit.repetitionDaysArray.contains(selectedWeekDay.toString()))) {
                                todayHabits.add(HabitInADay(thisHabit.id, false))
                                Log.d("Adding", "${thisHabit.id}")
                                anyUpdate = true
                            } else if (thisHabit?.repetitionType != Constant.HABIT_REPEAT_AS_WEEKDAY){
                                todayHabits.add(HabitInADay(thisHabit?.id ?: 0, false))
                                Log.d("Adding", "${thisHabit?.id}")
                                anyUpdate = true
                            }
                        }
                    }
                    if (anyUpdate) dataSource.updateHabitsInCalendar(todayInCalendar)
                    Log.e("availableToday2", "${todayInCalendar.habitsInADay.size}")
                    generateTodayHabitList()
                }
                return@launch
            }
            if (habitCalendar != null && (selectedDate!=todayDateString)) {
                todayInCalendar = habitCalendar
                todayHabits = todayInCalendar.habitsInADay as ArrayList<HabitInADay>
                generateTodayHabitList()
                return@launch
            }
            todayInCalendar = HabitCalendar(selectedDate, selectedWeekDay, emptyList())
            generateTodayHabitList()
        }
    }

    private fun generateTodayHabitList() {
        mainScope.launch {
            var finishedHabits = 0f
            habitsToShow = ArrayList()
            for (habitInT in todayInCalendar.habitsInADay) {
                Log.d("HabitToShow", "Searching for ${habitInT.habitId}")
                val habit = dataSource.getHabitByIdAsync(habitInT.habitId).await()
                if (habit != null) {
                    Log.d("HabitToShow", "Found ${habit.id}")
                    val newHabitToShow = TodayHabit(habit, habitInT.isFinished, habitInT.goalTimesCount, habitInT.goalDurationCount)
                    if (habitInT.isFinished) {
                        finishedHabits += 1f
                    }
                    if (habitInT.goalTimesCount < habit.repetitionGoalCount) {
                        finishedHabits += (habitInT.goalTimesCount.toFloat()/habit.repetitionGoalCount)
                    }
                    if (habitInT.goalDurationCount < habit.repetitionGoalDuration) {
                        finishedHabits += (habitInT.goalDurationCount.toFloat() / habit.repetitionGoalDuration)
                    }
                    habitsToShow.add(newHabitToShow)
                }
            }
            Log.d("TotalHabitsToSow", "${habitsToShow.size}")
            val adapter = HabitAdapter(habitsToShow, isCurrentSelectionIsFuture())
            binding.todayHabitRecycler.adapter = adapter
            totalHabitOfDay = habitsToShow.size
            updateCounters(true, finishedHabits)
            setDoItCardListeners()
        }
    }

    private fun updateCounters(reload : Boolean, countChange: Float) {
        if (reload) {
            finishedHabitCountInDay = countChange
        } else {
            finishedHabitCountInDay += countChange
        }
        binding.completedCountText.text = "$totalHabitOfDay Habits"
        val completed = (finishedHabitCountInDay *100 / totalHabitOfDay).toInt()
        binding.completedPercentageText.text = "COMPLETED $completed %"
        dataSource.updateCompletedInCalendar(selectedDate, completed) { }
    }

    // handle do it at card options
    private fun setDoItCardListeners() {
        val future = isCurrentSelectionIsFuture()
        val mBinding = binding.morningCardLayout
        mBinding.doItAnytimeCard.setOnClickListener {
            setUpDoItAtTiming(Constant.HABIT_DO_IT_ANYTIME, mBinding)
            binding.todayHabitRecycler.adapter = HabitAdapter(habitsToShow, future)
        }
        mBinding.doItMorningCard.setOnClickListener {
            setUpDoItAtTiming(Constant.HABIT_DO_IT_MORNING, mBinding)
            val morningHabits = ArrayList<TodayHabit>()
            for (habit in habitsToShow) {
                if (habit.habit.doItAtTime == Constant.HABIT_DO_IT_ANYTIME) {
                    morningHabits.add(habit)
                    continue
                }
                if (habit.habit.doItAtTime == Constant.HABIT_DO_IT_MORNING) {
                    morningHabits.add(habit)
                }
            }
            binding.todayHabitRecycler.adapter = HabitAdapter(morningHabits, future)
        }
        mBinding.doItAfternoonCard.setOnClickListener {
            setUpDoItAtTiming(Constant.HABIT_DO_IT_AFTERNOON, mBinding)
            val afternoonHabits = ArrayList<TodayHabit>()
            for (habit in habitsToShow) {
                if (habit.habit.doItAtTime == Constant.HABIT_DO_IT_ANYTIME) {
                    afternoonHabits.add(habit)
                    continue
                }
                if (habit.habit.doItAtTime == Constant.HABIT_DO_IT_AFTERNOON) {
                    afternoonHabits.add(habit)
                }
            }
            binding.todayHabitRecycler.adapter = HabitAdapter(afternoonHabits, future)
        }
        mBinding.doItEveningCard.setOnClickListener {
            setUpDoItAtTiming(Constant.HABIT_DO_IT_EVENING, mBinding)
            val eveningHabits = ArrayList<TodayHabit>()
            for (habit in habitsToShow) {
                if (habit.habit.doItAtTime == Constant.HABIT_DO_IT_ANYTIME) {
                    eveningHabits.add(habit)
                    continue
                }
                if (habit.habit.doItAtTime == Constant.HABIT_DO_IT_EVENING) {
                    eveningHabits.add(habit)
                }
            }
            binding.todayHabitRecycler.adapter = HabitAdapter(eveningHabits, future)
        }
        // show habit as per current time
        when(Constant.getCurrentTimePeriod(dataSource.getTimePeriodData() ?: TimePeriodData())) {
            Constant.CURRENT_TIME_MORNING -> {
                mBinding.doItMorningCard.performClick()
            }
            Constant.CURRENT_TIME_AFTERNOON -> {
                mBinding.doItAfternoonCard.performClick()
            }
            Constant.CURRENT_TIME_EVENING -> {
                mBinding.doItEveningCard.performClick()
            }
        }
    }

    private fun changeHabitStatus(id: Int, status: Boolean) {
        val habitToShow = habitsToShow.find { it.habit.id == id }
        habitToShow?.finished = status
        dataSource.changeHabitStatus(selectedDate, id, status)
    }

    private fun setUpDoItAtTiming(time: Int, mBinding: OtherMorningCardsBinding) : Int{
        mBinding.doItAnytimeCard.isSelected = false
        mBinding.doItMorningCard.isSelected = false
        mBinding.doItAfternoonCard.isSelected = false
        mBinding.doItEveningCard.isSelected = false
        when(time) {
            Constant.HABIT_DO_IT_ANYTIME -> mBinding.doItAnytimeCard.isSelected = true
            Constant.HABIT_DO_IT_MORNING -> mBinding.doItMorningCard.isSelected = true
            Constant.HABIT_DO_IT_AFTERNOON -> mBinding.doItAfternoonCard.isSelected = true
            Constant.HABIT_DO_IT_EVENING -> mBinding.doItEveningCard.isSelected = true
        }
        return time
    }

    inner class HabitAdapter(val habits : List<TodayHabit>, val isFuture: Boolean) : RecyclerView.Adapter<HabitAdapter.HabitHolder>() {

        inner class HabitHolder(val binding : RecyclerItemRegularHabitBinding) :
                RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitHolder =
            HabitHolder(RecyclerItemRegularHabitBinding.inflate(layoutInflater, parent, false))

        override fun onBindViewHolder(holder: HabitHolder, position: Int) {
            val habit = habits[position]
            var extraOptionType = -1
            var isExtraCardOpen = false
            holder.binding.titleText.text = habit.habit.title
            holder.binding.habitIcon.text = habit.habit.icon
            if (isFuture) {
                holder.binding.checkboxUnchecked.visibility = View.GONE
                if (habit.habit.repetitionGoalCount > 1) {
                    holder.binding.habitInfoText.text = "0/${habit.habit.repetitionGoalCount} repetition completed"
                }
                if (habit.habit.repetitionGoalDuration > 0) {
                    holder.binding.habitInfoText.text =
                        Constant.convertSecondsToText(habit.durationCount) + " / "+ Constant.convertSecondsToText(habit.habit.repetitionGoalDuration)
                }
            //    holder.binding.timerClock.visibility = View.GONE
                return
            }
            if (habit.habit.repetitionType != Constant.HABIT_REPEAT_AS_WEEKDAY) {
                holder.binding.daysCompletionText.visibility = View.VISIBLE
                val afterText = when(habit.habit.repetitionType) {
                    Constant.HABIT_REPEAT_IN_WEEK -> "Days in A Week"
                    Constant.HABIT_REPEAT_IN_MONTH -> "Days in A month"
                    Constant.HABIT_REPEAT_IN_YEAR -> "Days in A year"
                    else -> "Error"
                }
                holder.binding.daysCompletionText.text =
                    "${habit.habit.repetitionDaysCompleted} / ${habit.habit.repetitionDaysCount} $afterText"
            }
            if (habit.habit.repetitionGoalDuration > 0) {
                holder.binding.timerClock.visibility = View.VISIBLE
                holder.binding.checkboxUnchecked.visibility = View.GONE
                holder.binding.timerClock.setOnClickListener {
                    setExtraCardOptions(extraOptionType, !isExtraCardOpen, holder.binding, habit, position, holder.binding.daysCompletionText)
                    isExtraCardOpen = !isExtraCardOpen
                }
                holder.binding.habitInfoText.text =
                    Constant.convertSecondsToText(habit.durationCount) + " / "+ Constant.convertSecondsToText(habit.habit.repetitionGoalDuration)
                extraOptionType = if (habit.finished) {
                    EXTRA_OPTIONS_TYPE_GOAL_TIMER_FINISHED
                } else {
                    EXTRA_OPTIONS_TYPE_GOAL_TIMER
                }
            }
            if (habit.habit.repetitionGoalCount > 1) {
                holder.binding.habitInfoText.text = "${habit.goalCount}/${habit.habit.repetitionGoalCount} repetition completed"
                holder.binding.checkboxUnchecked.visibility = View.GONE
                extraOptionType = if (habit.finished) {
                    EXTRA_OPTIONS_TYPE_GOAL_COUNTER_FINISHED
                } else {
                    EXTRA_OPTIONS_TYPE_GOAL_COUNTER
                }
            }
            if (habit.finished) {
                changeCardColor(holder.binding, true)
            }
            holder.binding.extraOptButton.setOnClickListener {
                setExtraCardOptions(extraOptionType, !isExtraCardOpen, holder.binding, habit, position, holder.binding.daysCompletionText)
                isExtraCardOpen = !isExtraCardOpen
            }
            holder.binding.checkboxUnchecked.setOnClickListener {
                habit.finished = !habit.finished
                changeCardColor(holder.binding, habit.finished)
                changeHabitStatus(habit.habit.id, habit.finished)
                val countChange = if (habit.finished) 1f else -1f
                updateCounters(false, countChange)
                if (habit.habit.repetitionType != Constant.HABIT_REPEAT_AS_WEEKDAY) {
                    if (habit.finished) {
                        changeHabitDaysCount(1, habit, holder.binding.daysCompletionText)
                    } else {
                        changeHabitDaysCount(-1, habit, holder.binding.daysCompletionText)
                    }
                }
            }
        }

        override fun getItemCount(): Int = habits.size

        private fun changeCardColor(cBinding : RecyclerItemRegularHabitBinding, toDark : Boolean) {
            if (toDark) {
                cBinding.checkboxUnchecked.setImageDrawable(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.checkbox_checked, ctx.resources.newTheme()))
                cBinding.mainCard.setCardBackgroundColor(
                    ResourcesCompat.getColor(ctx.resources, R.color.black_light, resources.newTheme())
                )
                cBinding.titleText.setTextColor(Color.WHITE)
                cBinding.habitInfoText.setTextColor(Color.WHITE)
            } else {
                cBinding.checkboxUnchecked.setImageDrawable(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.checkbox_unchecked, ctx.resources.newTheme()))
                cBinding.mainCard.setCardBackgroundColor(
                    ResourcesCompat.getColor(ctx.resources, R.color.card_view_color, resources.newTheme())
                )
                cBinding.titleText.setTextColor(Color.BLACK)
                cBinding.habitInfoText.setTextColor(Color.BLACK)
            }
        }

        private fun setExtraCardOptions(optionTypes: Int, toOpen: Boolean, binding: RecyclerItemRegularHabitBinding, habit: TodayHabit, position: Int,
            completionText: TextView) {
            if (toOpen) {
                when(optionTypes) {
                    EXTRA_OPTIONS_TYPE_GOAL_COUNTER -> {
                        binding.goalCountLayout.visibility = View.VISIBLE
                        binding.countPicker.maxValue = habit.habit.repetitionGoalCount
                        binding.countPicker.minValue = 2
                        binding.finish1RepLayout.setOnClickListener {
                            updateCounters(false, -(habit.goalCount.toFloat()/habit.habit.repetitionGoalCount))
                            if (habit.goalCount < habit.habit.repetitionGoalCount) {
                                habit.goalCount += 1
                                dataSource.changeHabitGoalCount(habit.goalCount, selectedDate, habit.habit.id)
                                updateCounters(false, (habit.goalCount.toFloat()/habit.habit.repetitionGoalCount))
                            }
                            if (habit.goalCount == habit.habit.repetitionGoalCount) {
                                habit.finished = true
                                if (habit.habit.repetitionType != Constant.HABIT_REPEAT_AS_WEEKDAY) {
                                    changeHabitDaysCount(1, habit, completionText)
                                }
                            }
                            notifyItemChanged(position)
                            binding.goalCountLayout.visibility = View.GONE
                        }
                        binding.applyNumberPicker.setOnClickListener {
                            Log.d("NumberPickerSelected", "${binding.countPicker.value}")
                            updateCounters(false, -(habit.goalCount.toFloat()/habit.habit.repetitionGoalCount))
                            habit.goalCount = (binding.countPicker.value - 1)
                            dataSource.changeHabitGoalCount(habit.goalCount, selectedDate, habit.habit.id)
                            updateCounters(false, (habit.goalCount.toFloat()/habit.habit.repetitionGoalCount))
                            if (habit.goalCount == habit.habit.repetitionGoalCount) {
                                habit.finished = true
                                if (habit.habit.repetitionType != Constant.HABIT_REPEAT_AS_WEEKDAY) {
                                    changeHabitDaysCount(1, habit, completionText)
                                }
                            }
                            notifyItemChanged(position)
                            binding.goalCountLayout.visibility = View.GONE
                        }
                        binding.finishAllLayout.setOnClickListener {
                            updateCounters(false, -(habit.goalCount.toFloat()/habit.habit.repetitionGoalCount))
                            dataSource.changeHabitGoalCount(-1, selectedDate, habit.habit.id)
                            habit.goalCount = habit.habit.repetitionGoalCount
                            habit.finished = true
                            if (habit.habit.repetitionType != Constant.HABIT_REPEAT_AS_WEEKDAY) {
                                changeHabitDaysCount(1, habit, completionText)
                            }
                            notifyItemChanged(position)
                            updateCounters(false, 1f)
                            binding.goalCountLayout.visibility = View.GONE
                        }
                    }
                    EXTRA_OPTIONS_TYPE_GOAL_COUNTER_FINISHED -> {
                        binding.goalCountFinishedLayout.visibility = View.VISIBLE
                        binding.undoLayout.setOnClickListener {
                            dataSource.changeHabitGoalCount(0, selectedDate, habit.habit.id)
                            habit.goalCount = 0
                            habit.finished = false
                            if (habit.habit.repetitionType != Constant.HABIT_REPEAT_AS_WEEKDAY) {
                                changeHabitDaysCount(-1, habit, completionText)
                            }
                            notifyItemChanged(position)
                            binding.goalCountFinishedLayout.visibility = View.GONE
                            updateCounters(false,-1f)
                        }
                        binding.takeANoteLayout.setOnClickListener {

                        }
                        binding.editLayout.setOnClickListener {

                        }
                    }
                    EXTRA_OPTIONS_TYPE_GOAL_TIMER -> {
                        binding.goalTimerLayout.visibility = View.VISIBLE
                        binding.extraTimerButton.setOnClickListener {
                            TimerFragment.habitGoalDuration = habit.habit.repetitionGoalDuration
                            TimerFragment.habitDurationFinished = habit.durationCount
                            TimerFragment.habitName = habit.habit.title
                            TimerFragment.selectedDate = selectedDate
                            TimerFragment.habitId = habit.habit.id
                            findNavController().navigate(R.id.action_navigation_today_to_timerFragment)
                        }
                        binding.extraFinishButton.setOnClickListener {
                            updateCounters(false, -(habit.durationCount.toFloat()/habit.habit.repetitionGoalDuration))
                            dataSource.changeHabitDurationCount(-1, selectedDate, habit.habit.id)
                            habit.durationCount = habit.habit.repetitionGoalDuration
                            habit.finished = true
                            if (habit.habit.repetitionType != Constant.HABIT_REPEAT_AS_WEEKDAY) {
                                changeHabitDaysCount(1, habit, completionText)
                            }
                            notifyItemChanged(position)
                            updateCounters(false, 1f)
                            binding.goalCountLayout.visibility = View.GONE
                        }
                    }
                    EXTRA_OPTIONS_TYPE_GOAL_TIMER_FINISHED -> {
                        binding.goalCountFinishedLayout.visibility = View.VISIBLE
                        binding.undoLayout.setOnClickListener {
                            dataSource.changeHabitDurationCount(0, selectedDate, habit.habit.id)
                            habit.durationCount = 0
                            habit.finished = false
                            if (habit.habit.repetitionType != Constant.HABIT_REPEAT_AS_WEEKDAY) {
                                changeHabitDaysCount(-1, habit, completionText)
                            }
                            notifyItemChanged(position)
                            binding.goalCountFinishedLayout.visibility = View.GONE
                            updateCounters(false,-1f)
                        }
                        binding.takeANoteLayout.setOnClickListener {

                        }
                        binding.editLayout.setOnClickListener {

                        }
                    }
                }
            } else {
                when(optionTypes) {
                    EXTRA_OPTIONS_TYPE_GOAL_COUNTER -> {
                        binding.goalCountLayout.visibility = View.GONE
                    }
                    EXTRA_OPTIONS_TYPE_GOAL_COUNTER_FINISHED -> {
                        binding.goalCountFinishedLayout.visibility = View.GONE
                    }
                    EXTRA_OPTIONS_TYPE_GOAL_TIMER -> {
                        binding.goalTimerLayout.visibility = View.GONE
                    }
                    EXTRA_OPTIONS_TYPE_GOAL_TIMER_FINISHED -> {
                        binding.goalCountFinishedLayout.visibility = View.GONE
                    }
                }
            }
        }

        private fun changeHabitDaysCount(diff: Int, habit: TodayHabit, completionText: TextView) {
            habit.habit.repetitionDaysCompleted += diff
            val afterText = when(habit.habit.repetitionType) {
                Constant.HABIT_REPEAT_IN_WEEK -> "Days in a Week"
                Constant.HABIT_REPEAT_IN_MONTH -> "Days in a month"
                Constant.HABIT_REPEAT_IN_YEAR -> "Days in a year"
                else -> "Error"
            }
            dataSource.updateHabitDayCount(habit.habit.repetitionDaysCompleted, habit.habit.id)
            completionText.text =
                "${habit.habit.repetitionDaysCompleted} / ${habit.habit.repetitionDaysCount} $afterText"
        }
    }

    class TodayHabit(val habit: Habit, var finished: Boolean = false, var goalCount: Int, var durationCount : Int)

    private fun isCurrentSelectionIsFuture() : Boolean {
        val month = viewModel.selectedWeekCalendarDate.value?.get(1) ?: 0
        val date = viewModel.selectedWeekCalendarDate.value?.get(0) ?: 0
        val todayIs = Constant.getTodayArray()
        if (month > todayIs[1]) {
            return true
        }
        if (month < todayIs[1]) {
            return false
        }
        if (month == todayIs[1]) {
            return date > todayIs[0]
        }
        return false
    }
}