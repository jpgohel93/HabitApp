package com.tjcg.habitapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.habitapp.R
import com.tjcg.habitapp.data.data_class.Journey
import com.tjcg.habitapp.data.repository.JourneyRepository
import com.tjcg.habitapp.databinding.RecyclerItemJourneyWhiteCardBinding

class AllJourneyAdapter(private val ctx: Context,
                        private val navController: NavController,
                        private val journeys: ArrayList<Journey>)
    : RecyclerView.Adapter<AllJourneyAdapter.AllJourneyHolder>() {

    inner class AllJourneyHolder(val binding: RecyclerItemJourneyWhiteCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllJourneyHolder {
        return AllJourneyHolder(
            RecyclerItemJourneyWhiteCardBinding.inflate(
                LayoutInflater.from(ctx), parent, false))
    }

    override fun onBindViewHolder(holder: AllJourneyHolder, position: Int) {
        holder.binding.journeyLogo.setImageDrawable(journeys[position].logo)
        holder.binding.journeyText.text = journeys[position].title
        holder.binding.journeyNext.setOnClickListener {
            JourneyRepository.setIdForDescription(journeys[position].id)
            navController.navigate(R.id.action_navigation_journeyMainFragment_to_navigation_journeyDescriptionFragment)
        }
    }

    override fun getItemCount(): Int = journeys.size
}