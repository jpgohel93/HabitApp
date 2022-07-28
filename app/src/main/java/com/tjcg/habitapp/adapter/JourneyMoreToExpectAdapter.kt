package com.tjcg.habitapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.habitapp.databinding.RecyclerItemJourneyMoreToExpectBinding

class JourneyMoreToExpectAdapter(private val ctx: Context,
                                 private val expectations: Array<String>) :
    RecyclerView.Adapter<JourneyMoreToExpectAdapter.MyHolder>() {

    inner class MyHolder(val binding: RecyclerItemJourneyMoreToExpectBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(RecyclerItemJourneyMoreToExpectBinding.inflate(
            LayoutInflater.from(ctx), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.binding.expectationText.text = expectations[position]
    }

    override fun getItemCount(): Int = expectations.size

}