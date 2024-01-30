package com.tjcg.habitapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.habitapp.data.SoundEffect
import com.tjcg.habitapp.databinding.RecyclerItemSimpleBinding

class SoundEffectAdapter(private val ctx:Context, private val soundEffects: ArrayList<SoundEffect>)
    : RecyclerView.Adapter<SoundEffectAdapter.SoundHolder>() {

    companion object {
        var selectedSoundName: String = ""
    }

    inner class SoundHolder(val binding: RecyclerItemSimpleBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundHolder =
        SoundHolder(RecyclerItemSimpleBinding.inflate(LayoutInflater.from(ctx), parent, false))

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: SoundHolder, position: Int) {
        val sound = soundEffects[position]
        holder.binding.simpleTitle.text = sound.name
        holder.binding.mainLayout.isSelected = selectedSoundName == sound.name
        holder.binding.mainLayout.setOnClickListener {
            val mediaPlayer = MediaPlayer.create(ctx, sound.resourceId)
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener {
                mediaPlayer.release()
            }
            selectedSoundName = sound.name
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = soundEffects.size


}