package com.tjcg.habitapp.fragment

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.habitapp.MainActivity
import com.tjcg.habitapp.R
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.databinding.FragmentMyProfileMainBinding
import com.tjcg.habitapp.databinding.RecyclerItemMyProfileOptionBinding

const val POS_NOTIFICATION = 0
const val POS_G_SETTINGS = 1
const val POS_LANGUAGES = 2
const val POS_SHARE = 3
const val POS_RATE_US= 4
const val POS_FEEDBACK = 5

class MyProfileMainFragment : Fragment() {

    lateinit var binding : FragmentMyProfileMainBinding
    lateinit var ctx: Context
    private var isSignInCardExpand = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
   /*     if (!MainActivity.isNavShowing) {
            MainActivity.showBottomNavigation()
        }
        MainActivity.currentPage = Constant.PAGE_4  */
        ctx = findNavController().context
        binding = FragmentMyProfileMainBinding.inflate(
            inflater, container, false)
        generateProfileOptions(binding.myProfileOptionsRecycler)
        binding.backAndRestLayout.setOnClickListener {
            if (isSignInCardExpand) {
                binding.loginCollapsedLayout.visibility = View.GONE
                isSignInCardExpand = false
            } else {
                binding.loginCollapsedLayout.visibility = View.VISIBLE
                isSignInCardExpand = true
            }
        }
        binding.loginCollapsedLayout.setOnClickListener(null)
        binding.loginGoogleBtn.setOnClickListener {
            Toast.makeText(ctx, "Yet to be Implement", Toast.LENGTH_SHORT).show()
            // TODO("perform google login here")
        }
        binding.loginFacebookBtn.setOnClickListener {
            Toast.makeText(ctx, "Yet to be Implement", Toast.LENGTH_SHORT).show()
            // TODO("perform facebook login here")
        }
        binding.goPremiumBtn.setOnClickListener {
    //        findNavController().navigate(R.id.navigation_premium)
        }
        return binding.root
    }

    private fun generateProfileOptions(recycler: RecyclerView) {
        val opList = ArrayList<ProfileOptions>()
        opList.add(
            ProfileOptions(
                ResourcesCompat.getDrawable(ctx.resources, R.drawable.op_notification, ctx.theme)!!,
                ctx.resources.getString(R.string.option_notification)))
        opList.add(
            ProfileOptions(
                ResourcesCompat.getDrawable(ctx.resources, R.drawable.op_settings, ctx.theme)!!,
                ctx.resources.getString(R.string.options_general_settings)))
        opList.add(
            ProfileOptions(
                ResourcesCompat.getDrawable(ctx.resources, R.drawable.op_language, ctx.theme)!!,
                ctx.resources.getString(R.string.option_languages)))
        opList.add(
            ProfileOptions(
                ResourcesCompat.getDrawable(ctx.resources, R.drawable.share_icon, ctx.theme)!!,
                ctx.resources.getString(R.string.option_share)))
        opList.add(
            ProfileOptions(
                ResourcesCompat.getDrawable(ctx.resources, R.drawable.op_rate, ctx.theme)!!,
                ctx.resources.getString(R.string.option_rate_us)))
        opList.add(
            ProfileOptions(
                ResourcesCompat.getDrawable(ctx.resources, R.drawable.op_feedback, ctx.theme)!!,
                ctx.resources.getString(R.string.option_feedback)))
        recycler.layoutManager = LinearLayoutManager(ctx)
        recycler.adapter = OptionsAdapter(opList)
    }

    class ProfileOptions(val icon: Drawable, val text: String)

    inner class OptionsAdapter(private val options: ArrayList<ProfileOptions>) :
        RecyclerView.Adapter<OptionsAdapter.MyHolder>() {

        inner class MyHolder(val binding: RecyclerItemMyProfileOptionBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder =
            MyHolder(RecyclerItemMyProfileOptionBinding.inflate(
                LayoutInflater.from(ctx), parent, false))

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            holder.binding.opIcon.setImageDrawable(options[position].icon)
            holder.binding.opText.text = options[position].text
            holder.binding.myProfileOptionLayout.setOnClickListener {
                when(position) {
                    POS_NOTIFICATION -> {
                        findNavController().navigate(
                            R.id.action_navigation_myProfileMainFragment_to_navigation_myProfileNotificationFragment)
                    }
                    POS_G_SETTINGS -> {
                        findNavController().navigate(
                            R.id.action_navigation_myProfileMainFragment_to_navigation_myProfileGeneralSettingsFragment)
                    }
                    POS_RATE_US -> {
                        findNavController().navigate(
                            R.id.action_navigation_myProfileMainFragment_to_navigation_rateUsFragment)
                    }
                    POS_FEEDBACK -> {
                        findNavController().navigate(
                            R.id.action_navigation_myProfileMainFragment_to_navigation_feedbackFragment)
                    }
                    else -> {
                        Toast.makeText(ctx, "Yet to be implemented", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        override fun getItemCount(): Int = options.size

    }

    companion object {
        fun getInstance(ctx: Context) : MyProfileMainFragment {
            val fragment = MyProfileMainFragment()
            fragment.ctx = ctx
            return fragment
        }
    }
}