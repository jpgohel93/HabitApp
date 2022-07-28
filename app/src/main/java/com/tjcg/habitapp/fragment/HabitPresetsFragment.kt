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
import com.tjcg.habitapp.databinding.FragmentHabitPresetsBinding

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
        binding = FragmentHabitPresetsBinding.inflate(inflater, container, false)
        binding.newHabitButton.setOnClickListener {
            findNavController().navigate(R.id.action_habitPresetsFragment_to_navigation_new_habit)
        }
        return binding.root
    }
}