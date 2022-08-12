package com.tjcg.habitapp.data

class NotificationData {

    var globalReminderActive : Boolean? = false
    var globalNotificationTime = "09:00"
    // should be stored as hasMap of weekday -> isActive
    var globalNotificationDaysActive: HashMap<Int, Boolean> = HashMap()

    var morningReminderActive: Boolean? = false
    var morningNotificationTime = "08:00"
    var morningNotificationDaysActive: HashMap<Int, Boolean> = HashMap()

    var afternoonReminderActive : Boolean? = false
    var afternoonNotificationTime = "13:00"
    var afternoonNotificationDaysActive: HashMap<Int, Boolean> = HashMap()

    var eveningReminderActive : Boolean? = false
    var eveningNotificationTime = "19:00"
    var eveningNotificationDaysActive: HashMap<Int, Boolean> = HashMap()

}