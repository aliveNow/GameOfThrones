package ru.skillbranch.gameofthrones.ui.main

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.koin.getViewModel
import ru.skillbranch.gameofthrones.HouseType
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.databinding.FragmentMainBinding
import ru.skillbranch.gameofthrones.ui.characters.list.CharactersListFragment
import ru.skillbranch.gameofthrones.utils.ui.hideSoftInput
import ru.skillbranch.gameofthrones.utils.ui.setDisplayHomeAsUpEnabled
import ru.skillbranch.gameofthrones.utils.views.ColoredTabsAppBar

class MainFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var vb: FragmentMainBinding

    val toolbar: Toolbar
        get() = vb.tabsAppBar.toolbar

    val tabLayout: TabLayout
        get() = vb.tabsAppBar.tabLayout

    private var menuSearchView: SearchView? = null

    private val onSearchBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            menuSearchView?.isIconified = true
        }
    }

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        vb.viewPager.adapter = ViewPagerFragmentStateAdapter(requireActivity())
        vb.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_SETTLING) {
                    /*val position = vb.viewPager.currentItem
                    val currentThemeId = AppConfig.appThemeId
                    val newThemeId = if (position == 0) R.style.AppThemeOverlay_Lannister else R.style.AppThemeOverlay_Baratheon
                    if (currentThemeId != newThemeId) {
                        AppConfig.appThemeId = newThemeId
                        toolbar.context.setTheme(newThemeId)
                    }*/
                }
            }

            override fun onPageSelected(position: Int) {

            }
        })
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

        })
        vb.tabsAppBar.colorIds = HouseType.values().map { it.colorPrimaryId }
        ColoredTabsAppBar.ColoredTabsAppBarMediator(
            vb.tabsAppBar,
            vb.viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text = HouseType.values()[position].shortName
            }).attach()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setDisplayHomeAsUpEnabled(false)
        viewModel = getKoin().getViewModel(this, clazz = MainViewModel::class)
        requireActivity().onBackPressedDispatcher.addCallback(onSearchBackPressedCallback)
    }

    override fun onDestroy() {
        menuSearchView = null
        super.onDestroy()
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
            menuSearchView = this
            setIconifiedByDefault(true)
            setOnQueryTextListener(this@MainFragment)
            queryHint = getString(R.string.characters_list_search_hint)
            viewModel.lastSearchString?.let {
                isIconified = false
                onSearchViewIsVisible(true)
                setQuery(it, false)
            }
            setOnSearchClickListener {
                onSearchViewIsVisible(true)
            }
            setOnCloseListener {
                onSearchViewIsVisible(false)
                false
            }
        }
    }

    private fun onSearchViewIsVisible(isVisible: Boolean) {
        setDisplayHomeAsUpEnabled(isVisible)
        onSearchBackPressedCallback.isEnabled = isVisible
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

}