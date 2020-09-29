package com.anibalventura.anothernote.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.databinding.ActivityMainBinding
import com.anibalventura.anothernote.utils.setupTheme
import com.anibalventura.anothernote.utils.share
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // DataBinding.
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    // NavController.
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        // Set theme after splash screen.
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        // Use DataBinding to set the activity view.
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Setup navigation.
        setupNavigation()

        // Setup theme.
        setupTheme(this)
    }

    // Called when the hamburger menu or back button are pressed on the Toolbar.
    // Delegate this to Navigation.
    override fun onSupportNavigateUp() =
        navigateUp(findNavController(R.id.navHostFragment), binding.drawerLayout)

    private fun setupNavigation() {
        // Set the toolbar.
        setSupportActionBar(binding.toolbar)

        // Find the nav controller.
        navController = findNavController(R.id.navHostFragment)

        // Setup the action bar, tell it about the DrawerLayout.
        setupActionBarWithNavController(navController, binding.drawerLayout)

        // Setup navDrawer.
        binding.navigationView.setupWithNavController(navController)
        binding.navigationView.setNavigationItemSelectedListener(this)

        navController.addOnDestinationChangedListener { _, destination: NavDestination, _ ->
            val toolBar = supportActionBar ?: return@addOnDestinationChangedListener
            toolbar.setBackgroundColor(ActivityCompat.getColor(this, R.color.transparent))
            this.window.navigationBarColor = ActivityCompat.getColor(this, R.color.primaryColor)
            this.window.statusBarColor = ActivityCompat.getColor(this, R.color.primaryColor)

            if (destination.id == R.id.noteFragment
                || destination.id == R.id.archiveFragment
                || destination.id == R.id.trashFragment
            ) toolBar.setDisplayShowTitleEnabled(true)

            if (destination.id == R.id.noteAddFragment
                || destination.id == R.id.noteUpdateFragment
                || destination.id == R.id.archiveUpdateFragment
                || destination.id == R.id.trashUpdateFragment
            ) {
                toolBar.setDisplayHomeAsUpEnabled(false)
                toolBar.setDisplayShowTitleEnabled(false)
            }
        }
    }

    override fun onNavigationItemSelected(menu: MenuItem): Boolean {
        when (menu.itemId) {
            R.id.noteFragment -> navController.navigate(R.id.noteFragment)
            R.id.archiveFragment -> navController.navigate(R.id.archiveFragment)
            R.id.trashFragment -> navController.navigate(R.id.trashFragment)
            R.id.tellFriends -> share(this, getString(R.string.tell_friends))
            R.id.aboutFragment -> navController.navigate(R.id.aboutFragment)
            R.id.settingsActivity -> navController.navigate(R.id.settingsActivity)
        }

        // CLose navDrawer after selection.
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        // When drawer is open and press back button, close the drawer instead of close the app.
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}