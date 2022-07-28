package com.tjcg.habitapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tjcg.habitapp.databinding.FragmentHistoryAllHabitsBinding

class HistoryAllHabitsFragment : Fragment() {

    private lateinit var binding : FragmentHistoryAllHabitsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryAllHabitsBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    companion object {
        fun getInstance() : HistoryAllHabitsFragment {
            return HistoryAllHabitsFragment()
        }
    }

}