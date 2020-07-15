package ru.skillbranch.gameofthrones.utils.ui

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


fun Fragment.setDisplayHomeAsUpEnabled(isEnabled: Boolean) {
    (requireActivity() as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(isEnabled)
}

fun Fragment.hideSoftInput(clearFocus: Boolean) {
    with(requireActivity()) {
        val imm: InputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view: View? = currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        if (clearFocus) {
            view.clearFocus()
        }
    }
}