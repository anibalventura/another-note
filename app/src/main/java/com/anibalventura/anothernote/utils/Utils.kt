package com.anibalventura.anothernote.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.anibalventura.anothernote.CONST
import kotlinx.coroutines.Dispatchers

/*
* SharedPreferences.
*/
fun sharedPref(context: Context): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(context)
}

/*
 * Show Toast.
 */
fun showToast(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

/*
 * Setup the current theme.
 */
fun setupTheme(context: Context) {
    // Set the theme from the sharedPref value.
    when (sharedPref(context).getString(CONST.THEME, "0")) {
        "1" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        "2" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        "0" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}

/*
 * Share any text on a sharesheet.
 */
fun shareText(context: Context, msg: String) {
    // Create the intent to send.
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, msg)
        type = "text/plain"
    }

    // Send the intent.
    context.let {
        Intent(context, Dispatchers.Main::class.java)
        context.startActivity(Intent.createChooser(sendIntent, null))
    }
}

/*
 * Hide keyboard.
 */
fun hideKeyboard(activity: Activity) {
    val inputMethodManager =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val currentFocusedView = activity.currentFocus

    currentFocusedView?.let {
        inputMethodManager.hideSoftInputFromWindow(
            currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}