package com.tjcg.habitapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tjcg.habitapp.MainActivity
import com.tjcg.habitapp.R
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.databinding.DialogDeleteAllBinding
import com.tjcg.habitapp.databinding.FragmentMyProfileGeneralSettingsBinding

class MyProfileGeneralSettingsFragment : Fragment() {

    private lateinit var binding : FragmentMyProfileGeneralSettingsBinding
    private lateinit var ctx: Context
    private var isFirstDayExpanded = false
    private lateinit var deleteDialog: DeleteDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.hideBottomNavigation()
        MainActivity.currentPage = Constant.PAGE_IN
        binding = FragmentMyProfileGeneralSettingsBinding.inflate(
            inflater, container, false)
        deleteDialog = DeleteDialog.getInstance()
        binding.timePeriodLayout.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_profile_g_settings_to_myProfileTimePeriodSettings)
        }
        binding.firstDayExpandLayout.setOnClickListener {
            if (isFirstDayExpanded) {
                MainActivity.rotateWhileCollapse(binding.firstDayArrow)
                binding.dayPicker.visibility = View.GONE
                isFirstDayExpanded = false
            } else {
                MainActivity.rotateWhileExpand(binding.firstDayArrow)
                binding.dayPicker.visibility = View.VISIBLE
                isFirstDayExpanded = true
            }
        }
        binding.dayPicker.maxValue = 6
        binding.dayPicker.displayedValues = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday")
        binding.privacyPolicyCard.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_profile_g_settings_to_navigation_privacy_policy)
        }
        binding.deleteDataCard.setOnClickListener {
            deleteDialog.show(parentFragmentManager, "delete")
        }
        return binding.root
    }

    class DeleteDialog : DialogFragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            val binding = DialogDeleteAllBinding.inflate(inflater, container, false)
            binding.cancelDelete.setOnClickListener {
                this.dialog?.dismiss()
            }
            binding.deleteYes.setOnClickListener {
                binding.deleteAnimation.visibility  = View.VISIBLE
                binding.dialogMain.visibility = View.GONE
            }
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            this.dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            super.onViewCreated(view, savedInstanceState)
        }

        companion object {
            fun getInstance() : DeleteDialog {
                return DeleteDialog()
            }
        }
    }
}