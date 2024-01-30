package com.tjcg.habitapp.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tjcg.habitapp.MainActivity
import com.tjcg.habitapp.R
import com.tjcg.habitapp.adapter.JourneyKeyResultsAdapter
import com.tjcg.habitapp.adapter.JourneyMoreToExpectAdapter
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.data.repository.JourneyRepository
import com.tjcg.habitapp.databinding.FragmentJourneyDescriptionBinding
import com.tjcg.habitapp.viewmodel.JourneyViewModel

class JourneyDescriptionFragment : Fragment() {

    private lateinit var bindingMain : FragmentJourneyDescriptionBinding
    private lateinit var ctx: Context
    private lateinit var jvModel : JourneyViewModel

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
        bindingMain = FragmentJourneyDescriptionBinding.inflate(
            inflater, container, false
        )
        jvModel = ViewModelProvider(this)[JourneyViewModel::class.java]
        setViewModelObservers()
        bindingMain.startJourneyButton.setOnClickListener {
       //      findNavController().navigate(R.id.action_navigation_journey_desc_to_navigation_premium)
        }
        bindingMain.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        return bindingMain.root
    }

    private fun setViewModelObservers() {
        if (!JourneyRepository.isRepositorySet) {
            Log.e("Description", "repository not set")
            return
        }
        JourneyRepository.jvModel.showDescriptionOf.observe(viewLifecycleOwner, { id ->
            val journey = JourneyRepository.getJourneyFromId(id)
            bindingMain.journeyLogo.setImageDrawable(journey.logo)
            bindingMain.journeyText.text = journey.title
            bindingMain.journeyDescription.text = journey.description
            bindingMain.content.journeyKeyResultsRecycler.layoutManager = GridLayoutManager(ctx, 2)
            bindingMain.content.journeyKeyResultsRecycler.adapter = JourneyKeyResultsAdapter(ctx, journey.keyResults)
            bindingMain.content.journeyMoreToExpectRecycler.layoutManager = LinearLayoutManager(ctx)
            bindingMain.content.journeyMoreToExpectRecycler.adapter = JourneyMoreToExpectAdapter(ctx, journey.moreToExpect)
        })
    }

}