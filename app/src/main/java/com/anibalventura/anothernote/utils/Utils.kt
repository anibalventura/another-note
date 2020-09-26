package com.anibalventura.anothernote.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.colorChooser
import com.anibalventura.anothernote.App
import com.anibalventura.anothernote.Constants.THEME
import com.anibalventura.anothernote.R
import kotlinx.coroutines.Dispatchers

val app = App.resourses!!

/** ========================== SharedPreferences. ========================== **/
fun sharedPref(context: Context): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(context)
}

/** ============================= Setup theme. ============================= **/
fun setupTheme(context: Context) {
    // Set the theme from the sharedPref value.
    when (sharedPref(context).getString(THEME, "0")) {
        "1" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        "2" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        "0" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}

/** =========================== Toast message. =========================== **/
fun showToast(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

/** ============================= Share text. ============================= **/
fun shareText(context: Context, msg: String) {
    // Create the intent.
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

/** ======================= Change note background color. ======================= **/
fun changeNoteBackgroundColor(view: View, toolbar: Toolbar?, context: Context) {

    // Get colors.
    val colors = intArrayOf(
        ActivityCompat.getColor(context, R.color.transparent),
        ActivityCompat.getColor(context, R.color.red),
        ActivityCompat.getColor(context, R.color.orange),
        ActivityCompat.getColor(context, R.color.yellow),
        ActivityCompat.getColor(context, R.color.green),
        ActivityCompat.getColor(context, R.color.blue),
        ActivityCompat.getColor(context, R.color.blue2),
        ActivityCompat.getColor(context, R.color.blue3),
        ActivityCompat.getColor(context, R.color.purple),
        ActivityCompat.getColor(context, R.color.pink),
        ActivityCompat.getColor(context, R.color.brown),
        ActivityCompat.getColor(context, R.color.grey)
    )

    // Create dialog to choose color.
    MaterialDialog(context).show {
        title(R.string.dialog_choose_color)
        colorChooser(colors) { _, color ->
            view.setBackgroundColor(color)
            toolbar?.setBackgroundColor(color)
        }
        negativeButton(R.string.dialog_negative)
        positiveButton(R.string.dialog_select)
    }
}

/** ======================= Hide Soft Keyboard. ======================= **/
fun hideSoftKeyboard(activity: Activity) {
    val inputMethodManager =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val currentFocusedView = activity.currentFocus

    currentFocusedView?.let {
        inputMethodManager.hideSoftInputFromWindow(
            currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}