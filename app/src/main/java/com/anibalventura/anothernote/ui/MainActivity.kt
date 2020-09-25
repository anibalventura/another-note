package com.anibalventura.anothernote.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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
import com.anibalventura.anothernote.utils.shareText
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // Use DataBinding.
    private lateinit var binding: ActivityMainBinding

    // NavController.
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        // Set theme after splash screen.
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        // Use DataBinding to set the activity view.
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        // Setup navigation.
        setupNavigation()

        // Setup theme.
        setupTheme(this)
    }

    /**
     * Called when the hamburger menu or back button are pressed on the Toolbar.
     * Delegate this to Navigation.
     */
    override fun onSupportNavigateUp() = navigateUp(
        findNavController(R.id.navHostFragment), binding.drawerLayout
    )

    /**
     * Setup Navigation for this Activity.
     */
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
            when (destination.id) {
                // Show title.
                R.id.noteFragment -> toolBar.setDisplayShowTitleEnabled(true)
                R.id.archiveFragment -> toolBar.setDisplayShowTitleEnabled(true)
                R.id.trashFragment -> toolBar.setDisplayShowTitleEnabled(true)

                // Disable title .
                R.id.noteAddFragment -> toolBar.setDisplayShowTitleEnabled(false)
                R.id.noteUpdateFragment -> toolBar.setDisplayShowTitleEnabled(false)
                R.id.archiveUpdateFragment -> toolBar.setDisplayShowTitleEnabled(false)
                R.id.trashUpdateFragment -> toolBar.setDisplayShowTitleEnabled(false)
            }
        }
    }

    override fun onNavigationItemSelected(menu: MenuItem): Boolean {
        when (menu.itemId) {
            R.id.noteFragment -> navController.navigate(R.id.noteFragment)
            R.id.archiveFragment -> navController.navigate(R.id.archiveFragment)
            R.id.trashFragment -> navController.navigate(R.id.trashFragment)
            R.id.shareApp -> shareText(this, getString(R.string.share_app))
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