package com.tjcg.habitapp.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tjcg.habitapp.MainActivity
import com.tjcg.habitapp.R
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.data.HabitDataSource
import com.tjcg.habitapp.data.TimePeriodData
import com.tjcg.habitapp.databinding.FragmentMyProfileTimePeriodsBinding
import com.tjcg.habitapp.databinding.OtherTimePeriodSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


const val CARD_MORNING = 0
const val CARD_AFTERNOON = 1
const val CARD_EVENING = 2
const val CARD_END = 3

@AndroidEntryPoint
class MyProfileTimePeriodSettings : Fragment() {

    @Inject lateinit var dataSource: HabitDataSource
    private lateinit var ctx: Context
    private lateinit var binding: FragmentMyProfileTimePeriodsBinding
    private lateinit var periodData: TimePeriodData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ctx = findNavController().context
//        MainActivity.currentPage = Constant.PAGE_IN
        binding = FragmentMyProfileTimePeriodsBinding.inflate(inflater, container, false)
        periodData = dataSource.getTimePeriodData() ?: TimePeriodData()
        setCardValues(binding.morningPeriodCard, CARD_MORNING)
        setCardValues(binding.afternoonPeriodCard, CARD_AFTERNOON)
        setCardValues(binding.eveningPeriodCard, CARD_EVENING)
        setCardValues(binding.dayEndPeriodCard, CARD_END)
        return binding.root
    }

    private fun setCardValues(cBinding: OtherTimePeriodSettingsBinding, cardType: Int) {
        val morningHour = periodData.timePeriodMorning[0]
        val morningMinutes = periodData.timePeriodMorning[1]
        val afternoonHours = periodData.timePeriodAfternoon[0]
        val afternoonMinutes = periodData.timePeriodAfternoon[1]
        val eveningHours = periodData.timePeriodEvening[0]
        val eveningMinutes = periodData.timePeriodEvening[1]
        val endHours = periodData.timePeriodEnd[0]
        val endMinutes = periodData.timePeriodEnd[1]
        when (cardType) {
            CARD_MORNING -> {
                cBinding.periodTitle.text = "Morning"
                cBinding.timeText.text = Constant.convertTimeDigitToText(morningHour) + ":" +
                        Constant.convertTimeDigitToText(morningMinutes) + " - " +
                        Constant.convertTimeDigitToText(afternoonHours) + ":" +
                        Constant.convertTimeDigitToText(afternoonMinutes)
                cBinding.timeAdjustText.text =
                    "Start at ${Constant.convertTimeDigitToText(morningHour)}:${Constant.convertTimeDigitToText(morningMinutes)}"
            }
            CARD_AFTERNOON -> {
                cBinding.periodTitle.text = "Afternoon"
                cBinding.timeText.text = Constant.convertTimeDigitToText(afternoonHours) + ":" +
                        Constant.convertTimeDigitToText(afternoonMinutes) + " - " +
                        Constant.convertTimeDigitToText(eveningHours) + ":" +
                        Constant.convertTimeDigitToText(eveningMinutes)
                cBinding.timeAdjustText.text =
                    "Start at ${Constant.convertTimeDigitToText(afternoonHours)}:${Constant.convertTimeDigitToText(afternoonMinutes)}"
            }
            CARD_EVENING -> {
                cBinding.periodTitle.text = "Evening"
                cBinding.timeText.text = Constant.convertTimeDigitToText(eveningHours) + ":" +
                        Constant.convertTimeDigitToText(eveningMinutes) + " - " +
                        Constant.convertTimeDigitToText(endHours) + ":" +
                        Constant.convertTimeDigitToText(endMinutes)
                cBinding.timeAdjustText.text =
                    "Start at ${Constant.convertTimeDigitToText(eveningHours)}:${Constant.convertTimeDigitToText(eveningMinutes)}"
            }
            CARD_END -> {
                cBinding.periodTitle.text = "End Of The Day"
                cBinding.timeText.visibility = View.GONE
                cBinding.timeAdjustText.text =
                    "Start at ${Constant.convertTimeDigitToText(endHours)}:${Constant.convertTimeDigitToText(endMinutes)}"
            }
        }
        cBinding.timeAdjustLayout.setOnClickListener {
            adjustTimePeriod(cBinding, cardType)
        }
    }

    private fun adjustTimePeriod(cBinding: OtherTimePeriodSettingsBinding, cardType: Int) {
        cBinding.timePeriodCard.background = ResourcesCompat.getDrawable(
            ctx.resources, R.drawable.card_black, ctx.theme)
        cBinding.periodTitle.setTextColor(Color.WHITE)
        cBinding.timeText.setTextColor(Color.WHITE)
        cBinding.timeAdjustLayout.visibility = View.GONE
        cBinding.saveButton.visibility = View.VISIBLE
        cBinding.timePicker.visibility = View.VISIBLE
   //     cBinding.timePickerLayout.visibility = View.VISIBLE
        when (cardType) {
            CARD_MORNING -> {
                var newStartHour = periodData.timePeriodMorning[0]
                var newStartMinute = periodData.timePeriodMorning[1]
                cBinding.timePicker.hour = periodData.timePeriodMorning[0]
                cBinding.timePicker.minute = periodData.timePeriodMorning[1]
                cBinding.timePicker.setIs24HourView(true)
                cBinding.timePicker.setOnTimeChangedListener { _, i, i2 ->
                    if (i > periodData.timePeriodAfternoon[0]) {
                        cBinding.timeWarning.visibility = View.VISIBLE
                        cBinding.saveButton.visibility = View.GONE
                        cBinding.timeWarning.text = "Should be lower than afternoon time"
                    } else if (i == periodData.timePeriodAfternoon[0] && i2 > periodData.timePeriodAfternoon[1]) {
                        cBinding.timeWarning.visibility = View.VISIBLE
                        cBinding.saveButton.visibility = View.GONE
                        cBinding.timeWarning.text = "Should be lower than afternoon time"
                    } else {
                        cBinding.timeWarning.visibility = View.GONE
                        cBinding.saveButton.visibility = View.VISIBLE
                        newStartHour = i
                        newStartMinute = i2
                    }
                }
                cBinding.saveButton.setOnClickListener {
                    periodData.timePeriodMorning = arrayOf(newStartHour, newStartMinute)
                    cBinding.timePeriodCard.background = ResourcesCompat.getDrawable(
                        ctx.resources, R.drawable.card_default, ctx.theme)
                    cBinding.periodTitle.setTextColor(Color.BLACK)
                    cBinding.timeText.setTextColor(Color.BLACK)
                    cBinding.timeAdjustLayout.visibility = View.VISIBLE
                    cBinding.saveButton.visibility = View.GONE
                    cBinding.timePicker.visibility = View.GONE
                    setCardValues(cBinding, CARD_MORNING)
                    dataSource.saveTimePeriodData(periodData)
                }
            }
            CARD_AFTERNOON -> {
                var newStartHour = periodData.timePeriodAfternoon[0]
                var newStartMinute = periodData.timePeriodAfternoon[1]
                cBinding.timePicker.hour = periodData.timePeriodAfternoon[0]
                cBinding.timePicker.minute = periodData.timePeriodAfternoon[1]
                cBinding.timePicker.setIs24HourView(true)
                cBinding.timePicker.setOnTimeChangedListener { _, i, i2 ->
                    if (i < periodData.timePeriodMorning[0]) {
                        cBinding.timeWarning.visibility = View.VISIBLE
                        cBinding.saveButton.visibility = View.GONE
                        cBinding.timeWarning.text = "Should be higher than morning time"
                    } else if (i == periodData.timePeriodMorning[0] && i2 < periodData.timePeriodMorning[1]) {
                        cBinding.timeWarning.visibility = View.VISIBLE
                        cBinding.saveButton.visibility = View.GONE
                        cBinding.timeWarning.text = "Should be higher than morning time"
                    } else if (i > periodData.timePeriodEvening[0]) {
                        cBinding.timeWarning.visibility = View.VISIBLE
                        cBinding.saveButton.visibility = View.GONE
                        cBinding.timeWarning.text = "Should be lower than evening time"
                    } else if (i == periodData.timePeriodEvening[0] && i > periodData.timePeriodEvening[1]) {
                        cBinding.timeWarning.visibility = View.VISIBLE
                        cBinding.saveButton.visibility = View.GONE
                        cBinding.timeWarning.text = "Should be lower than evening time"
                    } else {
                        cBinding.timeWarning.visibility = View.GONE
                        cBinding.saveButton.visibility = View.VISIBLE
                        newStartHour = i
                        newStartMinute = i2
                    }
                }
                cBinding.saveButton.setOnClickListener {
                    periodData.timePeriodAfternoon = arrayOf(newStartHour, newStartMinute)
                    cBinding.timePeriodCard.background = ResourcesCompat.getDrawable(
                        ctx.resources, R.drawable.card_default, ctx.theme)
                    cBinding.periodTitle.setTextColor(Color.BLACK)
                    cBinding.timeText.setTextColor(Color.BLACK)
                    cBinding.timeAdjustLayout.visibility = View.VISIBLE
                    cBinding.saveButton.visibility = View.GONE
                    cBinding.timePicker.visibility = View.GONE
                    setCardValues(binding.morningPeriodCard, CARD_MORNING)
                    setCardValues(cBinding, CARD_AFTERNOON)
                    dataSource.saveTimePeriodData(periodData)
                }
            }
            CARD_EVENING -> {
                var newStartHour = periodData.timePeriodEvening[0]
                var newStartMinute = periodData.timePeriodEvening[1]
                cBinding.timePicker.hour = newStartHour
                cBinding.timePicker.minute = newStartMinute
                cBinding.timePicker.setIs24HourView(true)
                cBinding.timePicker.setOnTimeChangedListener { _, i, i2 ->
                    if (i < periodData.timePeriodAfternoon[0]) {
                        cBinding.timeWarning.visibility = View.VISIBLE
                        cBinding.saveButton.visibility = View.GONE
                        cBinding.timeWarning.text = "Should be higher than Afternoon time"
                    } else if (i == periodData.timePeriodAfternoon[0] && i2 < periodData.timePeriodAfternoon[1]) {
                        cBinding.timeWarning.visibility = View.VISIBLE
                        cBinding.saveButton.visibility = View.GONE
                        cBinding.timeWarning.text = "Should be higher than Afternoon time"
                    } else if (i > periodData.timePeriodEnd[0]) {
                        cBinding.timeWarning.visibility = View.VISIBLE
                        cBinding.saveButton.visibility = View.GONE
                        cBinding.timeWarning.text = "Should be lower than day end time"
                    } else if (i == periodData.timePeriodEnd[0] && i > periodData.timePeriodEnd[1]) {
                        cBinding.timeWarning.visibility = View.VISIBLE
                        cBinding.saveButton.visibility = View.GONE
                        cBinding.timeWarning.text = "Should be lower than day end time"
                    } else {
                        cBinding.timeWarning.visibility = View.GONE
                        cBinding.saveButton.visibility = View.VISIBLE
                        newStartHour = i
                        newStartMinute = i2
                    }
                }
                cBinding.saveButton.setOnClickListener {
                    periodData.timePeriodEvening = arrayOf(newStartHour, newStartMinute)
                    cBinding.timePeriodCard.background = ResourcesCompat.getDrawable(
                        ctx.resources, R.drawable.card_default, ctx.theme)
                    cBinding.periodTitle.setTextColor(Color.BLACK)
                    cBinding.timeText.setTextColor(Color.BLACK)
                    cBinding.timeAdjustLayout.visibility = View.VISIBLE
                    cBinding.saveButton.visibility = View.GONE
                    cBinding.timePicker.visibility = View.GONE
                    setCardValues(binding.afternoonPeriodCard, CARD_AFTERNOON)
                    setCardValues(cBinding, CARD_EVENING)
                    dataSource.saveTimePeriodData(periodData)
                }
            }
            CARD_END -> {
                var newStartHour = periodData.timePeriodEnd[0]
                var newStartMinute = periodData.timePeriodEnd[1]
                cBinding.timePicker.hour = newStartHour
                cBinding.timePicker.minute = newStartMinute
                cBinding.timePicker.setIs24HourView(true)
                cBinding.timePicker.setOnTimeChangedListener { _, i, i2 ->
                    if (i < periodData.timePeriodEvening[0]) {
                        cBinding.timeWarning.visibility = View.VISIBLE
                        cBinding.saveButton.visibility = View.GONE
                        cBinding.timeWarning.text = "Should be higher than evening time"
                    } else if (i == periodData.timePeriodEvening[0] && i2 < periodData.timePeriodEvening[1]) {
                        cBinding.timeWarning.visibility = View.VISIBLE
                        cBinding.saveButton.visibility = View.GONE
                        cBinding.timeWarning.text = "Should be higher than evening time"
                    } else {
                        cBinding.timeWarning.visibility = View.GONE
                        cBinding.saveButton.visibility = View.VISIBLE
                        newStartHour = i
                        newStartMinute = i2
                    }
                }
                cBinding.saveButton.setOnClickListener {
                    periodData.timePeriodEnd = arrayOf(newStartHour, newStartMinute)
                    cBinding.timePeriodCard.background = ResourcesCompat.getDrawable(
                        ctx.resources, R.drawable.card_default, ctx.theme)
                    cBinding.periodTitle.setTextColor(Color.BLACK)
                    cBinding.timeText.setTextColor(Color.BLACK)
                    cBinding.timeAdjustLayout.visibility = View.VISIBLE
                    cBinding.saveButton.visibility = View.GONE
                    cBinding.timePicker.visibility = View.GONE
                    setCardValues(binding.eveningPeriodCard, CARD_EVENING)
                    setCardValues(cBinding, CARD_END)
                    dataSource.saveTimePeriodData(periodData)
                }
            }
        }
    }
}