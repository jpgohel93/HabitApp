package com.tjcg.habitapp.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.habitapp.data.HabitDataSource
import com.tjcg.habitapp.databinding.FragmentIconSelectBinding
import com.tjcg.habitapp.databinding.RecyclerItemHabitIconBinding
import com.tjcg.habitapp.viewmodel.HabitViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class IconListFragment : Fragment() {

    @Inject lateinit var dataSource: HabitDataSource
    lateinit var binding : FragmentIconSelectBinding
    lateinit var ctx: Context
    private lateinit var selectedIcon : String
    private lateinit var habitViewModel: HabitViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ctx = findNavController().context
        habitViewModel = dataSource.provideViewModel()
        selectedIcon = habitViewModel.habitIcon.value ?: "calendar-check"
        binding = FragmentIconSelectBinding.inflate(inflater, container, false)
        binding.habitIconRecyclerView.layoutManager = GridLayoutManager(ctx, 3)
        binding.habitIconRecyclerView.recycledViewPool.setMaxRecycledViews(0,0)
        binding.habitIconRecyclerView.adapter = IconAdapter(generateIconList())
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    private fun generateIconList() : ArrayList<String> {
        val icons = ArrayList<String>()
        icons.add("calendar-check")
        icons.add("cat")
        icons.add("pen")
        icons.add("dog")
        icons.add("umbrella")
        icons.add("clock")
        icons.add("cow")
        icons.add("phone")
        icons.add("running")
        icons.add("mouse")
        icons.add("car")
        icons.add("bath")
        icons.add("bed")
        icons.add("envelope")
        icons.add("bank")
        icons.add("battery")
        icons.add("coffee")
        icons.add("gamepad")
        icons.add("hotel")
        icons.add("low-vision")
        icons.add("paintbrush")
        icons.add("sun")
        icons.add("trash")
        icons.add("truck")
        icons.add("bug")
        icons.add("clone")
        icons.add("commenting")
        icons.add("credit-card")
        icons.add("dollar")
        icons.add("eraser")
        icons.add("female")
        icons.add("male")
        icons.add("film")
        icons.add("flask")
        icons.add("heartbeat")
        icons.add("hourglass")
        icons.add("language")
        icons.add("shopping-basket")
        icons.add("soccer-ball")
        icons.add("university")
        icons.add("balance-scale")
        icons.add("bell-slash")
        icons.add("birthday-cake")
        icons.add("globe")
        icons.add("handshake")
        icons.add("music")
        icons.add("laptop")
        icons.add("magnet")
        icons.add("microphone")
        icons.add("plug")
        icons.add("tree")
        icons.add("tv")
        icons.add("wheelchair")
        icons.add("wrench")
        icons.add("beer")
        icons.add("cloud")
        icons.add("graduation-cap")
        icons.add("trophy")
        icons.add("ghost")
        return icons
    }

    inner class IconAdapter(private val iconList: ArrayList<String>) : RecyclerView.Adapter<IconAdapter.IconHolder>() {

        inner class IconHolder(val binding: RecyclerItemHabitIconBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconHolder =
            IconHolder(RecyclerItemHabitIconBinding.inflate(layoutInflater, parent, false))

        override fun onBindViewHolder(holder: IconHolder, position: Int) {
            val icon = iconList[position]
            holder.binding.iconText.text = icon
            if (icon == selectedIcon) {
                holder.binding.iconText.isSelected = true
                holder.binding.iconText.setTextColor(Color.WHITE)
            }
            holder.binding.iconText.setOnClickListener { 
                habitViewModel.habitIcon.value = icon
                findNavController().navigateUp()
            }
        }

        override fun getItemCount(): Int = iconList.size
    }
}