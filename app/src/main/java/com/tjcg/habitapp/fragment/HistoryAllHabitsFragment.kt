package com.tjcg.habitapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.data.Habit
import com.tjcg.habitapp.data.HabitDataSource
import com.tjcg.habitapp.databinding.FragmentHistoryAllHabitsBinding
import com.tjcg.habitapp.databinding.RecyclerItemHabitInGridBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HistoryAllHabitsFragment : Fragment() {

    @Inject lateinit var dataSource: HabitDataSource
    private lateinit var binding : FragmentHistoryAllHabitsBinding
    private lateinit var ctx: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ctx = findNavController().context
        binding = FragmentHistoryAllHabitsBinding.inflate(
            inflater, container, false
        )
        CoroutineScope(Dispatchers.Main).launch {
            val allHabits = dataSource.getAllHabitsAsync().await()
            val anyTimeHabit = ArrayList<Habit>()
            val morningHabit = ArrayList<Habit>()
            val afternoonHabit = ArrayList<Habit>()
            val eveningHabit = ArrayList<Habit>()
            for (habit in allHabits) {
                when(habit.doItAtTime) {
                    Constant.HABIT_DO_IT_ANYTIME -> anyTimeHabit.add(habit)
                    Constant.HABIT_DO_IT_MORNING -> morningHabit.add(habit)
                    Constant.HABIT_DO_IT_AFTERNOON -> afternoonHabit.add(habit)
                    Constant.HABIT_DO_IT_EVENING -> eveningHabit.add(habit)
                }
            }
            if (anyTimeHabit.isEmpty()) {
                binding.anyTimeEmptyCard.visibility = View.VISIBLE
            }
            if (morningHabit.isEmpty()) {
                binding.morningEmptyCard.visibility = View.VISIBLE
            }
            if (afternoonHabit.isEmpty()) {
                binding.afternoonEmptyCard.visibility = View.VISIBLE
            }
            if (eveningHabit.isEmpty()) {
                binding.eveningEmptyCard.visibility = View.VISIBLE
            }
            binding.anytimeHabitRecycler.layoutManager = GridLayoutManager(ctx, 2)
            binding.morningHabitRecycler.layoutManager = GridLayoutManager(ctx, 2)
            binding.afternoonHabitRecycler.layoutManager = GridLayoutManager(ctx, 2)
            binding.eveningHabitRecycler.layoutManager = GridLayoutManager(ctx, 2)
            binding.anytimeHabitRecycler.adapter = HabitInGridAdapter(anyTimeHabit)
            binding.morningHabitRecycler.adapter = HabitInGridAdapter(morningHabit)
            binding.afternoonHabitRecycler.adapter = HabitInGridAdapter(afternoonHabit)
            binding.eveningHabitRecycler.adapter = HabitInGridAdapter(eveningHabit)
        }
        return binding.root
    }

    companion object {
        fun getInstance() : HistoryAllHabitsFragment {
            return HistoryAllHabitsFragment()
        }
    }

    inner class HabitInGridAdapter(val habits:ArrayList<Habit>) :
            RecyclerView.Adapter<HabitInGridAdapter.HabitHolder>() {

        inner class HabitHolder(val binding: RecyclerItemHabitInGridBinding) :
                RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitHolder =
            HabitHolder(RecyclerItemHabitInGridBinding.inflate(layoutInflater, parent, false))

        override fun onBindViewHolder(holder: HabitHolder, position: Int) {
            val habit = habits[position]
            holder.binding.habitTitle.text = habit.title
        }

        override fun getItemCount(): Int = habits.size
    }

}