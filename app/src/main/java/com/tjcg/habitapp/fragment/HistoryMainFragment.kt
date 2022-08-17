package com.tjcg.habitapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tjcg.habitapp.MainActivity
import com.tjcg.habitapp.R
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.databinding.FragmentHistoryMainBinding

class HistoryMainFragment : Fragment(), View.OnClickListener {

    private lateinit var binding : FragmentHistoryMainBinding
    private lateinit var ctx: Context
    private lateinit var tab1 : TextView
    private lateinit var tab2 : TextView
    private lateinit var tab3 : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ctx = findNavController().context
        MainActivity.currentPage = Constant.PAGE_3
        binding = FragmentHistoryMainBinding.inflate(inflater,
            container, false)
        tab1 = binding.tabs.tab1
        tab2 = binding.tabs.tab2
        tab3 = binding.tabs.tab3
        tab1.setOnClickListener(this)
        tab2.setOnClickListener(this)
        tab3.setOnClickListener(this)
        changeTab(0)
        binding.historyViewPager.adapter = ViewPagerAdapter(parentFragmentManager)
        binding.historyViewPager.isUserInputEnabled = false
        return binding.root
    }

    override fun onClick(tab: View?) {
        // change text color of tab text
        tab as TextView
        when(tab.id) {
            R.id.tab1 -> {
                changeTab(0)
            }
            R.id.tab2 -> {
                changeTab(1)
            }
            R.id.tab3 -> {
                changeTab(2)
            }
        }
    }

    private fun changeTab(selectedTab : Int) {
        tab1.isSelected = false
        tab2.isSelected = false
        tab3.isSelected = false
        tab1.setTextColor(ResourcesCompat.getColor( ctx.resources, R.color.black,ctx.theme))
        tab2.setTextColor(ResourcesCompat.getColor( ctx.resources,R.color.black,ctx.theme))
        tab3.setTextColor(ResourcesCompat.getColor( ctx.resources,R.color.black,ctx.theme))
        when (selectedTab) {
            0 -> {
                tab1.isSelected = true
                tab1.setTextColor(ResourcesCompat.getColor(ctx.resources, R.color.white, ctx.theme))
                binding.historyViewPager.currentItem = 0
            }
            1 -> {
                tab2.isSelected = true
                tab2.setTextColor(ResourcesCompat.getColor(ctx.resources, R.color.white, ctx.theme))
                binding.historyViewPager.currentItem = 1
            }
            2 -> {
                tab3.isSelected = true
                tab3.setTextColor(ResourcesCompat.getColor(ctx.resources, R.color.white, ctx.theme))
                binding.historyViewPager.currentItem = 2
            }
        }
    }

    inner class ViewPagerAdapter(manager: FragmentManager) : FragmentStateAdapter(manager, lifecycle) {

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HistoryCalendarFragment.getInstance()
                1 -> HistoryAllHabitsFragment.getInstance()
                2 -> HistoryAchievementsFragment.getInstance(ctx)
                else ->  HistoryCalendarFragment.getInstance()
            }
        }
    }
}