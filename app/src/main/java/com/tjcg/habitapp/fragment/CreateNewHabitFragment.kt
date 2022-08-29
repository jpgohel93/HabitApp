package com.tjcg.habitapp.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tjcg.habitapp.LoginActivity
import com.tjcg.habitapp.MainActivity
import com.tjcg.habitapp.R
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.data.Habit
import com.tjcg.habitapp.data.HabitDataSource
import com.tjcg.habitapp.databinding.*
import com.tjcg.habitapp.viewmodel.HabitViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class CreateNewHabitFragment : Fragment(), View.OnClickListener {

    @Inject lateinit var dataSource : HabitDataSource
    private lateinit var bindingMain: FragmentCreateHabitBinding
    private lateinit var binding : FragmentCreateHabitContentBinding
    private lateinit var repetitionBinding : OtherHabitRepeatationSetupBinding
    private lateinit var morningCardsBinding: OtherMorningCardsBinding
    private var isAdvanceOpen = false
    private var weekdayList = ArrayList<Int>()
    private lateinit var animationHandler: Handler
    private lateinit var ctx: Context

    private lateinit var habitViewModel : HabitViewModel

    private var currentlySelectedRepetition = 1
    private var daysRepetitionCount = 1
    private var habitToEdit : Habit? = null

    private val mainScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (MainActivity.isNavShowing) {
            MainActivity.hideBottomNavigation()
        }
        ctx = findNavController().context
 //       MainActivity.currentPage = Constant.PAGE_IN
        bindingMain = FragmentCreateHabitBinding.inflate(layoutInflater)
        binding = bindingMain.content
        morningCardsBinding = binding.morningCardLayout
        habitViewModel = dataSource.provideViewModel()
        animationHandler = Handler(Looper.getMainLooper())

        //get habit to edit if in edit mode
        mainScope.launch {
            habitToEdit = dataSource.getHabitByIdAsync(editHabitId).await()
            if (habitToEdit != null) {
                bindingMain.titleText.text = habitToEdit?.title
                bindingMain.titleTextEdit.setText(habitToEdit?.title)
                if (!iconChanged) {
                    bindingMain.habitIcon.text = habitToEdit?.icon
                } else {
                    iconChanged = false
                }
            }
        }

        // toolbar operations
        // change habit name...
        bindingMain.editHabitName.setOnClickListener {
            bindingMain.saveHabitName.visibility = View.VISIBLE
            it.visibility = View.GONE
            bindingMain.titleText.visibility = View.GONE
            bindingMain.titleTextEdit.visibility = View.VISIBLE
            bindingMain.titleTextEdit.requestFocus()
            val inputManager =
                ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(bindingMain.titleTextEdit, InputMethodManager.SHOW_FORCED)
            bindingMain.saveHabitName.setOnClickListener { save ->
                if (!bindingMain.titleTextEdit.text.isNullOrBlank()) {
                    habitViewModel.setHabitName(bindingMain.titleTextEdit.text.toString())
                }
                save.visibility = View.GONE
                it.visibility = View.VISIBLE
                bindingMain.titleTextEdit.visibility = View.GONE
                bindingMain.titleText.visibility = View.VISIBLE
            }
        }

        // change habit icon
        habitViewModel.habitIcon.observe(viewLifecycleOwner) { icon ->
            bindingMain.habitIcon.text = icon
        }
        bindingMain.habitIcon.setOnClickListener {
            if (editHabit) {
                findNavController().navigate(R.id.action_navigation_habitEdit_to_navigation_habitIcons)
            } else {
                findNavController().navigate(R.id.action_bottom_createNewHabitFragment_to_bottom_iconListFragment)
            }
        }

        // handle repetition options
        /*     binding.daysPerWeekPicker.minValue = 1
             binding.daysPerMonthPicker.minValue = 1
             binding.daysPerYearPicker.minValue = 1
             binding.daysPerWeekPicker.maxValue = 6
             binding.daysPerMonthPicker.maxValue = 10
             binding.daysPerYearPicker.maxValue = 30

             binding.editRepeatation.setOnClickListener { edit ->
                 expandCard(binding.repeatationRadioGroup)
                 edit.visibility = View.GONE
                 binding.saveRepeatation.visibility = View.VISIBLE
                 binding.saveRepeatation.setOnClickListener { save ->
                     //TODO("save users data here")
                     save.visibility = View.GONE
                     collapseCard(binding.repeatationRadioGroup)
                     edit.visibility = View.VISIBLE
                 }
             }

             binding.checkboxWeekdays.setOnCheckedChangeListener { _, b ->
                 if (b) {
                     expandCard(binding.repeatationWeekdaysLayout)
                 } else {
                     collapseCard(binding.repeatationWeekdaysLayout)
                 }
             }

             binding.checkboxDaysPerWeek.setOnCheckedChangeListener { _, b ->
                 if (b) {
                     expandCard(binding.daysPerWeekPicker)
                 } else {
                     collapseCard(binding.daysPerWeekPicker)
                 }
             }

             binding.checkboxDaysPerMonth.setOnCheckedChangeListener { _, b ->
                 if (b) {
                     expandCard(binding.daysPerMonthPicker)
                 } else {
                     collapseCard(binding.daysPerMonthPicker)
                 }
             }

             binding.checkboxDaysPerYear.setOnCheckedChangeListener { _, b ->
                 if (b) {
                     expandCard(binding.daysPerYearPicker)
                 } else {
                     collapseCard(binding.daysPerYearPicker)
                 }
             }  */

        // handle repetition settings
  /*      addDayInArray(Calendar.SUNDAY)
        addDayInArray(Calendar.MONDAY)
        addDayInArray(Calendar.TUESDAY)
        addDayInArray(Calendar.WEDNESDAY)
        addDayInArray(Calendar.THURSDAY)
        addDayInArray(Calendar.FRIDAY)
        addDayInArray(Calendar.SATURDAY)
        repetitionBinding.sun.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                addDayInArray(Calendar.SUNDAY)
            } else {
                removeDayInArray(Calendar.SUNDAY)
            }
        }
        repetitionBinding.mon.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                addDayInArray(Calendar.MONDAY)
            } else {
                removeDayInArray(Calendar.MONDAY)
            }
        }
        repetitionBinding.tue.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                addDayInArray(Calendar.TUESDAY)
            } else {
                removeDayInArray(Calendar.TUESDAY)
            }
        }
        repetitionBinding.wed.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                addDayInArray(Calendar.WEDNESDAY)
            } else {
                removeDayInArray(Calendar.WEDNESDAY)
            }
        }
        repetitionBinding.thu.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                addDayInArray(Calendar.THURSDAY)
            } else {
                removeDayInArray(Calendar.THURSDAY)
            }
        }
        repetitionBinding.fri.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                addDayInArray(Calendar.FRIDAY)
            } else {
                removeDayInArray(Calendar.FRIDAY)
            }
        }
        repetitionBinding.sat.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                addDayInArray(Calendar.SATURDAY)
            } else {
                removeDayInArray(Calendar.SATURDAY)
            }
        }  */
        repetitionBinding = binding.repetitionLayout
        repetitionBinding.monthDayPicker.maxValue = 10
        repetitionBinding.monthDayPicker.minValue = 1
        repetitionBinding.yearDayPicker.maxValue = 30
        repetitionBinding.yearDayPicker.minValue = 1
        repetitionBinding.monthDayPicker.setOnValueChangedListener { numberPicker, i, i2 ->
            repetitionBinding.monthDayPickerText.text = "$i2 days in Month"
            daysRepetitionCount = i2
        }
        repetitionBinding.yearDayPicker.setOnValueChangedListener { numberPicker, i, i2 ->
            repetitionBinding.yearDayPickerText.text = "$i2 days in year"
            daysRepetitionCount = i2
        }
        repetitionBinding.editRepeatation.setOnClickListener {
            repetitionBinding.repeatationSettingsCollapsed.visibility = View.VISIBLE
            repetitionBinding.repetationInfoLayout.visibility = View.GONE
            it.visibility = View.GONE
            repetitionBinding.saveRepeatation.visibility = View.VISIBLE
        }

        repetitionBinding.weekRadio1.setOnCheckedChangeListener { compoundButton, b ->
            if(b) {
                updateDaysPerWeekCount(1)
            }
        }
        repetitionBinding.weekRadio2.setOnCheckedChangeListener { compoundButton, b ->
            if(b) {
                updateDaysPerWeekCount(2)
            }
        }
        repetitionBinding.weekRadio3.setOnCheckedChangeListener { compoundButton, b ->
            if(b) {
                updateDaysPerWeekCount(3)
            }
        }
        repetitionBinding.weekRadio4.setOnCheckedChangeListener { compoundButton, b ->
            if(b) {
                updateDaysPerWeekCount(4)
            }
        }
        repetitionBinding.weekRadio5.setOnCheckedChangeListener { compoundButton, b ->
            if(b) {
                updateDaysPerWeekCount(5)
            }
        }
        repetitionBinding.weekRadio6.setOnCheckedChangeListener { compoundButton, b ->
            if(b) {
                updateDaysPerWeekCount(6)
            }
        }
        repetitionBinding.saveRepeatation.setOnClickListener {
            repetitionBinding.editRepeatation.visibility = View.VISIBLE
            it.visibility = View.GONE
            repetitionBinding.repeatationSettingsCollapsed.visibility = View.GONE
            repetitionBinding.repetationInfoLayout.visibility = View.VISIBLE
            if (currentlySelectedRepetition == Constant.HABIT_REPEAT_AS_WEEKDAY) {
                repetitionBinding.repeatationWeekdaysLayout.visibility = View.VISIBLE
                repetitionBinding.perDayRepeatationText.visibility = View.GONE
            } else {
                repetitionBinding.repeatationWeekdaysLayout.visibility = View.GONE
                repetitionBinding.perDayRepeatationText.visibility = View.VISIBLE
                val scopeText : String = when (currentlySelectedRepetition) {
                    Constant.HABIT_REPEAT_IN_WEEK -> "In Week"
                    Constant.HABIT_REPEAT_IN_MONTH -> "In Month"
                    Constant.HABIT_REPEAT_IN_YEAR -> "In Year"
                    else -> "Error"
                }
                repetitionBinding.perDayRepeatationText.text = "$daysRepetitionCount Days $scopeText"
            }
            Log.d("currentSelection", "$currentlySelectedRepetition")
        }
        repetitionBinding.repChecked1.setOnClickListener(this)
        repetitionBinding.repChecked2.setOnClickListener(this)
        repetitionBinding.repChecked3.setOnClickListener(this)
        repetitionBinding.repChecked4.setOnClickListener(this)
        // already set for edit mode
        if (editHabit) {
            mainScope.launch {
                while (habitToEdit == null) { delay(500) }
                Log.d("EditRepetationType", "${habitToEdit?.title} - ${habitToEdit?.repetitionType}")
                when (habitToEdit?.repetitionType) {
                    Constant.HABIT_REPEAT_AS_WEEKDAY -> {
                        val repetitionDaysArray = habitToEdit?.repetitionDaysArray
                        repetitionBinding.sun.isChecked = repetitionDaysArray?.contains("1") == true
                        repetitionBinding.mon.isChecked = repetitionDaysArray?.contains("2") == true
                        repetitionBinding.tue.isChecked = repetitionDaysArray?.contains("3") == true
                        repetitionBinding.wed.isChecked = repetitionDaysArray?.contains("4") == true
                        repetitionBinding.thu.isChecked = repetitionDaysArray?.contains("5") == true
                        repetitionBinding.fri.isChecked = repetitionDaysArray?.contains("6") == true
                        repetitionBinding.sat.isChecked = repetitionDaysArray?.contains("7") == true
                    }
                    Constant.HABIT_REPEAT_IN_WEEK -> {
                        repetitionBinding.editRepeatation.performClick()
                        repetitionBinding.repChecked2.performClick()
                    }
                    Constant.HABIT_REPEAT_IN_MONTH -> {
                        repetitionBinding.editRepeatation.performClick()
                        repetitionBinding.repChecked3.performClick()
                        repetitionBinding.monthDayPicker.value = habitToEdit?.repetitionDaysCount ?: 2
                    }
                    Constant.HABIT_REPEAT_IN_YEAR -> {
                        repetitionBinding.editRepeatation.performClick()
                        repetitionBinding.repChecked4.performClick()
                    }
                }
            }
        }

        // handle goal options
        var goalHours = 0
        var goalMinutes = 0
        var goalRepetitionCount = 1
        binding.goalHourPicker.maxValue = 23
        binding.goalMinutePicker.maxValue = 59
        binding.goalMinutePicker.value = 5
        binding.goalHourPicker.setOnValueChangedListener { _, _, i2 ->
            goalHours = i2
        }
        binding.goalMinutePicker.setOnValueChangedListener { _, _, i2 ->
            goalMinutes = i2
        }
        binding.goalRepeatPicker.minValue = 2
        binding.goalRepeatPicker.maxValue = 100
        binding.goalRepeatPicker.setOnValueChangedListener { _, _, i2 ->
            goalRepetitionCount = i2
        }
        var goalSwitchSilentCheck = false
        binding.goalSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if (goalSwitchSilentCheck) {
                // Do nothing, required no action when auto on in edit mode
                goalSwitchSilentCheck = false
                return@setOnCheckedChangeListener
            }
            if (b) {
                BottomSheetDialog(this.requireContext()).apply {
                    val sheetBinding = BottomsheetDailyGoalBinding.inflate(layoutInflater)
                    sheetBinding.closeBtn.setOnClickListener {
                        compoundButton.isChecked = false
                        this.cancel()
                    }
                    sheetBinding.durationBtn.setOnClickListener {
                        binding.goalCollapseDurationLayout.visibility = View.VISIBLE
                        //  expandCard(binding.goalCollapseDurationLayout)
                        this.cancel()
                    }
                    sheetBinding.repeatBtn.setOnClickListener {
                        binding.goalCollapseRepeatLayout.visibility = View.VISIBLE
                        //    expandCard(binding.goalCollapseRepeatLayout)
                        this.cancel()
                    }
                    setContentView(sheetBinding.root)
                }.show()
            } else {
                // TODO("Change user data accordingly")
                binding.goalCollapseDurationLayout.visibility = View.GONE
                binding.goalCollapseRepeatLayout.visibility = View.GONE
                //   collapseCard(binding.goalCollapseDurationLayout)
                //   collapseCard(binding.goalCollapseRepeatLayout)
            }
        }
        // set up for edit mode
        if (editHabit) {
            mainScope.launch {
                while (habitToEdit == null) { delay(500) }
                if ((habitToEdit?.repetitionGoalCount ?: 1) > 1) {
                    goalSwitchSilentCheck = true
                    binding.goalSwitch.isChecked = true
                    binding.goalCollapseRepeatLayout.visibility = View.VISIBLE
                    binding.goalRepeatPicker.value = habitToEdit?.repetitionGoalCount ?: 2
                    return@launch
                }
                if ((habitToEdit?.repetitionGoalDuration ?: 0) > 0) {
                    goalSwitchSilentCheck = true
                    binding.goalSwitch.isChecked = true
                    binding.goalCollapseDurationLayout.visibility = View.VISIBLE
                    val goalDuration = habitToEdit?.repetitionGoalDuration ?: 0
                    val goalHours1 = goalDuration / 3600
                    val goalMinutes1 = (goalDuration % 3600) / 60
                    binding.goalHourPicker.value = goalHours1
                    binding.goalMinutePicker.value = goalMinutes1
                }
            }
        }



        setUpDoItAtTiming(Constant.HABIT_DO_IT_ANYTIME)
        var doItTime = Constant.HABIT_DO_IT_ANYTIME
        // TODO("handle day timing or do it at")
        morningCardsBinding.doItAnytimeCard.setOnClickListener {
            doItTime = setUpDoItAtTiming(Constant.HABIT_DO_IT_ANYTIME)
        }
        morningCardsBinding.doItMorningCard.setOnClickListener {
            doItTime = setUpDoItAtTiming(Constant.HABIT_DO_IT_MORNING)
        }
        morningCardsBinding.doItAfternoonCard.setOnClickListener {
            doItTime = setUpDoItAtTiming(Constant.HABIT_DO_IT_AFTERNOON)
        }
        morningCardsBinding.doItEveningCard.setOnClickListener {
            doItTime = setUpDoItAtTiming(Constant.HABIT_DO_IT_EVENING)
        }
        //set up for edit mode
        if (editHabit) {
            mainScope.launch {
                while (habitToEdit == null) { delay(500) }
                when(habitToEdit?.doItAtTime) {
                    Constant.HABIT_DO_IT_ANYTIME -> morningCardsBinding.doItAnytimeCard.performClick()
                    Constant.HABIT_DO_IT_MORNING -> morningCardsBinding.doItMorningCard.performClick()
                    Constant.HABIT_DO_IT_AFTERNOON -> morningCardsBinding.doItAfternoonCard.performClick()
                    Constant.HABIT_DO_IT_EVENING -> morningCardsBinding.doItEveningCard.performClick()
                }
            }
        }


        // handle advance settings
        binding.advanceSettingsToggleLayout.setOnClickListener {
            if (isAdvanceOpen) {
                binding.advanceBottomCard.visibility = View.GONE
                binding.advanceCollapsedView.visibility = View.GONE
                //    collapseCard(binding.advanceCollapsedView)
                //    collapseCard(binding.advanceBottomCard)
                isAdvanceOpen = false
                MainActivity.rotateWhileCollapse(binding.advanceSettingArrow)
                return@setOnClickListener
            }
            binding.advanceCollapsedView.visibility = View.VISIBLE
            binding.advanceBottomCard.visibility = View.VISIBLE
            //   expandCard(binding.advanceCollapsedView)
            //   expandCard(binding.advanceBottomCard)
            MainActivity.rotateWhileExpand(binding.advanceSettingArrow)
            isAdvanceOpen = true
        }
        // handle reminder settings
        var reminderSelectedTime = ""
        var reminderSelectedHour = 0
        var reminderSelectedMinutes = 0
        var reminderSilentSwitch = false
        binding.advanceReminderSwitch.setOnCheckedChangeListener { _, b ->
            if (reminderSilentSwitch) {
                // do nothing , checked for edit mode
                reminderSilentSwitch = false
            }
            // TODO("update user configuration here")
            if (b) {
                binding.reminderCollapsedLayout.visibility = View.VISIBLE
                //     expandCard(binding.reminderCollapsedLayout)
            } else {
                binding.reminderCollapsedLayout.visibility = View.GONE
                reminderSelectedTime = ""
                //     collapseCard(binding.reminderCollapsedLayout)
            }
        }
        binding.reminderHourPicker.maxValue = 23
        binding.reminderMinutePicker.maxValue = 59
        binding.reminderHourPicker.setOnValueChangedListener { numberPicker, i, i2 ->
            reminderSelectedHour = i2
            reminderSelectedTime = "$reminderSelectedHour:$reminderSelectedMinutes"
        }
        binding.reminderMinutePicker.setOnValueChangedListener { numberPicker, i, i2 ->
            reminderSelectedMinutes = i2
            reminderSelectedTime = "$reminderSelectedHour:$reminderSelectedMinutes"
        }
        // set up for edit mode
        if (editHabit) {
            mainScope.launch {
                while (habitToEdit == null) { delay(500) }
                if (!habitToEdit?.habitReminderTime.isNullOrBlank()) {
                    reminderSilentSwitch = true
                    binding.advanceReminderSwitch.isChecked = true
                    reminderSelectedTime = habitToEdit?.habitReminderTime ?: "0:0"
                    binding.reminderHourPicker.value =
                        (habitToEdit?.habitReminderTime?.split(":")?.get(0))?.toInt() ?: 0
                    binding.reminderMinutePicker.value =
                        (habitToEdit?.habitReminderTime?.split(":")?.get(1))?.toInt() ?: 0
                }
                // set encouragement text
                binding.encouragementEditText.setText(habitToEdit?.encouragementText)
            }
        }



        // TODO("handle Ends on selection")
        var habitEndType = Constant.HABIT_END_ON_NULL
        var endOnDate = ""
        var endOnDays = 3
        binding.endOnOff.isSelected = true
        binding.endOnOff.setOnClickListener {
            binding.endOnOff.isSelected = true
            binding.endOnDate.isSelected = false
            binding.endOnDays.isSelected = false
            habitEndType = Constant.HABIT_END_ON_NULL
        }
        binding.endOnDate.setOnClickListener {
            val bottomDialog = BottomSheetDialog(ctx)
            val bBinding = BottomSheetChooseTheDateBinding.inflate(layoutInflater)
            bBinding.datePicker.minDate = System.currentTimeMillis()
            bBinding.datePicker.maxDate = System.currentTimeMillis() + 31536000000
            bBinding.cancelButton.setOnClickListener {
                bottomDialog.dismiss()
            }
            bBinding.saveButton.setOnClickListener {
                binding.endOnOff.isSelected = false
                binding.endOnDate.isSelected = true
                binding.endOnDays.isSelected = false
                habitEndType = Constant.HABIT_END_ON_DATE
                endOnDate= "${bBinding.datePicker.year}-${bBinding.datePicker.month}-" +
                        "${bBinding.datePicker.dayOfMonth}"
                bottomDialog.dismiss()
            }
            bottomDialog.setContentView(bBinding.root)
            bottomDialog.show()
        }
        binding.endOnDays.setOnClickListener {
            val bottomDialog = BottomSheetDialog(ctx)
            val bBinding = BottomSheetChooseTheDaysBinding.inflate(layoutInflater)
            bBinding.daysPicker.minValue = 3
            bBinding.daysPicker.maxValue = 100
            bBinding.daysPicker.value = endOnDays
            bBinding.cancelButton.setOnClickListener {
                bottomDialog.dismiss()
            }
            bBinding.saveButton.setOnClickListener {
                binding.endOnOff.isSelected = false
                binding.endOnDate.isSelected =false
                binding.endOnDays.isSelected = true
                habitEndType = Constant.HABIT_END_ON_DAYS
                endOnDays = bBinding.daysPicker.value
                bottomDialog.dismiss()
            }
            bottomDialog.setContentView(bBinding.root)
            bottomDialog.show()
        }
        // for edit mode
        if (editHabit) {
            mainScope.launch {
                while (habitToEdit == null) { delay(500) }
                when(habitToEdit?.endsOnType) {
                    Constant.HABIT_END_ON_NULL -> binding.endOnOff.isSelected = true
                    Constant.HABIT_END_ON_DATE -> {
                        binding.endOnOff.isSelected = false
                        binding.endOnDate.isSelected = true
                        endOnDate = habitToEdit?.endsOnDate ?: "0000-00-00"
                    }
                    Constant.HABIT_END_ON_DAYS -> {
                        binding.endOnOff.isSelected = false
                        binding.endOnDays.isSelected = true
                        endOnDays = habitToEdit?.endsOnDays ?: 3
                    }
                }
            }
        }


        // save Habit
        bindingMain.saveHabitButton.setOnClickListener {
            val habitTitle = bindingMain.titleText.text.toString()
            val repetitionGoalDuration = ((goalHours * 60) + goalMinutes) * 60 // in seconds
            if(editHabit) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (habitToEdit != null) {
                        habitToEdit?.title = habitTitle
                        habitToEdit?.icon = bindingMain.habitIcon.text.toString()
                        habitToEdit?.repetitionType = currentlySelectedRepetition
                        habitToEdit?.repetitionDaysArray = createSelectedWeekdayArray()
                        habitToEdit?.repetitionDaysCount = daysRepetitionCount
                        habitToEdit?.repetitionGoalDuration = repetitionGoalDuration
                        habitToEdit?.repetitionGoalCount = goalRepetitionCount
                        habitToEdit?.doItAtTime = doItTime
                        habitToEdit?.habitReminderTime =reminderSelectedTime
                        // update sound effect here
                        habitToEdit?.encouragementText = binding.encouragementEditText.text.toString()
                        habitToEdit?.endsOnType = habitEndType
                        habitToEdit?.endsOnDate = endOnDate
                        habitToEdit?.endsOnDays = endOnDays
                        dataSource.updateHabitAsync(habitToEdit!!)
                    } else {
                        Toast.makeText(ctx, "An Error Occurred, Please try again", Toast.LENGTH_SHORT).show()
                    }
                    // restart app
                 //   dataSource.restartApp(ctx.applicationContext)
                    (ctx as MainActivity).finishAffinity()
                    ctx.startActivity(Intent(ctx, LoginActivity::class.java))
                }
            } else {
                val newHabit = Habit(bindingMain.habitIcon.text.toString(), habitTitle, 0,
                    currentlySelectedRepetition, createSelectedWeekdayArray(), daysRepetitionCount,
                    repetitionGoalDuration, goalRepetitionCount, doItTime, reminderSelectedTime, 0,
                    binding.encouragementEditText.text.toString(), habitEndType, endOnDate, endOnDays)
                dataSource.addHabit(newHabit)
                findNavController().navigateUp()
            }
        }

        // set up view model observers
        setupViewModelsObservers()

        return bindingMain.root
    }

    private fun setupViewModelsObservers() {
        habitViewModel.habitName.observe(viewLifecycleOwner) { name ->
            bindingMain.titleText.isSelected = true
            bindingMain.titleText.text = name
            bindingMain.titleTextEdit.setText(name)
        }
    }

    // update as per Weekday habits Array
    private fun addDayInArray(day: Int) {
        val available = weekdayList.find { it == day }
        if (available == null) {
            weekdayList.add(day)
        }
    }
    private fun removeDayInArray(day: Int) {
        val available = weekdayList.find { it == day }
        if (available != null) {
            weekdayList.remove(available)
        }
    }

    // days per week count text updated only for second selection in repetition
    private fun updateDaysPerWeekCount(count : Int) {
        daysRepetitionCount = count
        repetitionBinding.daysPerWeekText.text = "$daysRepetitionCount days per Week"
    }

    private fun setUpDoItAtTiming(time: Int) : Int{
        morningCardsBinding.doItAnytimeCard.isSelected = false
        morningCardsBinding.doItMorningCard.isSelected = false
        morningCardsBinding.doItAfternoonCard.isSelected = false
        morningCardsBinding.doItEveningCard.isSelected = false
        when(time) {
            Constant.HABIT_DO_IT_ANYTIME -> morningCardsBinding.doItAnytimeCard.isSelected = true
            Constant.HABIT_DO_IT_MORNING -> morningCardsBinding.doItMorningCard.isSelected = true
            Constant.HABIT_DO_IT_AFTERNOON -> morningCardsBinding.doItAfternoonCard.isSelected = true
            Constant.HABIT_DO_IT_EVENING -> morningCardsBinding.doItEveningCard.isSelected = true
        }
        return time
    }

    private fun createSelectedWeekdayArray() : String {
        if (currentlySelectedRepetition == Constant.HABIT_REPEAT_AS_WEEKDAY) {
            val weekdays = ArrayList<Int>()
            Log.d("WeekdayCheck", "Check")
            if (repetitionBinding.sun.isChecked) weekdays.add(Constant.SUNDAY)
            Log.d("WeekdayCheck", "Check")
            if (repetitionBinding.mon.isChecked) weekdays.add(Constant.MONDAY)
            Log.d("WeekdayCheck", "Check")
            if (repetitionBinding.tue.isChecked) weekdays.add(Constant.TUESDAY)
            Log.d("WeekdayCheck", "Check")
            if (repetitionBinding.wed.isChecked) weekdays.add(Constant.WEDNESDAY)
            Log.d("WeekdayCheck", "Check")
            if (repetitionBinding.thu.isChecked) weekdays.add(Constant.THURSDAY)
            if (repetitionBinding.fri.isChecked) weekdays.add(Constant.FRIDAY)
            if (repetitionBinding.sat.isChecked) weekdays.add(Constant.SATURDAY)
            return weekdays.joinToString(",")
        } else {
            Log.d("WeekdayCheck", "returning Emptyr")
            return ""
        }
    }

    // onClick Listener for Repetition settings
    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.repChecked1 -> selectRepetitionOption(1)
            R.id.repChecked2 -> selectRepetitionOption(2)
            R.id.repChecked3 -> selectRepetitionOption(3)
            R.id.repChecked4 -> selectRepetitionOption(4)
        }
    }

    private fun selectRepetitionOption(option : Int) {
        val unselectDra = ResourcesCompat.getDrawable(ctx.resources, R.drawable.checkbox_unchecked, ctx.theme)
        repetitionBinding.repChecked1.setImageDrawable(unselectDra)
        repetitionBinding.repChecked2.setImageDrawable(unselectDra)
        repetitionBinding.repChecked3.setImageDrawable(unselectDra)
        repetitionBinding.repChecked4.setImageDrawable(unselectDra)
        val selectDra = ResourcesCompat.getDrawable(ctx.resources, R.drawable.checkbox_checked, ctx.theme)
        repetitionBinding.xDaysPerWeekCollapsed.visibility = View.GONE
        repetitionBinding.xDaysPerMonthCollapsed.visibility = View.GONE
        repetitionBinding.xDaysPerYearCollapsed.visibility = View.GONE
        when (option) {
            1 -> {
                currentlySelectedRepetition = Constant.HABIT_REPEAT_AS_WEEKDAY
                repetitionBinding.repChecked1.setImageDrawable(selectDra)
            }
            2 -> {
                currentlySelectedRepetition = Constant.HABIT_REPEAT_IN_WEEK
                repetitionBinding.repChecked2.setImageDrawable(selectDra)
                repetitionBinding.xDaysPerWeekCollapsed.visibility = View.VISIBLE
            }
            3 -> {
                currentlySelectedRepetition = Constant.HABIT_REPEAT_IN_MONTH
                repetitionBinding.repChecked3.setImageDrawable(selectDra)
                repetitionBinding.xDaysPerMonthCollapsed.visibility = View.VISIBLE
            }
            4 -> {
                currentlySelectedRepetition = Constant.HABIT_REPEAT_IN_YEAR
                repetitionBinding.repChecked4.setImageDrawable(selectDra)
                repetitionBinding.xDaysPerYearCollapsed.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        var editHabit = false
        var editHabitId = -1
        var iconChanged = false
    }
}