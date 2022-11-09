package com.tjcg.habitapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.data.HabitDataSource
import com.tjcg.habitapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val ANIM_DURATION = 300L

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var dataSource: HabitDataSource
 //   private lateinit var habitViewModel : HabitViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navView = binding.navView
        navAddBtn = binding.navAddButton
        bottomNavController = Navigation.findNavController(this, R.id.bottom_nav_fragment)
        binding.navPage1.setOnClickListener { navigateToPage(Constant.PAGE_1) }
        binding.navPage2.setOnClickListener { navigateToPage(Constant.PAGE_2) }
        binding.navPage3.setOnClickListener { navigateToPage(Constant.PAGE_3)}
        binding.navPage4.setOnClickListener { navigateToPage(Constant.PAGE_4) }
        binding.navAddButton.setOnClickListener {
            bottomNavController.navigate(R.id.bottom_habitPresetsFragment)
        }
        askAutoStartPermission()


        Handler(Looper.getMainLooper()).postDelayed( {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
   /*
        setContentView(binding.root)
        habitViewModel = dataSource.provideViewModel()
        navController = findNavController(R.id.mainNavFragment)

        binding.navPage1.setOnClickListener { navigateToPage(Constant.PAGE_1) }
        binding.navPage2.setOnClickListener { navigateToPage(Constant.PAGE_2) }
        binding.navPage3.setOnClickListener { navigateToPage(Constant.PAGE_3)}
        binding.navPage4.setOnClickListener { navigateToPage(Constant.PAGE_4) }
        binding.navAddButton.setOnClickListener {
            navController.navigate(R.id.habitPresetsFragment)
        }
        habitViewModel.selectedAppPage.observe(this) {
            navigateToPage(it)
        }
        /*      navView = binding.navView

              val navController =
                  findNavController(R.id.nav_host_fragment_activity_bottom_navigation_main)
              // Passing each menu ID as a set of Ids because each
              // menu should be considered as top level destinations.
       /*       val appBarConfiguration = AppBarConfiguration(
                  setOf(
                      R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
                  )
              )   */
      //        setupActionBarWithNavController(navController, appBarConfiguration)
              navView.setupWithNavController(navController)  */

    */
    }

    private fun askAutoStartPermission() {
        if (dataSource.sharedPreferences.getBoolean(Constant.PREFS_ASKED_AUTORUN, false)) {
            return
        }
        val powerManagerIntents: Array<Intent> = arrayOf(
            Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            Intent().setComponent(ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),
            Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
            Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            Intent().setComponent(ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            Intent().setComponent(ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            Intent().setComponent(ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),
            Intent().setComponent(ComponentName("com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity")),
            Intent().setComponent(ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity"))
        )
        var launchIntent : Intent? = null
        for(i in powerManagerIntents) {
            if (packageManager.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                launchIntent = i
                break
            }
        }
        if(launchIntent == null) {
            return
        }
        val bottomSheetDialog = BottomSheetDialog(this).apply {
            setContentView(R.layout.bottom_sheet_auto_start_permission)
            val cancelButton = findViewById<Button>(R.id.autorunCancel)
            val nextButton = findViewById<Button>(R.id.autorunNext)
            cancelButton?.setOnClickListener {
                this.dismiss()
            }
            nextButton?.setOnClickListener {
                this.dismiss()
                startActivity(launchIntent)
                dataSource.sharedPreferences.edit().putBoolean(
                    Constant.PREFS_ASKED_AUTORUN, true).apply()
            }
        }
        bottomSheetDialog.show()
    }

    private fun navigateToPage(page: Int) {
        binding.navActive1.visibility = View.GONE
        binding.navActive2.visibility = View.GONE
        binding.navActive3.visibility = View.GONE
        binding.navActive4.visibility = View.GONE
        currentPage = page
        when(page) {
            Constant.PAGE_1 -> {
                bottomNavController.popBackStack()
                bottomNavController.navigate(R.id.bottom_todayFragment)
                binding.navActive1.visibility = View.VISIBLE
            }
            Constant.PAGE_2 -> {
                bottomNavController.popBackStack()
                bottomNavController.navigate(R.id.bottom_journeyMainFragment)
                binding.navActive2.visibility = View.VISIBLE
            }
            Constant.PAGE_3 -> {
                bottomNavController.popBackStack()
                bottomNavController.navigate(R.id.bottom_historyMainFragment)
                binding.navActive3.visibility = View.VISIBLE
            }
            Constant.PAGE_4 -> {
                bottomNavController.popBackStack()
                bottomNavController.navigate(R.id.bottom_myProfileMainFragment)
                binding.navActive4.visibility = View.VISIBLE
            }
        }
    }


    companion object {
        private const val collapseExpandDuration = 100L
        private val animationHandler : Handler = Handler(Looper.getMainLooper())
        var currentPage = Constant.PAGE_1
        lateinit var navView : ConstraintLayout
        lateinit var navAddBtn : ImageFilterView
        var isNavShowing = true


        fun showBottomNavigation() {
            Animator.slideUpShow(navView, ANIM_DURATION)
            Animator.zoomIn(navAddBtn, ANIM_DURATION)
            isNavShowing = true
        }

        fun hideBottomNavigation() {
            Animator.slideDownGone(navView, ANIM_DURATION)
            Animator.zoomOut(navAddBtn, ANIM_DURATION)
            isNavShowing = false
        }

        // Animation functions
        // animations removed from this functions because layout changes will notified by android itself now.
     /*   private fun collapseCard(viewToCollapse: View) {
            //     Animator.scaleUp(viewToCollapse, collapseExpandDuration)
            //      animationHandler.postDelayed( {
            viewToCollapse.visibility = View.GONE
            //     }, collapseExpandDuration)
        }  */

     /*   private fun expandCard(viewToExpand : View) {
            viewToExpand.visibility = View.VISIBLE
            //     Animator.scaleDown(viewToExpand, collapseExpandDuration)
        }  */

        fun rotateWhileExpand(viewToRotate : View) {
            Animator.rotateClockwise(viewToRotate, collapseExpandDuration)
            animationHandler.postDelayed({
                viewToRotate.rotation = 90f
            }, collapseExpandDuration)
        }

        fun rotateWhileCollapse(viewToRotate: View) {
            Animator.rotateAntiClock(viewToRotate, collapseExpandDuration)
            animationHandler.postDelayed( {
                viewToRotate.rotation = 0f
            }, collapseExpandDuration)
        }

        fun loginAgain(ctx: Context, dataSource: HabitDataSource) {
            dataSource.sharedPreferences.edit().putString(Constant.PREFS_AUTHORIZATION, "").apply()
            (ctx as MainActivity).finish()
            ctx.startActivity(Intent(ctx, LoginActivity::class.java))
        }
    }
}