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
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.color.colorChooser
import com.anibalventura.anothernote.App
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.utils.Constants.ARCHIVE_DISCARD
import com.anibalventura.anothernote.utils.Constants.NOTE_ADD_DISCARD
import com.anibalventura.anothernote.utils.Constants.NOTE_DISCARD
import com.anibalventura.anothernote.utils.Constants.THEME
import kotlinx.coroutines.Dispatchers

val resources = App.resourses!!

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
fun toast(context: Context, message: Int) {
    Toast.makeText(context, resources.getString(message), Toast.LENGTH_SHORT).show()
}

/** ============================= Share text. ============================= **/
fun share(context: Context, message: String) {
    // Create the intent.
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, message)
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
        ActivityCompat.getColor(context, R.color.plain),
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
    MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
        title(R.string.dialog_choose_color)
        colorChooser(colors) { _, color ->
            view.setBackgroundColor(color)
            toolbar?.setBackgroundColor(color)
        }
        negativeButton(R.string.dialog_negative)
        positiveButton(R.string.dialog_select)
    }
}

/** ======================= Discard changes. ======================= **/
fun discardDialog(from: String, context: Context, view: View) {
    MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
        title(R.string.dialog_discard)
        message(R.string.dialog_discard_confirmation)
        positiveButton(R.string.dialog_confirmation) {
            when (from) {
                NOTE_ADD_DISCARD -> view.findNavController()
                    .navigate(R.id.action_noteAddFragment_to_noteFragment)
                NOTE_DISCARD -> view.findNavController()
                    .navigate(R.id.action_noteUpdateFragment_to_noteFragment)
                ARCHIVE_DISCARD -> view.findNavController()
                    .navigate(R.id.action_archiveUpdateFragment_to_archiveFragment)
            }
            toast(context, R.string.dialog_discard_successful)
        }
        negativeButton(R.string.dialog_negative)
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