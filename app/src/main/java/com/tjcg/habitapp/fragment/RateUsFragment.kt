package com.tjcg.habitapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tjcg.habitapp.R
import com.tjcg.habitapp.databinding.FragmentRateUsBinding

class RateUsFragment : Fragment() {

    private lateinit var binding : FragmentRateUsBinding
    private lateinit var ctx : Context
    private var currentRating = 1


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ctx = findNavController().context
        binding = FragmentRateUsBinding.inflate(
            inflater, container, false)
        binding.rate1.setOnClickListener {
            currentRating = 1
            updateUi()
        }
        binding.rate2.setOnClickListener {
            currentRating = 2
            updateUi()
        }
        binding.rate3.setOnClickListener {
            currentRating = 3
            updateUi()
        }
        binding.rate4.setOnClickListener {
            currentRating = 4
            updateUi()
        }
        binding.rate5.setOnClickListener {
            currentRating = 5
            updateUi()
        }
        return binding.root
    }

    private fun updateUi() {
        binding.rate2.setImageResource(R.drawable.star2)
        binding.rate3.setImageResource(R.drawable.star2)
        binding.rate4.setImageResource(R.drawable.star2)
        binding.rate5.setImageResource(R.drawable.star2)
        when (currentRating) {
            1 -> {
                binding.rateInfoText.text = getString(R.string.rate_1)
                binding.gifImageView.setImageResource(R.drawable.rate_1)
                binding.rate1.setImageResource(R.drawable.star_anim)
            }
            2 -> {
                binding.rateInfoText.text = getString(R.string.rate_1)
                binding.gifImageView.setImageResource(R.drawable.rate_2)
                binding.rate1.setImageResource(R.drawable.star_anim)
                binding.rate2.setImageResource(R.drawable.star_anim)
            }
            3 -> {
                binding.rateInfoText.text = getString(R.string.rate_3)
                binding.gifImageView.setImageResource(R.drawable.rate_3)
                binding.rate1.setImageResource(R.drawable.star_anim)
                binding.rate2.setImageResource(R.drawable.star_anim)
                binding.rate3.setImageResource(R.drawable.star_anim)
            }
            4 -> {
                binding.rateInfoText.text = getString(R.string.rate_4)
                binding.gifImageView.setImageResource(R.drawable.rate_4)
                binding.rate1.setImageResource(R.drawable.star_anim)
                binding.rate2.setImageResource(R.drawable.star_anim)
                binding.rate3.setImageResource(R.drawable.star_anim)
                binding.rate4.setImageResource(R.drawable.star_anim)
            }
            5 -> {
                binding.rateInfoText.text = getString(R.string.rate_5)
                binding.gifImageView.setImageResource(R.drawable.rate_5)
                binding.rate1.setImageResource(R.drawable.star_anim)
                binding.rate2.setImageResource(R.drawable.star_anim)
                binding.rate3.setImageResource(R.drawable.star_anim)
                binding.rate4.setImageResource(R.drawable.star_anim)
                binding.rate5.setImageResource(R.drawable.star_anim)
            }
        }
    }
}