package com.tjcg.habitapp.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.habitapp.MainActivity
import com.tjcg.habitapp.R
import com.tjcg.habitapp.adapter.AllJourneyAdapter
import com.tjcg.habitapp.adapter.MyJourneyAdapter
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.data.data_class.Journey
import com.tjcg.habitapp.data.data_class.JourneyKeyResult
import com.tjcg.habitapp.data.repository.JourneyRepository
import com.tjcg.habitapp.databinding.FragmentJourneyMainBinding
import com.tjcg.habitapp.viewmodel.JourneyViewModel

class JourneyMainFragment : Fragment() {

    private lateinit var binding : FragmentJourneyMainBinding
    private lateinit var ctx: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!MainActivity.isNavShowing) {
            MainActivity.showBottomNavigation()
        }
        ctx = findNavController().context
        MainActivity.currentPage = Constant.PAGE_2
        binding = FragmentJourneyMainBinding.inflate(inflater, container, false)
        val journeyViewModel = ViewModelProvider(this)[JourneyViewModel::class.java]
        JourneyRepository.setRepository(journeyViewModel)
        showDummyMyJourneys(binding.myJourneyRecycler)
        showDummyAllJourneys(binding.suggestedJourneyRecycler)
        return binding.root
    }

    private fun showDummyMyJourneys(recycler: RecyclerView) {
        recycler.layoutManager = LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL, false)
        val dummyJourneys =  ArrayList<Journey>()
        val keyResults = generateDummyKeyResults()
        val moreToExpect = generateDummyMoreToExpect()
        val description = "No Description"
        dummyJourneys.add(
            Journey(0,"Work everyday for health", description,
                ResourcesCompat.getDrawable(ctx.resources, R.drawable.working, ctx.theme)!!,
                keyResults, moreToExpect))
        dummyJourneys.add(
            Journey(1,"energy boosting morning routine",description,
                ResourcesCompat.getDrawable(ctx.resources, R.drawable.energy_boost, ctx.theme)!!,
                keyResults, moreToExpect))
        dummyJourneys.add(
            Journey(2,"happy life with furry friends",description,
                ResourcesCompat.getDrawable(ctx.resources, R.drawable.happy_life, ctx.theme)!!,
                keyResults, moreToExpect)
        )
        recycler.adapter = MyJourneyAdapter(ctx, dummyJourneys)
    }

    private fun showDummyAllJourneys(recycler: RecyclerView) {
        recycler.layoutManager = LinearLayoutManager(ctx)
        val dummyJourneys = ArrayList<Journey>()
        val keyResults = generateDummyKeyResults()
        val moreToExpect = generateDummyMoreToExpect()
        val description = "No Description"
        dummyJourneys.add(
            Journey(0,"Bedtime Ritual for sweet sleep", description,
                ResourcesCompat.getDrawable(ctx.resources,
                    R.drawable.sleeping, ctx.theme)!!, keyResults, moreToExpect))
        dummyJourneys.add(
            Journey(1,"Say Goodbye to sugar", description,
                ResourcesCompat.getDrawable(ctx.resources,
                    R.drawable.no_sugar, ctx.theme)!!, keyResults, moreToExpect))
        dummyJourneys.add(
            Journey(2,"Meditation For Peace Of Mind", description,
                ResourcesCompat.getDrawable(ctx.resources,
                    R.drawable.meditation, ctx.theme)!!, keyResults, moreToExpect))
        dummyJourneys.add(
            Journey(3,"Happy life with furry friends",description,
                ResourcesCompat.getDrawable(ctx.resources,
                    R.drawable.happy_life, ctx.theme)!!, keyResults, moreToExpect))
        dummyJourneys.add(
            Journey(4,"Increase Productivity at work",description,
                ResourcesCompat.getDrawable(ctx.resources,
                    R.drawable.working, ctx.theme)!!, keyResults, moreToExpect))
        if (!JourneyRepository.isRepositorySet) {
            Log.e("JourneyMain", "Repository not set")
            return
        }
        JourneyRepository.storeAllJourneys(dummyJourneys)
        recycler.adapter = AllJourneyAdapter(ctx, findNavController(), dummyJourneys)
    }

    private fun generateDummyKeyResults() : ArrayList<JourneyKeyResult> {
        val dummyList = ArrayList<JourneyKeyResult>()
        dummyList.add(JourneyKeyResult(ResourcesCompat.getDrawable(
            ctx.resources, R.drawable.k_result1, ctx.theme)!!,
            "Reduce Sedentary Time"))
        dummyList.add(JourneyKeyResult(ResourcesCompat.getDrawable(
            ctx.resources, R.drawable.k_result2, ctx.theme)!!,
            "Better Professional Performance"))
        dummyList.add(JourneyKeyResult(ResourcesCompat.getDrawable(
            ctx.resources, R.drawable.k_result3, ctx.theme)!!,
            "Prevent Occupational Disease"))
        dummyList.add(JourneyKeyResult(ResourcesCompat.getDrawable(
            ctx.resources, R.drawable.k_result4, ctx.theme)!!,
            "Less Chronic Stress"))
        return dummyList
    }

    private fun generateDummyMoreToExpect() : Array<String> {
        return arrayOf("Better Memory And Cognitive Function",
            "Efficient Use Of Fragmented Time",
            "Inspire Confidence And Lift The Mood",
            "Better Relationships With Co-Workers")
    }
}