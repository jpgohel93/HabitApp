package com.tjcg.habitapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.habitapp.data.Habit
import com.tjcg.habitapp.data.HabitPreset
import com.tjcg.habitapp.databinding.FragmentPresetHabitsBinding
import com.tjcg.habitapp.databinding.RecyclerItemHabitInPresetBinding

class HabitsInPresetFragment : Fragment() {

    private lateinit var ctx: Context
    private lateinit var binding: FragmentPresetHabitsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ctx = findNavController().context
        binding = FragmentPresetHabitsBinding.inflate(inflater, container, false)
        binding.habitsRecyclerView.layoutManager = LinearLayoutManager(ctx)
        if (currentPreset != null) {
            binding.habitsRecyclerView.adapter = PresetHabitsAdapter(currentPreset?.habits ?: emptyList())
        }
        binding.closeBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    companion object {
        var currentPreset : HabitPreset? = null
    }

    inner class PresetHabitsAdapter(val habits: List<Habit>) : RecyclerView.Adapter<PresetHabitsAdapter.HabitHolder>() {

        inner class HabitHolder(val binding: RecyclerItemHabitInPresetBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitHolder =
            HabitHolder(RecyclerItemHabitInPresetBinding.inflate(layoutInflater, parent, false))

        override fun onBindViewHolder(holder: HabitHolder, position: Int) {
            val habit = habits[position]
            holder.binding.habitTitle.text = habit.title
            holder.binding.habitTitle.isSelected = true
            holder.binding.habitIconText.text = habit.icon
            holder.binding.habitInfo.text = habit.encouragementText
        }

        override fun getItemCount(): Int = habits.size


    }
}