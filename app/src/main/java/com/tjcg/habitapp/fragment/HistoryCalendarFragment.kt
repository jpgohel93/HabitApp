package com.tjcg.habitapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tjcg.habitapp.databinding.FragmentHistoryCalendarBinding

class HistoryCalendarFragment : Fragment() {

    lateinit var binding : FragmentHistoryCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryCalendarBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    companion object {

        fun getInstance() : HistoryCalendarFragment {
            return HistoryCalendarFragment()
        }
    }
}