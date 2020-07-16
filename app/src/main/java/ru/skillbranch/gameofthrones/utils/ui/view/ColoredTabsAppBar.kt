package ru.skillbranch.gameofthrones.utils.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils
import androidx.annotation.ColorRes
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ru.skillbranch.gameofthrones.databinding.ViewColoredTabsAppBarBinding
import kotlin.math.max


class ColoredTabsAppBar(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    var coloredTabs: List<ColoredTab> = emptyList()
    val toolbar: Toolbar
        get() = vb.toolbar

    val tabLayout: TabLayout
        get() = vb.tabs

    private val vb = ViewColoredTabsAppBarBinding.inflate(LayoutInflater.from(context), this)
    private var lastMotionEvent: MotionEvent? = null
    private var lastAnimation: Animator? = null

    init {
        initToolbarDimension()

    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP -> {
                //TODO: только если событие принадлежит tabLayout
                lastMotionEvent = event
            }
        }
        return super.dispatchTouchEvent(event)
    }

    // fun animateBackground(position: Int, positionOffset: Float, only )

    fun animateTabChangeOnlyOnEvent(position: Int) {
        lastMotionEvent?.let {
            //animateTabChange(position, it.rawX, it.rawY)
            animateTabChange(position)
        }
        lastMotionEvent = null
    }

    fun animateTabChange(
        position: Int,
        animationStartX: Float? = null,
        animationStartY: Float? = null
    ) {
        val tab = checkNotNull(vb.tabs.getTabAt(position))
        val rect = Rect()
        tab.view.getGlobalVisibleRect(rect)
        val newBackgroundColor = checkNotNull(getColorAtPosition(position))
        lastAnimation = with(vb.revealView) {
            visibility = View.VISIBLE
            setBackgroundColor(newBackgroundColor)
            val radius = max(width, height) * 1.2f
            val startX = (animationStartX ?: rect.exactCenterX()).toInt()
            val startY = (animationStartY ?: rect.exactCenterY()).toInt()
            ViewAnimationUtils.createCircularReveal(this, startX, startY, 0f, radius)
                .apply {
                    duration = 2000
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animator: Animator) {
                            vb.backgroundView.setBackgroundColor(newBackgroundColor)
                            visibility = View.INVISIBLE
                            lastAnimation = null
                        }
                    })
                }
        }.also {
            it.start()
        }
    }

    private fun getBackgroundColor(position: Int, positionOffset: Float): Int? {
        val nextPosition = position +
                when {
                    positionOffset == 0f -> 0
                    positionOffset > 0 -> 1
                    else -> -1
                }
        val startColor = getColorAtPosition(position)
        val endColor = getColorAtPosition(nextPosition)
        val argbEvaluator = ArgbEvaluator()
        return if (startColor != null && endColor != null) {
            when (startColor) {
                endColor -> startColor
                else -> argbEvaluator.evaluate(positionOffset, startColor, endColor) as Int
            }
        } else {
            null
        }
    }

    private fun getColorAtPosition(position: Int): Int? {
        val colorId = if (coloredTabs.size > position) coloredTabs[position].colorId else null
        return colorId?.let { ContextCompat.getColor(context, it) }
    }

    //FIXME: лучше, наверное, вынести в CustomAppBarLayout
    private fun initToolbarDimension() {
        val params = toolbar.layoutParams as AppBarLayout.LayoutParams
        params.setMargins(0, getStatusBarHeight(), 0, 0)
        toolbar.layoutParams = params
    }

    //FIXME: погуглить более надёжный способ
    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    data class ColoredTab(
        val name: String,
        @ColorRes val colorId: Int
    )

    class ColoredTabsAppBarMediator(
        private val tabsAppBar: ColoredTabsAppBar,
        private val viewPager: ViewPager2
    ) {
        private val tabMediator =
            TabLayoutMediator(tabsAppBar.tabLayout, viewPager) { tab, position ->
                tab.text = tabsAppBar.coloredTabs[position].name
            }
        var onPageSelected: ((position: Int) -> Unit)? = null

        fun attach() {
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    with(tabsAppBar) {
                        if (lastMotionEvent == null && lastAnimation == null) {
                            getBackgroundColor(position, positionOffset)?.let {
                                vb.backgroundView.setBackgroundColor(it)
                            }
                        }
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                    if (state == ViewPager2.SCROLL_STATE_IDLE) {
                        tabsAppBar.animateTabChangeOnlyOnEvent(tabsAppBar.tabLayout.selectedTabPosition)
                    }
                }

                override fun onPageSelected(position: Int) {
                    //tabsAppBar.animateTabChange(position, onlyOnEvent = true)
                }
            })
            tabMediator.attach()
        }
    }

}