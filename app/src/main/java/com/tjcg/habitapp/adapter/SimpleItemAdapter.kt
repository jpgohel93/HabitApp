package com.tjcg.habitapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.habitapp.databinding.RecyclerItemSimpleBinding

class SimpleItemAdapter(val ctx: Context, private val items : ArrayList<String>) : RecyclerView.Adapter<SimpleItemAdapter.SimpleHolder>() {


    inner class SimpleHolder(val binding: RecyclerItemSimpleBinding) :
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleHolder =
        SimpleHolder(RecyclerItemSimpleBinding.inflate(LayoutInflater.from(ctx), parent, false))

    override fun onBindViewHolder(holder: SimpleHolder, position: Int) {
        val item = items[position]
        holder.binding.simpleTitle.text = item
    }

    override fun getItemCount(): Int = items.size

}