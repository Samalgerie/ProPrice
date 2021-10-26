package dz.samalgeria.proprice.view.activities

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dz.samalgeria.proprice.R

class ProPriceActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wemix)
        navController = Navigation.findNavController(this, R.id.fragment)
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt("applyAnimation", 1)
            apply()
        }
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        navView.setupWithNavController(navController)
        navView.selectedItemId = R.id.navigation_calculator


    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)

    }

}