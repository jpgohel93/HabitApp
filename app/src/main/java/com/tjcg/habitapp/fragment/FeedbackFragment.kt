package com.tjcg.habitapp.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tjcg.habitapp.MainActivity
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.databinding.FragmentFeedbackBinding

class FeedbackFragment : Fragment() {

    private lateinit var binding : FragmentFeedbackBinding
    private lateinit var ctx: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ctx= findNavController().context
        MainActivity.currentPage = Constant.PAGE_IN
        binding = FragmentFeedbackBinding.inflate(
            inflater, container, false)
        binding.sendBtn.setOnClickListener {
            binding.feedbackSendAnim.visibility = View.VISIBLE
            binding.feedbackMainLayout.visibility = View.GONE
            Handler(Looper.getMainLooper()).postDelayed( {
                findNavController().navigateUp()
            }, 2500)
        }
        return binding.root
    }

}