package com.tjcg.habitapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.habitapp.MainActivity
import com.tjcg.habitapp.R
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.databinding.FragmentGoPremiumBinding
import com.tjcg.habitapp.databinding.RecyclerItemSubscriptionDetailBinding

class GoPremiumFragment : Fragment() {

    private lateinit var binding : FragmentGoPremiumBinding
    private lateinit var ctx: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ctx = findNavController().context
   //     MainActivity.currentPage = Constant.PAGE_IN
        binding = FragmentGoPremiumBinding.inflate(
            inflater, container, false
        )
        generateSubscriptionTerms(binding.content.subscriptionInfoRecycler)
        binding.closeBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    private fun generateSubscriptionTerms(recyclerView: RecyclerView) {
        val termsList = ArrayList<SubscriptionDetail>()
        termsList.add(SubscriptionDetail(1, ctx.resources.getString(R.string.terms1)))
        termsList.add(SubscriptionDetail(2, ctx.resources.getString(R.string.terms1)))
        termsList.add(SubscriptionDetail(3, ctx.resources.getString(R.string.terms1)))
        termsList.add(SubscriptionDetail(4, ctx.resources.getString(R.string.terms1)))
        recyclerView.layoutManager = LinearLayoutManager(ctx)
        recyclerView.adapter = SubscriptionAdapter(termsList)
    }

    inner class SubscriptionAdapter(private val terms: ArrayList<SubscriptionDetail>) : RecyclerView.Adapter<SubscriptionAdapter.SubHolder>() {

        inner class SubHolder(val binding: RecyclerItemSubscriptionDetailBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubHolder {
            return SubHolder(RecyclerItemSubscriptionDetailBinding.inflate(
                LayoutInflater.from(ctx), parent, false))
        }

        override fun onBindViewHolder(holder: SubHolder, position: Int) {
            holder.binding.detailNumberText.text = terms[position].num.toString()
            holder.binding.subscriptionText.text = terms[position].text
        }

        override fun getItemCount(): Int = terms.size


    }

    class SubscriptionDetail(val num:Int, val text:String)
}