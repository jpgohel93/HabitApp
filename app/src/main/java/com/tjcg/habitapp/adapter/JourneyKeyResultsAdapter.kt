package com.tjcg.habitapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.habitapp.data.data_class.JourneyKeyResult
import com.tjcg.habitapp.databinding.RecyclerItemJourneyKeyResultsBinding

class JourneyKeyResultsAdapter(private val ctx: Context,
                               private val keyResults: ArrayList<JourneyKeyResult>) :
    RecyclerView.Adapter<JourneyKeyResultsAdapter.MyHolder>() {

    inner class MyHolder(val binding: RecyclerItemJourneyKeyResultsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(RecyclerItemJourneyKeyResultsBinding.inflate(
            LayoutInflater.from(ctx), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.binding.resultImage.setImageDrawable(keyResults[position].logo)
        holder.binding.resultText.text = keyResults[position].text
    }

    override fun getItemCount(): Int = keyResults.size
}