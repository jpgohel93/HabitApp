package com.tjcg.habitapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tjcg.habitapp.MainActivity
import com.tjcg.habitapp.R
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.databinding.FragmentHabitPresetsBinding


const val CATEGORY_REGULAR = 1
const val CATEGORY_NEGATIVE = 2
const val CATEGORY_ONE_TIME = 3

class HabitPresetsFragment : Fragment() {

    private lateinit var binding: FragmentHabitPresetsBinding
    private lateinit var ctx : Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (MainActivity.isNavShowing) {
            MainActivity.hideBottomNavigation()
        }
        ctx = findNavController().context
        MainActivity.currentPage = Constant.PAGE_IN
        binding = FragmentHabitPresetsBinding.inflate(inflater, container, false)
        selectCategoryCard(CATEGORY_REGULAR)
        binding.regularHabitCard.setOnClickListener {
            selectCategoryCard(CATEGORY_REGULAR)
        }
        binding.negativeHabitCard.setOnClickListener {
            selectCategoryCard(CATEGORY_NEGATIVE)
        }
        binding.oneTimeHabitCard.setOnClickListener {
            selectCategoryCard(CATEGORY_ONE_TIME)
        }
        binding.newHabitButton.setOnClickListener {
            findNavController().navigate(R.id.action_habitPresetsFragment_to_navigation_new_habit)
        }
        return binding.root
    }

    private fun selectCategoryCard(category: Int) {
        when(category) {
            CATEGORY_REGULAR -> {
                binding.presetCategorryText.text = "REGULAR"
                binding.categoryInfoText.text = getString(R.string.habit_regular_discr)
            }
            CATEGORY_NEGATIVE -> {
                binding.presetCategorryText.text = "NEGATIVE"
                binding.categoryInfoText.text = getString(R.string.habit_negative_discr)
            }
            CATEGORY_ONE_TIME -> {
                binding.presetCategorryText.text = "ONE TIME"
                binding.categoryInfoText.text = getString(R.string.habit_one_time_discr)
            }
        }
    }
}