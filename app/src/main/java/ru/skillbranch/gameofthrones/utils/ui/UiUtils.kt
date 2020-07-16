package ru.skillbranch.gameofthrones.utils.ui

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import ru.skillbranch.gameofthrones.utils.ui.data.EventLiveData


fun Fragment.setDisplayHomeAsUpEnabled(isEnabled: Boolean) {
    (requireActivity() as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(isEnabled)
}

fun Fragment.getDrawableById(@DrawableRes drawableId: Int) =
    requireContext().getDrawable(drawableId)

inline fun <T> EventLiveData<T>.observeEvent(fragment: Fragment, crossinline observer: (T?) -> Unit) =
    this.observe(
        fragment.viewLifecycleOwner,
        Observer { t -> t.getContentIfNotHandled()?.apply { observer.invoke(this) } })

inline fun <T> LiveData<T>.observeState(fragment: Fragment, crossinline observer: (T) -> Unit) =
    this.observe(fragment.viewLifecycleOwner, Observer { observer.invoke(it) })

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