package com.tjcg.habitapp.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tjcg.habitapp.MainActivity
import com.tjcg.habitapp.R
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.data.HabitDataSource
import com.tjcg.habitapp.data.NotificationData
import com.tjcg.habitapp.databinding.FragmentMyProfileNotificationBinding
import com.tjcg.habitapp.databinding.OtherNotificationSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


const val LAYOUT_GLOBAL = 0
const val LAYOUT_MORNING = 1
const val LAYOUT_AFTERNOON = 2
const val LAYOUT_EVENING = 3

@AndroidEntryPoint
class MyProfileNotificationFragment : Fragment() {

    @Inject lateinit var dataSource: HabitDataSource
    private lateinit var binding: FragmentMyProfileNotificationBinding
    private lateinit var ctx: Context
    private lateinit var notificationData: NotificationData
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ctx = findNavController().context
        MainActivity.currentPage = Constant.PAGE_IN
        binding = FragmentMyProfileNotificationBinding.inflate(inflater, container,false)
        MainActivity.hideBottomNavigation()
        notificationData = dataSource.getNotificationData() ?: NotificationData()
        binding.morningReminderLayout.notificationTitle.text = "Morning Notification"
        binding.afternoonReminderLayout.notificationTitle.text = "Afternoon Notification"
        binding.eveningReminderLayout.notificationTitle.text = "Evening Notification"
        setSwitchListener(binding.globalReminderLayout, LAYOUT_GLOBAL)
        setSwitchListener(binding.morningReminderLayout, LAYOUT_MORNING)
        setSwitchListener(binding.afternoonReminderLayout, LAYOUT_AFTERNOON)
        setSwitchListener(binding.eveningReminderLayout, LAYOUT_EVENING)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setSwitchListener(layout: OtherNotificationSettingsBinding, layoutType: Int) {
        val timeBtn = layout.timeButton
        val daysButton = layout.daysButton
        val setButtonLayout = layout.setButtonLayout
        val timePickerLayout = layout.timePickerLayout
        val hourPicker = layout.hourPicker
        val minPicker = layout.minutePicker
        val repLayout = layout.repeatationWeekdaysLayout
        hourPicker.maxValue = 23
        minPicker.maxValue  = 59
        var newHour = 0
        var newMinute = 0
        var settingTime = true
        timeBtn.setOnClickListener {
            if (settingTime) {
                timeBtn.text = "save"
                timeBtn.setTextColor(Color.BLACK)
                timeBtn.background = ResourcesCompat.getDrawable(ctx.resources, R.drawable.card_black_outline, ctx.resources.newTheme())
                timePickerLayout.visibility = View.VISIBLE
                hourPicker.setOnValueChangedListener { _, i, i2 ->
                    newHour = i2
                }
                minPicker.setOnValueChangedListener { _, _, i2 ->
                    newMinute = i2
                }
                settingTime = false
            } else {
                val newMinuteStr = if (newMinute < 10) {
                    "0$newMinute"
                } else {
                    "$newMinute"
                }
                timeBtn.text = "$newHour:$newMinuteStr"
                timeBtn.setTextColor(Color.WHITE)
                timePickerLayout.visibility = View.GONE
                timeBtn.background = ResourcesCompat.getDrawable(ctx.resources, R.drawable.card_black, ctx.resources.newTheme())
                when(layoutType) {
                    LAYOUT_GLOBAL -> {
                        notificationData.globalNotificationTime = "$newHour:$newMinuteStr"
                    }
                    LAYOUT_MORNING -> {
                        notificationData.morningNotificationTime = "$newHour:$newMinuteStr"
                    }
                    LAYOUT_AFTERNOON -> {
                        notificationData.afternoonNotificationTime = "$newHour:$newMinuteStr"
                    }
                    LAYOUT_EVENING -> {
                        notificationData.eveningNotificationTime = "$newHour:$newMinuteStr"
                    }
                }
                dataSource.saveNotificationData(notificationData)
                settingTime = true
            }
        }
        var settingDays = true
        daysButton.setOnClickListener {
            if (settingDays) {
                repLayout.visibility = View.VISIBLE
                daysButton.text = "save"
                daysButton.setTextColor(Color.BLACK)
                daysButton.background = ResourcesCompat.getDrawable(ctx.resources, R.drawable.card_black_outline, ctx.resources.newTheme())
                settingDays = false
            } else {
                repLayout.visibility = View.GONE
                val daysHashMap = HashMap<Int, Boolean>()
                daysHashMap[Constant.SUNDAY] = layout.sun.isChecked
                daysHashMap[Constant.MONDAY] = layout.mon.isChecked
                daysHashMap[Constant.TUESDAY] = layout.tue.isChecked
                daysHashMap[Constant.WEDNESDAY] = layout.wed.isChecked
                daysHashMap[Constant.THURSDAY] = layout.thu.isChecked
                daysHashMap[Constant.FRIDAY] = layout.fri.isChecked
                daysHashMap[Constant.SATURDAY] = layout.sat.isChecked
                var isEveryday = true
                val daysString = ArrayList<String>()
                for (mapItem in daysHashMap.toSortedMap(compareBy { it })) {
                    if(!mapItem.value) { isEveryday = false} else {
                        daysString.add(Constant.provideDaysInTwoLetters(mapItem.key))
                    }
                }
                daysButton.text = if (isEveryday) {
                    "Everyday"
                } else {
                    daysString.joinToString()
                }
                when(layoutType) {
                    LAYOUT_GLOBAL -> {
                        notificationData.globalNotificationDaysActive = daysHashMap
                    }
                    LAYOUT_MORNING -> {
                        notificationData.morningNotificationDaysActive = daysHashMap
                    }
                    LAYOUT_AFTERNOON -> {
                        notificationData.afternoonNotificationDaysActive = daysHashMap
                    }
                    LAYOUT_EVENING -> {
                        notificationData.eveningNotificationDaysActive = daysHashMap
                    }
                }
                dataSource.saveNotificationData(notificationData)
                daysButton.setTextColor(Color.WHITE)
                daysButton.background = ResourcesCompat.getDrawable(ctx.resources, R.drawable.card_black, ctx.resources.newTheme())
                settingDays = true
            }
        }
        layout.reminderSwitch.setOnCheckedChangeListener { _, b ->
            if (b) {
                setButtonLayout.visibility = View.VISIBLE
                when(layoutType) {
                    LAYOUT_GLOBAL -> {
                        notificationData.globalReminderActive = true
                    }
                    LAYOUT_MORNING -> {
                        notificationData.morningReminderActive = true
                    }
                    LAYOUT_AFTERNOON -> {
                        notificationData.afternoonReminderActive = true
                    }
                    LAYOUT_EVENING -> {
                        notificationData.eveningReminderActive = true
                    }
                }
                dataSource.saveNotificationData(notificationData)
            } else {
                setButtonLayout.visibility = View.GONE
                timePickerLayout.visibility = View.GONE
                repLayout.visibility = View.GONE
                when(layoutType) {
                    LAYOUT_GLOBAL -> {
                        notificationData.globalReminderActive = false
                    }
                    LAYOUT_MORNING -> {
                        notificationData.morningReminderActive = false
                    }
                    LAYOUT_AFTERNOON -> {
                        notificationData.afternoonReminderActive = false
                    }
                    LAYOUT_EVENING -> {
                        notificationData.eveningReminderActive = false
                    }
                }
                dataSource.saveNotificationData(notificationData)
            }
        }
        when(layoutType) {
            LAYOUT_GLOBAL -> {
                layout.reminderSwitch.isChecked = notificationData.globalReminderActive ?: false
                timeBtn.text = notificationData.globalNotificationTime
                hourPicker.value = notificationData.globalNotificationTime.split(":")[0].toInt()
                minPicker.value = notificationData.globalNotificationTime.split(":")[1].toInt()
            }
            LAYOUT_MORNING -> {
                layout.reminderSwitch.isChecked = notificationData.morningReminderActive ?: false
                timeBtn.text = notificationData.morningNotificationTime
                hourPicker.value = notificationData.morningNotificationTime.split(":")[0].toInt()
                minPicker.value = notificationData.morningNotificationTime.split(":")[1].toInt()
            }
            LAYOUT_AFTERNOON -> {
                layout.reminderSwitch.isChecked = notificationData.afternoonReminderActive ?: false
                timeBtn.text = notificationData.afternoonNotificationTime
                hourPicker.value = notificationData.afternoonNotificationTime.split(":")[0].toInt()
                minPicker.value = notificationData.afternoonNotificationTime.split(":")[1].toInt()
            }
            LAYOUT_EVENING -> {
                layout.reminderSwitch.isChecked = notificationData.eveningReminderActive ?: false
                timeBtn.text = notificationData.eveningNotificationTime
                hourPicker.value = notificationData.eveningNotificationTime.split(":")[0].toInt()
                minPicker.value = notificationData.eveningNotificationTime.split(":")[1].toInt()
            }
        }
        setUpDaysCheckboxes(layout, layoutType)
    }

    private fun setUpDaysCheckboxes(layout: OtherNotificationSettingsBinding, layoutType: Int) {
        val arrayDays = ArrayList<String>()
        val mapToGet = when (layoutType) {
            LAYOUT_GLOBAL -> {
                notificationData.globalNotificationDaysActive.toSortedMap(compareBy { it })
            }
            LAYOUT_MORNING -> {
                notificationData.morningNotificationDaysActive.toSortedMap(compareBy { it })
            }
            LAYOUT_AFTERNOON -> {
                notificationData.afternoonNotificationDaysActive.toSortedMap(compareBy { it })
            }
            LAYOUT_EVENING -> {
                notificationData.eveningNotificationDaysActive.toSortedMap(compareBy { it })
            }
            else -> null
        }
        if (!mapToGet.isNullOrEmpty()) {
            for (mapItem in mapToGet) {
                if (mapItem.key == Constant.SUNDAY && !mapItem.value) {
                    layout.sun.isChecked = false
                }
                if (mapItem.key == Constant.MONDAY && !mapItem.value) {
                    layout.mon.isChecked = false
                }
                if (mapItem.key == Constant.TUESDAY && !mapItem.value) {
                    layout.tue.isChecked = false
                }
                if (mapItem.key == Constant.WEDNESDAY && !mapItem.value) {
                    layout.wed.isChecked = false
                }
                if (mapItem.key == Constant.THURSDAY && !mapItem.value) {
                    layout.thu.isChecked = false
                }
                if (mapItem.key == Constant.FRIDAY && !mapItem.value) {
                    layout.fri.isChecked = false
                }
                if (mapItem.key == Constant.SATURDAY && !mapItem.value) {
                    layout.sat.isChecked = false
                }
                if (mapItem.value) {
                    arrayDays.add(Constant.provideDaysInTwoLetters(mapItem.key))
                }
            }
        }
        val daysButtonText = if (arrayDays.isEmpty()) {
            "Everyday"
        } else {
            arrayDays.joinToString()
        }
        layout.daysButton.text = daysButtonText
    }
}