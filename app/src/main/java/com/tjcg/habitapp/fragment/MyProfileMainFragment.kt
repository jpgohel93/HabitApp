package com.tjcg.habitapp.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.habitapp.LoginActivity
import com.tjcg.habitapp.MainActivity
import com.tjcg.habitapp.R
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.data.HabitDataSource
import com.tjcg.habitapp.databinding.DialogBackupAndRestoreBinding
import com.tjcg.habitapp.databinding.FragmentMyProfileMainBinding
import com.tjcg.habitapp.databinding.RecyclerItemMyProfileOptionBinding
import com.tjcg.habitapp.remote.ApiService
import com.tjcg.habitapp.remote.Apis
import com.tjcg.habitapp.remote.BackupResponse
import com.tjcg.habitapp.remote.RestoreResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

const val POS_NOTIFICATION = 0
const val POS_G_SETTINGS = 1
const val POS_LANGUAGES = 2
const val POS_SHARE = 3
const val POS_RATE_US= 4
const val POS_FEEDBACK = 5

@AndroidEntryPoint
class MyProfileMainFragment : Fragment() {

    @Inject lateinit var dataSource: HabitDataSource
    lateinit var binding : FragmentMyProfileMainBinding
    lateinit var ctx: Context
    private var isSignInCardExpand = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!MainActivity.isNavShowing) {
            MainActivity.showBottomNavigation()
        }
        MainActivity.currentPage = Constant.PAGE_4
        ctx = findNavController().context
        binding = FragmentMyProfileMainBinding.inflate(
            inflater, container, false)
        generateProfileOptions(binding.myProfileOptionsRecycler)
        binding.backAndRestLayout.setOnClickListener {
          //  dataSource.generateBackupDataAsync().await()
            var aDialog : AlertDialog? = null
            val builder  = AlertDialog.Builder(ctx).apply {
                val dBinding : DialogBackupAndRestoreBinding =
                    DialogBackupAndRestoreBinding.inflate(layoutInflater)
                dBinding.backupButton.setOnClickListener {
                    dBinding.backupButtonsLayout.visibility = View.GONE
                    dBinding.backupProgressLayout.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.Main).launch {
                        val backupData = dataSource.generateBackupDataAsync().await()
                        ApiService.apiService?.backupNow(
                            "Bearer " + Constant.authorizationToken,
                            backupData
                        )?.enqueue(object : Callback<BackupResponse> {
                            override fun onResponse(
                                call: Call<BackupResponse>,
                                response: Response<BackupResponse>
                            ) {
                                dBinding.backupProgressLayout.visibility = View.GONE
                                Log.d("HabitBackupResponse", "${response.body()?.message}")
                                if (response.isSuccessful && response.body()?.status == true) {
                                    dBinding.backupSuccessLayout.visibility = View.VISIBLE
                                    dBinding.backupFinishButton.setOnClickListener {
                                        aDialog?.dismiss()
                                    }
                                } else if (
                                    response.body()?.message?.lowercase()
                                        ?.contains("token") == true
                                ) {
                                    MainActivity.loginAgain(ctx, dataSource)
                                } else {
                                    dBinding.backupErrorLayout.visibility = View.VISIBLE
                                    dBinding.errorCloseButton.setOnClickListener {
                                        aDialog?.dismiss()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<BackupResponse>, t: Throwable) {
                                dBinding.backupProgressLayout.visibility = View.GONE
                                dBinding.backupErrorLayout.visibility = View.VISIBLE
                                dBinding.errorCloseButton.setOnClickListener {
                                    aDialog?.dismiss()
                                }
                            }
                        })
                    }
                }
                dBinding.restoreButton.setOnClickListener {
                    dBinding.backupButtonsLayout.visibility = View.GONE
                    dBinding.backupWarningLayout.visibility = View.VISIBLE
                    dBinding.restoreCancelButton.setOnClickListener {
                        aDialog?.dismiss()
                    }
                    dBinding.restoreProceedButton.setOnClickListener {
                        dBinding.backupWarningLayout.visibility = View.GONE
                        dBinding.backupProgressLayout.visibility = View.VISIBLE
                        ApiService.apiService?.restoreNow("Bearer "+Constant.authorizationToken)
                            ?.enqueue(object : Callback<RestoreResponse> {
                                override fun onResponse(
                                    call: Call<RestoreResponse>,
                                    response: Response<RestoreResponse>
                                ) {
                                    Log.d("RestoreResponse", "${response.body()?.message}")
                                    dBinding.backupProgressLayout.visibility = View.GONE
                                    if (response.isSuccessful && response.body()?.status == true) {
                                        val restoreData = response.body()?.data
                                        if (restoreData != null) {
                                            if (!restoreData.restoreJson.isNullOrBlank()) {
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    val success = dataSource.restoreHabitsAsync(restoreData.restoreJson!!).await()
                                                    if (success) {
                                                        (ctx as MainActivity).finishAffinity()
                                                        ctx.startActivity(Intent(ctx, LoginActivity::class.java))
                                                    } else {
                                                        dBinding.backupErrorLayout.visibility = View.VISIBLE
                                                        dBinding.errorCloseButton.setOnClickListener {
                                                            aDialog?.dismiss()
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else if (response.body()?.message?.lowercase()?.contains("token") == true) {
                                        MainActivity.loginAgain(ctx, dataSource)
                                    } else {
                                        dBinding.backupErrorLayout.visibility = View.VISIBLE
                                        dBinding.errorCloseButton.setOnClickListener {
                                            aDialog?.dismiss()
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<RestoreResponse>, t: Throwable) {
                                    dBinding.backupProgressLayout.visibility = View.GONE
                                    dBinding.backupErrorLayout.visibility = View.VISIBLE
                                    dBinding.errorCloseButton.setOnClickListener {
                                        aDialog?.dismiss()
                                    }
                                }
                            })
                    }
                }
                setView(dBinding.root)
            }
            aDialog = builder.create()
            aDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            aDialog.show()
         /*   if (isSignInCardExpand) {
                binding.loginCollapsedLayout.visibility = View.GONE
                isSignInCardExpand = false
            } else {
                binding.loginCollapsedLayout.visibility = View.VISIBLE
                isSignInCardExpand = true
            }  */
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