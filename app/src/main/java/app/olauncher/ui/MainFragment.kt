package app.olauncher.ui

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.olauncher.R
import app.olauncher.data.AppModel
import app.olauncher.data.Constants
import app.olauncher.data.Prefs
import app.olauncher.helper.MainViewModel
import app.olauncher.helper.OnSwipeTouchListener
import app.olauncher.helper.isPackageInstalled
import app.olauncher.helper.showToastShort
import kotlinx.android.synthetic.main.main_fragment.*
import java.lang.reflect.Method


class MainFragment : Fragment(), View.OnClickListener, View.OnLongClickListener {

    private lateinit var prefs: Prefs
    private lateinit var viewModel: MainViewModel
    private lateinit var deviceManager: DevicePolicyManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prefs = Prefs(requireContext())
        viewModel = activity?.run {
            ViewModelProvider(this).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        deviceManager =
            context?.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

        initClickListeners()
        initObservers()

        mainLayout.setOnTouchListener(getSwipeGestureListener(requireContext()))
    }

    override fun onResume() {
        super.onResume()
        populateHomeApps(false)
        viewModel.isOlauncherDefault()
    }

    private fun populateHomeApps(appCountUpdated: Boolean) {
        if (appCountUpdated) hideHomeApps()
        val homeAppsNum = prefs.homeAppsNum
        if (homeAppsNum == 0) return

        val pm = requireContext().packageManager

        homeApp1.visibility = View.VISIBLE
        if (!setHomeAppText(homeApp1, prefs.appName1, prefs.appPackage1, pm)) {
            prefs.appName1 = ""
            prefs.appPackage1 = ""
        }
        if (homeAppsNum == 1) return

        homeApp2.visibility = View.VISIBLE
        if (!setHomeAppText(homeApp2, prefs.appName2, prefs.appPackage2, pm)) {
            prefs.appName2 = ""
            prefs.appPackage2 = ""
        }
        if (homeAppsNum == 2) return

        homeApp3.visibility = View.VISIBLE
        if (!setHomeAppText(homeApp3, prefs.appName3, prefs.appPackage3, pm)) {
            prefs.appName3 = ""
            prefs.appPackage3 = ""
        }
        if (homeAppsNum == 3) return

        homeApp4.visibility = View.VISIBLE
        if (!setHomeAppText(homeApp4, prefs.appName4, prefs.appPackage4, pm)) {
            prefs.appName4 = ""
            prefs.appPackage4 = ""
        }
        if (homeAppsNum == 4) return

        homeApp5.visibility = View.VISIBLE
        if (!setHomeAppText(homeApp5, prefs.appName5, prefs.appPackage5, pm)) {
            prefs.appName5 = ""
            prefs.appPackage5 = ""
        }
        if (homeAppsNum == 5) return

        homeApp6.visibility = View.VISIBLE
        if (!setHomeAppText(homeApp6, prefs.appName6, prefs.appPackage6, pm)) {
            prefs.appName6 = ""
            prefs.appPackage6 = ""
        }
    }

    private fun setHomeAppText(
        textView: TextView,
        appName: String,
        packageName: String,
        pm: PackageManager
    ): Boolean {
        if (isPackageInstalled(packageName, pm)) {
            textView.text = appName
            return true
        }
        textView.text = ""
        return false
    }

    private fun hideHomeApps() {
        homeApp1.visibility = View.GONE
        homeApp2.visibility = View.GONE
        homeApp3.visibility = View.GONE
        homeApp4.visibility = View.GONE
        homeApp5.visibility = View.GONE
        homeApp6.visibility = View.GONE
    }

    private fun initClickListeners() {
        homeApp1.setOnClickListener(this)
        homeApp2.setOnClickListener(this)
        homeApp3.setOnClickListener(this)
        homeApp4.setOnClickListener(this)
        homeApp5.setOnClickListener(this)
        homeApp6.setOnClickListener(this)
        homeApp1.setOnLongClickListener(this)
        homeApp2.setOnLongClickListener(this)
        homeApp3.setOnLongClickListener(this)
        homeApp4.setOnLongClickListener(this)
        homeApp5.setOnLongClickListener(this)
        homeApp6.setOnLongClickListener(this)

        clock.setOnClickListener(this)
        date.setOnClickListener(this)
        setDefaultLauncher.setOnClickListener(this)
    }

    private fun initObservers() {
        viewModel.refreshHome.observe(viewLifecycleOwner, Observer<Boolean> {
            populateHomeApps(it)
        })
        viewModel.firstOpen.observe(viewLifecycleOwner, Observer<Boolean> {
            if (it) tips.visibility = View.VISIBLE
            else tips.visibility = View.GONE
        })
        viewModel.isOlauncherDefault.observe(viewLifecycleOwner, Observer<Boolean> {
            if (tips.visibility == View.VISIBLE) return@Observer
            if (it) setDefaultLauncher.visibility = View.GONE
            else setDefaultLauncher.visibility = View.VISIBLE
        })
    }

    override fun onClick(view: View) {
        when (view.id) {
//            R.id.wallpaper -> {
//                val width = Resources.getSystem().displayMetrics.widthPixels
//                val height = Resources.getSystem().displayMetrics.heightPixels
//                viewModel.setWallpaper(width, height, "https://images.unsplash.com/photo-1516410529446-2c777cb7366d?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1834&q=80")
//            }

            R.id.homeApp1 -> if (prefs.appPackage1.isEmpty()) onLongClick(view)
            else launchAppEvent(prefs.appName1, prefs.appPackage1)

            R.id.homeApp2 -> if (prefs.appPackage2.isEmpty()) onLongClick(view)
            else launchAppEvent(prefs.appName2, prefs.appPackage2)

            R.id.homeApp3 -> if (prefs.appPackage3.isEmpty()) onLongClick(view)
            else launchAppEvent(prefs.appName3, prefs.appPackage3)

            R.id.homeApp4 -> if (prefs.appPackage4.isEmpty()) onLongClick(view)
            else launchAppEvent(prefs.appName4, prefs.appPackage4)

            R.id.homeApp5 -> if (prefs.appPackage5.isEmpty()) onLongClick(view)
            else launchAppEvent(prefs.appName5, prefs.appPackage5)

            R.id.homeApp6 -> if (prefs.appPackage6.isEmpty()) onLongClick(view)
            else launchAppEvent(prefs.appName6, prefs.appPackage6)

            R.id.clock -> openAlarmApp()
            R.id.date -> openCalendar()
            R.id.setDefaultLauncher -> viewModel.resetDefaultLauncherApp(requireContext())
        }
    }

    override fun onLongClick(view: View): Boolean {
        when (view.id) {
            R.id.homeApp1 -> showAppList(Constants.FLAG_SET_HOME_APP_1)
            R.id.homeApp2 -> showAppList(Constants.FLAG_SET_HOME_APP_2)
            R.id.homeApp3 -> showAppList(Constants.FLAG_SET_HOME_APP_3)
            R.id.homeApp4 -> showAppList(Constants.FLAG_SET_HOME_APP_4)
            R.id.homeApp5 -> showAppList(Constants.FLAG_SET_HOME_APP_5)
            R.id.homeApp6 -> showAppList(Constants.FLAG_SET_HOME_APP_6)
        }
        return true
    }

    private fun showAppList(flag: Int) {
        viewModel.getAppList()
        findNavController().navigate(
            R.id.action_mainFragment_to_appListFragment,
            bundleOf("flag" to flag)
        )
    }

    private fun openAlarmApp() {
        try {
            val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
            startActivity(intent)
        } catch (e: java.lang.Exception) {
            Log.d("TAG", e.toString())
        }
    }

    private fun openCalendar() {
        try {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_APP_CALENDAR)
            startActivity(intent)
        } catch (e: java.lang.Exception) {

        }
    }

    private fun launchAppEvent(appName: String, packageName: String) {
        viewModel.selectedApp(
            AppModel(appName, packageName),
            Constants.FLAG_LAUNCH_APP
        )
    }

    // Source: https://stackoverflow.com/a/51132142
    @SuppressLint("WrongConstant", "PrivateApi")
    private fun expandNotificationDrawer(context: Context) {
        try {
            val statusBarService = context.getSystemService("statusbar")
            val methodName = "expandNotificationsPanel"
            val statusBarManager: Class<*> = Class.forName("android.app.StatusBarManager")
            val method: Method = statusBarManager.getMethod(methodName)
            method.invoke(statusBarService)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openDialerApp() {
        try {
            val sendIntent = Intent(Intent.ACTION_DIAL)
            startActivity(sendIntent)
        } catch (e: java.lang.Exception) {

        }
    }

    private fun openCameraApp() {
        try {
            val sendIntent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
            startActivity(sendIntent)
        } catch (e: java.lang.Exception) {

        }
    }

    private fun lockPhone() {
        try {
            deviceManager.lockNow()
        } catch (e: java.lang.Exception) {
            prefs.lockModeOn = false
            showToastShort(
                requireContext(),
                "Olauncher failed to lock phone"
            )
            findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
        }
    }

    private fun getSwipeGestureListener(context: Context): View.OnTouchListener {
        return object : OnSwipeTouchListener(context) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                openCameraApp()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                openDialerApp()
            }

            override fun onSwipeUp() {
                super.onSwipeUp()
                showAppList(Constants.FLAG_LAUNCH_APP)
            }

            override fun onSwipeDown() {
                super.onSwipeDown()
                expandNotificationDrawer(context)
            }

            override fun onLongClick() {
                super.onLongClick()
                findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
                viewModel.firstOpen(false) // To make tips disappear
            }

            override fun onDoubleClick() {
                super.onDoubleClick()
                if (prefs.lockModeOn) lockPhone()
            }
        }
    }
}