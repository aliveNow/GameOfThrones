package ru.skillbranch.gameofthrones.utils.ui

import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.gameofthrones.utils.ui.data.EventLiveData

//region VIEW
inline fun View.addOneTimeOnGlobalLayoutListener(crossinline listener: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(
        object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                listener.invoke()
            }
        })
}
//endregion

//region RECYCLER VIEW
fun RecyclerView.scrollToPositionAnimated(position: Int, animated: Boolean = true) {
    val linearLayoutManager = layoutManager as? LinearLayoutManager
    linearLayoutManager?.let {
        if (animated) {
            it.smoothScrollToPosition(this, null, position)
        } else {
            it.scrollToPositionWithOffset(position, 0)
        }
    }
}
//endregion

//region FRAGMENT
fun Fragment.setDisplayHomeAsUpEnabled(isEnabled: Boolean) {
    (requireActivity() as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(isEnabled)
}

fun Fragment.getDrawableById(@DrawableRes drawableId: Int) =
    requireContext().getDrawable(drawableId)

inline fun <T> EventLiveData<T>.observeEvent(
    fragment: Fragment,
    crossinline observer: (T?) -> Unit
) =
    this.observe(
        fragment.viewLifecycleOwner,
        Observer { t -> t.getContentIfNotHandled()?.apply { observer.invoke(this) } })

inline fun <T> LiveData<T>.observeState(fragment: Fragment, crossinline observer: (T) -> Unit) =
    this.observe(fragment.viewLifecycleOwner, Observer { observer.invoke(it) })
//endregion