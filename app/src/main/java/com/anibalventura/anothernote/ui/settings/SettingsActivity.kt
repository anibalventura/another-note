package com.anibalventura.anothernote.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.anibalventura.anothernote.utils.Constants.THEME
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.utils.setupTheme
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener,
    Preference.SummaryProvider<ListPreference> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()

        // Set toolbar.
        setupToolbar()

        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    private fun setupToolbar() {
        // Set the toolbar.
        setSupportActionBar(toolbar)
        // Update toolbar title.
        this.title = getString(R.string.menu_settings)
        // Set navigateUp button.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /*
     * Automatic implement changes on preferences.
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        // Update theme selected.
        setupTheme(this)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_preferences, rootKey)

            // Get preferences.
            bindSharedPrefSummary(findPreference(THEME)!!)
        }

        companion object {
            /**
             * A preference value change listener that updates the preference's summary
             * to reflect its new value.
             */
            private val sBindPreferenceSummaryToValueListener =
                Preference.OnPreferenceChangeListener { preference, value ->

                    val stringValue = value.toString()

                    if (preference is ListPreference) {
                        // For list preferences, look up the correct display value in
                        // the preference's 'entries' list.
                        val index = preference.findIndexOfValue(stringValue)

                        // Set the summary to reflect the new value.
                        preference.setSummary(
                            when {
                                index >= 0 -> preference.entries[index]
                                else -> null
                            }
                        )
                    } else {
                        // For all other preferences, set the summary to the value's
                        // simple string representation.
                        preference.summary = stringValue
                    }
                    true
                }

            private fun bindSharedPrefSummary(preference: Preference) {
                // Set the listener to watch for value changes.
                preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

                // Trigger the listener immediately with the preference's
                // current value.
                sBindPreferenceSummaryToValueListener.onPreferenceChange(
                    preference,
                    PreferenceManager
                        .getDefaultSharedPreferences(preference.context)
                        .getString(preference.key, "")
                )
            }
        }
    }

    override fun provideSummary(preference: ListPreference?): CharSequence =
        if (preference?.key == THEME) preference.entry
        else "Unknown Preference"
}