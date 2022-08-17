package com.tjcg.habitapp.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.tjcg.habitapp.MainActivity
import com.tjcg.habitapp.adapter.SimpleItemAdapter
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.data.Constant.convertSecondsToText
import com.tjcg.habitapp.data.HabitDataSource
import com.tjcg.habitapp.databinding.FragmentTimerBinding
import com.tjcg.habitapp.databinding.FragmentTimerTabsBinding
import com.tjcg.habitapp.databinding.SubFragmentTimerMusicSettingsBinding
import com.tjcg.habitapp.databinding.SubFragmentTimerNotificationSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TimerFragment : Fragment() {

    @Inject lateinit var dataSource: HabitDataSource

    lateinit var ctx: Context

    companion object {
        var habitName = ""
        var habitId = 0
        var selectedDate = ""
        var habitGoalDuration = 0
        var habitDurationFinished = 0
        lateinit var mainViewPager : ViewPager2
    }

    private lateinit var mainBinding : FragmentTimerTabsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ctx = findNavController().context
        MainActivity.currentPage = Constant.PAGE_IN
        mainBinding = FragmentTimerTabsBinding.inflate(inflater, container, false)
        mainViewPager = mainBinding.timerViewPager
        mainViewPager.adapter = TimerAdapter(this)
        mainViewPager.currentItem = 1
        mainViewPager.isUserInputEnabled = false
        return mainBinding.root
    }

    inner class TimerAdapter(fm: Fragment) : FragmentStateAdapter(fm) {

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> TimerNotificationSettingsFragment.getInstance(ctx)
                1 -> TimerMainFragment.getInstance(ctx, dataSource)
                2 -> TimerMusicSettingsFragment.getInstance(ctx)
                else -> TimerMainFragment.getInstance(ctx, dataSource)
            }
        }

    }

    class TimerMainFragment : Fragment() {

        lateinit var dataSource: HabitDataSource
        lateinit var binding: FragmentTimerBinding
        lateinit var timerHandler: Handler
        lateinit var timerRunnable: Runnable
        private var isRunning = false
        private var isFinished = false
        private lateinit var ctx: Context

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            ctx = findNavController().context
            binding = FragmentTimerBinding.inflate(inflater, container, false)
            MainActivity.hideBottomNavigation()
            binding.habitTitle.text = habitName
            binding.timeText.text = "Total ${convertSecondsToText(habitGoalDuration, true)}"
            binding.countDownTime.text = convertSecondsToText(habitGoalDuration - habitDurationFinished)
            val progress = (habitGoalDuration - habitDurationFinished) *100 / habitGoalDuration
            binding.progressBar.progress = progress
            if (habitDurationFinished == 0) {
                binding.pauseButton.text = "Start"
            } else {
                binding.pauseButton.text = "Resume"
            }
            setTimerHandlers()
            binding.pauseButton.setOnClickListener {
                if (isFinished) {
                    stopTimer()
                    findNavController().navigateUp()
                    return@setOnClickListener
                }
                if (isRunning) {
                    stopTimer()
                    binding.pauseButton.text = "Resume"
                } else {
                    startTimer()
                    binding.pauseButton.text = "Pause"
                }
                isRunning = !isRunning
            }
            binding.reloadButton.setOnClickListener {
                reloadTimer()
            }
            binding.notificationTab.setOnClickListener {
                mainViewPager.setCurrentItem(0, true)
            }
            binding.musicTab.setOnClickListener {
                mainViewPager.setCurrentItem(2, true)
            }

            val backCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isRunning) {
                        var aDialog : AlertDialog? = null
                        val build = AlertDialog.Builder(ctx).apply {
                            setMessage("Stop the timer and Go back")
                            setPositiveButton("YES") {_, _ ->
                                stopTimer()
                                findNavController().navigateUp()
                            }
                            setNegativeButton("NO") { _,_ ->
                                aDialog?.dismiss()
                            }
                        }
                        aDialog = build.create()
                        aDialog?.show()
                    } else {
                        findNavController().navigateUp()
                    }
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)
            return binding.root
        }

        private fun setTimerHandlers() {
            timerHandler = Handler(Looper.getMainLooper())
            timerRunnable = object : Runnable {
                override fun run() {
                    habitDurationFinished += 1
                    binding.countDownTime.text = convertSecondsToText(habitGoalDuration - habitDurationFinished)
                    val progress = (habitGoalDuration - habitDurationFinished) *100 / habitGoalDuration
                    binding.progressBar.progress = progress
                    if (habitDurationFinished == habitGoalDuration) {
                        dataSource.changeHabitDurationCount(-1, selectedDate, habitId)
                        showFinishUI()
                    } else {
                        timerHandler.postDelayed(this, 1000)
                    }
                }
            }
        }

        private fun startTimer() {
            timerHandler.postDelayed(timerRunnable, 1000)
        }

        private fun stopTimer() {
            timerHandler.removeCallbacks(timerRunnable)
            dataSource.changeHabitDurationCount(habitDurationFinished, selectedDate, habitId)
        }

        private fun reloadTimer() {
            timerHandler.removeCallbacks(timerRunnable)
            habitDurationFinished = 0
            binding.countDownTime.text = convertSecondsToText(habitGoalDuration - habitDurationFinished)
            val progress = (habitGoalDuration - habitDurationFinished) *100 / habitGoalDuration
            binding.progressBar.progress = progress
            dataSource.changeHabitDurationCount(habitDurationFinished, selectedDate, habitId)
        }

        private fun showFinishUI() {
            isFinished = true
            binding.countDownTime.text = "Complete"
            binding.pauseButton.text = "Finish"
        }

        companion object {
            fun getInstance(ctx: Context, dataSource: HabitDataSource) : TimerMainFragment {
                val fragment = TimerMainFragment().apply {
                    this.ctx = ctx
                    this.dataSource = dataSource
                }
                return fragment
            }
        }
    }

    class TimerNotificationSettingsFragment : Fragment() {

        private lateinit var tBinding : SubFragmentTimerNotificationSettingsBinding
        private lateinit var ctx: Context

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            tBinding = SubFragmentTimerNotificationSettingsBinding.inflate(
                inflater, container, false
            )
            tBinding.habitTitle.text = habitName
            tBinding.habitTimeText.text = convertSecondsToText(habitGoalDuration)
            tBinding.closeBtn.setOnClickListener {
                mainViewPager.setCurrentItem(1, true)
            }
            val dummyContent = ArrayList<String>()
            dummyContent.add("A")
            dummyContent.add("B")
            dummyContent.add("C")
            dummyContent.add("D")
            dummyContent.add("E")
            dummyContent.add("F")
            dummyContent.add("G")
            dummyContent.add("H")
            dummyContent.add("I")
            dummyContent.add("J")
            dummyContent.add("K")
            dummyContent.add("L")
            dummyContent.add("M")
            dummyContent.add("N")
            dummyContent.add("O")
            dummyContent.add("P")
            dummyContent.add("Q")
            dummyContent.add("R")
            tBinding.recyclerView.layoutManager = LinearLayoutManager(ctx)
            tBinding.recyclerView.adapter = SimpleItemAdapter(ctx, dummyContent)
            return tBinding.root
        }

        companion object {
            fun getInstance(ctx: Context) : TimerNotificationSettingsFragment {
                return TimerNotificationSettingsFragment().apply {
                    this.ctx = ctx
                }
            }
        }
    }

    class TimerMusicSettingsFragment : Fragment() {

        private lateinit var ctx: Context
        private lateinit var tBinding : SubFragmentTimerMusicSettingsBinding

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            tBinding = SubFragmentTimerMusicSettingsBinding.inflate(inflater, container, false)
            tBinding.habitTitle.text = habitName
            tBinding.habitTimeText.text = convertSecondsToText(habitGoalDuration)
            tBinding.closeBtn.setOnClickListener {
                mainViewPager.setCurrentItem(1, true)
            }
            val dummyContent = ArrayList<String>()
            dummyContent.add("A")
            dummyContent.add("B")
            dummyContent.add("C")
            dummyContent.add("D")
            dummyContent.add("E")
            dummyContent.add("F")
            dummyContent.add("G")
            dummyContent.add("H")
            dummyContent.add("I")
            dummyContent.add("J")
            dummyContent.add("K")
            dummyContent.add("L")
            dummyContent.add("M")
            dummyContent.add("N")
            dummyContent.add("O")
            dummyContent.add("P")
            dummyContent.add("Q")
            dummyContent.add("R")
            tBinding.recyclerView.layoutManager = LinearLayoutManager(ctx)
            tBinding.recyclerView.adapter = SimpleItemAdapter(ctx, dummyContent)
            return tBinding.root
        }

        companion object {
            fun getInstance(ctx: Context) : TimerMusicSettingsFragment {
                val fragment = TimerMusicSettingsFragment().apply {
                    this.ctx = ctx
                }
                return fragment
            }
        }
    }
}