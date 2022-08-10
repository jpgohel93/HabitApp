package com.tjcg.habitapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.habitapp.MainActivity
import com.tjcg.habitapp.data.WeekCalendarRow
import com.tjcg.habitapp.databinding.RecycleItemWeekCalendarDayBinding
import com.tjcg.habitapp.viewmodel.HabitViewModel

class WeekCalendarAdapter(private val ctx: Context, private val dates : ArrayList<ArrayList<WeekCalendarRow>>,
    private val viewModel: HabitViewModel)
    : RecyclerView.Adapter<WeekCalendarAdapter.WeekHolder>() {

    private var sDate: Int = 0
    private var sMonth : Int = 0

    init {
        setObserver()
    }

    inner class WeekHolder(val binding: RecycleItemWeekCalendarDayBinding) :
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekHolder =
        WeekHolder(RecycleItemWeekCalendarDayBinding.inflate(LayoutInflater.from(ctx), parent, false))

    override fun onBindViewHolder(holder: WeekHolder, position: Int) {
        val dates1 = dates[position]
        holder.binding.textAt1.text = dates1[0].date.toString()
        holder.binding.textAt2.text = dates1[1].date.toString()
        holder.binding.textAt3.text = dates1[2].date.toString()
        holder.binding.textAt4.text = dates1[3].date.toString()
        holder.binding.textAt5.text = dates1[4].date.toString()
        holder.binding.textAt6.text = dates1[5].date.toString()
        holder.binding.textAt7.text = dates1[6].date.toString()
        var markAt = -1
        for (i in 0 until dates1.size) {
            if (dates1[i].date == sDate && dates1[i].month == sMonth) {
                markAt = i
            }
        }
        holder.binding.selectionMark1.visibility = View.GONE
        holder.binding.selectionMark2.visibility = View.GONE
        holder.binding.selectionMark3.visibility = View.GONE
        holder.binding.selectionMark4.visibility = View.GONE
        holder.binding.selectionMark5.visibility = View.GONE
        holder.binding.selectionMark6.visibility = View.GONE
        holder.binding.selectionMark7.visibility = View.GONE
        when(markAt) {
            0 -> holder.binding.selectionMark1.visibility = View.VISIBLE
            1 -> holder.binding.selectionMark2.visibility = View.VISIBLE
            2 -> holder.binding.selectionMark3.visibility = View.VISIBLE
            3 -> holder.binding.selectionMark4.visibility = View.VISIBLE
            4 -> holder.binding.selectionMark5.visibility = View.VISIBLE
            5 -> holder.binding.selectionMark6.visibility = View.VISIBLE
            6 -> holder.binding.selectionMark7.visibility = View.VISIBLE
        }
        holder.binding.textAt1.setOnClickListener {
            setSelectionMark(dates1[0].date, dates1[0].month)
        }
        holder.binding.textAt2.setOnClickListener {
            setSelectionMark(dates1[1].date, dates1[1].month)
        }
        holder.binding.textAt3.setOnClickListener {
            setSelectionMark(dates1[2].date, dates1[2].month)
        }
        holder.binding.textAt4.setOnClickListener {
            setSelectionMark(dates1[3].date, dates1[3].month)
        }
        holder.binding.textAt5.setOnClickListener {
            setSelectionMark(dates1[4].date, dates1[4].month)
        }
        holder.binding.textAt6.setOnClickListener {
            setSelectionMark(dates1[5].date, dates1[5].month)
        }
        holder.binding.textAt7.setOnClickListener {
            setSelectionMark(dates1[6].date, dates1[6].month)
        }
    }

    override fun getItemCount(): Int = dates.size

    private fun setObserver() {
        viewModel.selectedWeekCalendarDate.observe((ctx as MainActivity)) { dates1 ->
            sDate = dates1[0]
            sMonth = dates1[1]
            notifyDataSetChanged()
        }
    }

    fun setSelectionMark(date: Int, month: Int) {
     /*   sDate = date
        sMonth = month
        notifyDataSetChanged()  */
        viewModel.selectedWeekCalendarDate.value = arrayOf(date, month)
    }

}