package com.tjcg.habitapp.data

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.tjcg.habitapp.MainActivity
import com.tjcg.habitapp.viewmodel.HabitViewModel
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitDataSource @Inject constructor(private val habitDao: HabitDao, private val
    calendarDao: HabitCalendarDao) : HabitDatabaseInterface,
    HabitCalendarInterface {

    private val mainScope = CoroutineScope(Dispatchers.Main)
    private lateinit var viewModel : HabitViewModel

    fun setupViewModel(ctx: Context) {
        viewModel = ViewModelProvider(ctx as MainActivity)[HabitViewModel::class.java]
    }

    fun provideViewModel() = viewModel

    private val exeService = Executors.newFixedThreadPool(4)
    private val mainHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun addHabit(habit: Habit) {
        exeService.execute {
            habitDao.insertNewHabits(habit)
            updateHabitList()
        }
    }

    override suspend fun getAllHabitsAsync(): Deferred<List<Habit>> =
        coroutineScope {
            async(Dispatchers.IO) {
                return@async habitDao.getAllHabits()
            }
        }


    override suspend fun getHabitByIdAsync(id: Int) : Deferred<Habit?> =
        coroutineScope {
            async(Dispatchers.IO) {
                return@async habitDao.getHabitById(id)
            }
        }

    private fun updateHabitList() {
        mainScope.launch {
            val habits = getAllHabitsAsync().await()
            viewModel.allHabitList.value = habits
        }
    }

    override fun addInCalendar(habitCalendar: HabitCalendar) {
        exeService.execute {
            calendarDao.insertInCalendar(habitCalendar)
        }
    }

    override fun getByCalendar(dateStr: String, callback: (HabitCalendar?) -> Unit) {
        exeService.execute {
            val calendarHabits = calendarDao.getHabitsByDate(dateStr)
            mainHandler.post {
                callback(calendarHabits)
            }
        }
    }

    override fun updateHabitsInCalendar(habitCalendar: HabitCalendar) {
        exeService.execute {
            calendarDao.updateHabitsInCalendar(habitCalendar)
        }
    }

    fun changeHabitStatus(date: String, id: Int, status : Boolean) {
        exeService.execute {
            val habitCalendar = calendarDao.getHabitsByDate(date)
            val listHabits = habitCalendar?.habitsInADay
            val habitToUpdate = listHabits?.find { it.habitId == id }
            habitToUpdate?.isFinished = status
            calendarDao.updateHabitsInCalendar(habitCalendar!!)
        }
    }

    fun changeHabitGoalCount(count : Int, dateStr: String, id: Int) {
        exeService.execute {
            val thatDayCalendar = calendarDao.getHabitsByDate(dateStr)
            val thatAllHabits = thatDayCalendar?.habitsInADay
            val thatHabit = thatAllHabits?.find { it.habitId == id }
            if (thatHabit != null) {
                val thatHabitData = habitDao.getHabitById(id)
                if (count == -1) {
                    thatHabit.goalTimesCount = (thatHabitData?.repetitionGoalCount ?: 99)
                    thatHabit.isFinished = true
                } else if (count == 0){
                    thatHabit.goalTimesCount = 0
                    thatHabit.isFinished = false
                }else {
                    thatHabit.goalTimesCount = count
                    if (thatHabit.goalTimesCount >= (thatHabitData?.repetitionGoalCount ?: 99)) {
                        thatHabit.goalTimesCount = (thatHabitData?.repetitionGoalCount ?: 99)
                        thatHabit.isFinished = true
                    }
                }
          /*      if (incr) {
                    if (thatHabit.goalTimesCount < (thatHabitData?.repetitionGoalCount ?: 99)) {
                        thatHabit.goalTimesCount += 1
                    }
                } else {
                    if (thatHabit.goalTimesCount > 0) {
                        thatHabit.goalTimesCount -= 1
                    }
                }
                if (thatHabit.goalTimesCount >= (thatHabitData?.repetitionGoalCount ?: 99)) {
                    thatHabit.isFinished = true
                }  */
                thatDayCalendar.habitsInADay = thatAllHabits
                calendarDao.updateHabitsInCalendar(thatDayCalendar)
            } else {
                Log.e("GoalCountFailure", "Habit with id $id not found")
            }
        }
    }
}