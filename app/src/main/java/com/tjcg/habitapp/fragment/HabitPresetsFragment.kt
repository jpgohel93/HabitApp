package com.tjcg.habitapp.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.habitapp.LoginActivity
import com.tjcg.habitapp.MainActivity
import com.tjcg.habitapp.R
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.data.HabitDataSource
import com.tjcg.habitapp.data.HabitPreset
import com.tjcg.habitapp.databinding.FragmentHabitPresetsBinding
import com.tjcg.habitapp.databinding.RecyclerItemHabitPresetBinding
import com.tjcg.habitapp.remote.ApiService
import com.tjcg.habitapp.remote.PresetResponse
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


const val CATEGORY_REGULAR = 1
const val CATEGORY_NEGATIVE = 2
const val CATEGORY_ONE_TIME = 3

@AndroidEntryPoint
class HabitPresetsFragment : Fragment() {

    private lateinit var binding: FragmentHabitPresetsBinding
    @Inject lateinit var dataSource: HabitDataSource
    private lateinit var ctx : Context
    private var regularHabitPresets : List<HabitPreset>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (MainActivity.isNavShowing) {
            MainActivity.hideBottomNavigation()
        }
        MainActivity.currentPage = Constant.PAGE_IN
        ctx = findNavController().context
        binding = FragmentHabitPresetsBinding.inflate(inflater, container, false)
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
            CreateNewHabitFragment.editHabit = false
            CreateNewHabitFragment.editHabitId = -1
            findNavController().navigate(R.id.action_bottom_habitPresetsFragment_to_bottom_createNewHabitFragment)
        }
        binding.habitPresetRecycler.layoutManager = GridLayoutManager(ctx, 3)
        ApiService.apiService?.getHabitPresets("Bearer "+Constant.authorizationToken)?.enqueue(
            object : Callback<PresetResponse> {
                override fun onResponse(
                    call: Call<PresetResponse>,
                    response: Response<PresetResponse>
                ) {
                    binding.progressBar.visibility = View.GONE
                    Log.d("PresetResponse", "${response.body()?.status}")
                    if (response.isSuccessful && response.body()?.status == true) {
                        if(!response.body()?.data.isNullOrEmpty()) {
                            Log.d("PresetResponse", "Found ${response.body()?.data?.size}")
                            regularHabitPresets = response.body()?.data
                            binding.habitPresetRecycler.adapter = HabitPresetAdapter(regularHabitPresets ?: emptyList())
                        }
                    } else if (!response.body()?.message.isNullOrBlank() &&
                            (response.body()?.message?.lowercase()?.contains("token")==true)) {
                        MainActivity.loginAgain(ctx, dataSource)
                    } else {
                        Log.e("PresetResponse", "${response.body()?.message}")
                    }
                }

                override fun onFailure(call: Call<PresetResponse>, t: Throwable) {
                    Log.e("PresetResponse", "${t.message}")
                    binding.progressBar.visibility = View.GONE
                }
            }
        )
     //   regularHabitPresets = dataSource.getHabitPresets(Constant.PRESET_REGULAR)

        selectCategoryCard(CATEGORY_REGULAR)
        return binding.root
    }

    private fun selectCategoryCard(category: Int) {
        binding.regularHabitCard.isSelected = false
        binding.negativeHabitCard.isSelected = false
        binding.oneTimeHabitCard.isSelected = false
        when(category) {
            CATEGORY_REGULAR -> {
                binding.regularHabitCard.isSelected = true
                binding.presetCategorryText.text = "REGULAR"
                binding.categoryInfoText.text = getString(R.string.habit_regular_discr)
            }
            CATEGORY_NEGATIVE -> {
                binding.negativeHabitCard.isSelected = true
                binding.presetCategorryText.text = "NEGATIVE"
                binding.categoryInfoText.text = getString(R.string.habit_negative_discr)
            }
            CATEGORY_ONE_TIME -> {
                binding.oneTimeHabitCard.isSelected = true
                binding.presetCategorryText.text = "ONE TIME"
                binding.categoryInfoText.text = getString(R.string.habit_one_time_discr)
            }
        }
    }

    inner class HabitPresetAdapter(private val presets: List<HabitPreset>) :
        RecyclerView.Adapter<HabitPresetAdapter.PresetHolder>() {

            inner class PresetHolder(val binding: RecyclerItemHabitPresetBinding) :
                    RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresetHolder =
            PresetHolder(RecyclerItemHabitPresetBinding.inflate(layoutInflater, parent, false))

        override fun onBindViewHolder(holder: PresetHolder, position: Int) {
            val preset = presets[position]
            holder.binding.habitTitle.text = preset.title
            holder.binding.habitIcon.text = preset.iconAwesome
            if (preset.iconImage != null) {
                holder.binding.habitIcon.visibility = View.GONE
                holder.binding.habitImage.visibility = View.VISIBLE
                holder.binding.habitImage.setImageBitmap(preset.iconImage)
            }
            holder.binding.habitInGrid.setOnClickListener {
                HabitsInPresetFragment.currentPreset = preset
                findNavController().navigate(R.id.action_bottom_habitPresetsFragment_to_bottom_habitsInPresetFragment)
            }
        }

        override fun getItemCount(): Int = presets.size
    }
}