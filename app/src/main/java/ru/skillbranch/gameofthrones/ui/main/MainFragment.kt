package ru.skillbranch.gameofthrones.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.koin.getViewModel
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.HouseType
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.databinding.FragmentMainBinding
import ru.skillbranch.gameofthrones.ui.characters.list.CharactersListFragment
import kotlin.math.max

class MainFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var vb: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentMainBinding.inflate(inflater, container, false)
            .also { vb = it }
            .root
    }

    private fun initToolbarDimension() {
        val params =
            vb.toolbar.getLayoutParams() as AppBarLayout.LayoutParams
        params.setMargins(0, getStatusBarHeight(), 0, 0)
        vb.toolbar.setLayoutParams(params)
    }

    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbarDimension()

        (requireActivity() as AppCompatActivity).setSupportActionBar(vb.toolbar)
        vb.viewPager.adapter = ViewPagerFragmentStateAdapter(requireActivity())
        vb.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
           //     vb.backgroundView.setBackgroundColor(getBackgroundColor(position, positionOffset))
            }

            override fun onPageScrollStateChanged(state: Int) {
               if (state == ViewPager2.SCROLL_STATE_SETTLING) {
                    val position = vb.viewPager.currentItem
                    val currentThemeId = AppConfig.appThemeId
                    val newThemeId = if (position == 0) R.style.AppThemeOverlay_Lannister else R.style.AppThemeOverlay_Baratheon
                    if (currentThemeId != newThemeId) {
                        AppConfig.appThemeId = newThemeId
                        vb.toolbar.context.setTheme(newThemeId)
                    }
                }
            }

            override fun onPageSelected(position: Int) {

            }
        })
        vb.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    if (it.position > 0) {
                        val rectf = Rect()
                        it.view.getGlobalVisibleRect(rectf)
                        reveal(it.position, rectf.exactCenterX(), rectf.exactCenterY())
                    }
                }
            }

        })
        TabLayoutMediator(vb.tabs, vb.viewPager) { tab, position ->
            tab.text = HouseType.values()[position].shortName //FIXME: to VM
        }.attach()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getKoin().getViewModel(this, clazz = MainViewModel::class)
    }

    override fun onResume() {
        super.onResume()
       /* val listener = object: View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View?) {
            }
            override fun onViewDetachedFromWindow(v: View?) {}
        }
        vb.mainContainer.addOnAttachStateChangeListener(listener) */
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_characters_list, menu)
        val searchItem = menu.findItem(R.id.search)
        (searchItem.actionView as? SearchView)?.apply {
            setOnQueryTextListener(this@MainFragment)
            queryHint = getString(R.string.characters_list_search_hint)
            viewModel.lastSearchString?.let {
                isIconified = false
                (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
                    true
                )
                setQuery(it, false)
            }
            setOnSearchClickListener {
                (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
                    true
                )
            }
            setOnCloseListener {
                (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
                    false
                )
                false
            }
        }
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.searchStringChanged(newText)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean = true

    class ViewPagerFragmentStateAdapter(activity: FragmentActivity) :
        FragmentStateAdapter(activity) {

        override fun createFragment(position: Int): Fragment =
            CharactersListFragment.newInstance(HouseType.values()[position].shortName)

        //FIXME: to VM
        override fun getItemCount(): Int = HouseType.values().size

    }

    fun reveal(position: Int, x: Float, y: Float) {
        val houseType = HouseType.values()[position]
        vb.revealView.setVisibility(View.VISIBLE)
        val revealViewX: Int = vb.revealView.getWidth()
        val revealViewY: Int = vb.revealView.getHeight()
        val radius = max(revealViewX, revealViewY) * 1.2f
        val reveal = ViewAnimationUtils
            .createCircularReveal(vb.revealView, x.toInt(), y.toInt(), 0f, radius)
        reveal.duration = 2000
        val newBackgroundColor = getColor(houseType.colorPrimaryId)
        reveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animator: Animator) {
                vb.backgroundView.setBackgroundColor(newBackgroundColor)
                vb.revealView.setVisibility(View.INVISIBLE)
            }
        })
        vb.revealView.setBackgroundColor(newBackgroundColor)
        reveal.start()
    }

    private fun getBackgroundColor(position: Int, positionOffset: Float): Int {
        val houseType = HouseType.values()[position]
        val nextHouseType = HouseType.values()[ position +
        when {
            positionOffset == 0f -> 0
            positionOffset > 0 -> 1
            else -> -1
        }]
        val startColor: Int = getColor(houseType.colorPrimaryId)
        val endColor: Int = getColor(nextHouseType.colorPrimaryId)
        val argbEvaluator = ArgbEvaluator()
        return argbEvaluator.evaluate(positionOffset, startColor, endColor) as Int
    }

    private fun getTheme() = requireActivity().theme

    private fun getColor(colorId: Int) = resources.getColor(colorId, getTheme())

}