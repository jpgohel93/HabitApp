package com.tjcg.habitapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.habitapp.data.data_class.Journey
import com.tjcg.habitapp.databinding.RecyclerItemJourneyBalckCardBinding

class MyJourneyAdapter(private val ctx: Context,
                       private val journeys : ArrayList<Journey>) :
    RecyclerView.Adapter<MyJourneyAdapter.MyJourneyHolder>()    {

    inner class MyJourneyHolder(val binding: RecyclerItemJourneyBalckCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyJourneyHolder {
        return MyJourneyHolder(
            RecyclerItemJourneyBalckCardBinding.inflate(
                LayoutInflater.from(ctx), parent, false))
    }

    override fun onBindViewHolder(holder: MyJourneyHolder, position: Int) {
        holder.binding.myJourneyText.text = journeys[position].title
        holder.binding.MyJourneyImage.setImageDrawable(journeys[position].logo)
    }

    override fun getItemCount(): Int = journeys.size
}