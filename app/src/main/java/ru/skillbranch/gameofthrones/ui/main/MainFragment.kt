package ru.skillbranch.gameofthrones.ui.main

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.zys.brokenview.BrokenCallback
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.koin.getViewModel
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.databinding.FragmentMainBinding
import ru.skillbranch.gameofthrones.ui.characters.list.CharactersListFragment
import ru.skillbranch.gameofthrones.utils.ui.addOneTimeOnGlobalLayoutListener
import ru.skillbranch.gameofthrones.utils.ui.observeState
import ru.skillbranch.gameofthrones.utils.ui.setDisplayHomeAsUpEnabled
import ru.skillbranch.gameofthrones.utils.ui.view.ColoredTabsAppBar
import ru.skillbranch.gameofthrones.utils.ui.view.ColoredTabsAppBar.ColoredTab

class MainFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var vb: FragmentMainBinding
    private val args by navArgs<MainFragmentArgs>()

    private val toolbar: Toolbar
        get() = vb.tabsAppBar.toolbar

    private var menuSearchView: SearchView? = null

    private val onSearchBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            viewModel.searchCloseClicked()
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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getKoin().getViewModel(this, clazz = MainViewModel::class)
        initSplashAnimation()

        vb.viewPager.adapter =
            ViewPagerFragmentStateAdapter(requireActivity(), viewModel.houseNames)
        vb.tabsAppBar.apply {
            coloredTabs = viewModel.houseTypes.map { ColoredTab(it.shortName, it.colorPrimaryId) }
            ColoredTabsAppBar.ColoredTabsAppBarMediator(this, vb.viewPager).attach()
        }

        requireActivity().onBackPressedDispatcher.addCallback(onSearchBackPressedCallback)
        viewModel.isSearchVisible.observeState(this) {
            updateSearchViewVisibility(it)
        }
    }

    override fun onDestroyView() {
        onSearchBackPressedCallback.isEnabled = false
        super.onDestroyView()
    }

    override fun onDestroy() {
        menuSearchView = null
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_characters_list, menu)
        menuSearchView = menu.findItem(R.id.search).actionView as? SearchView
        menuSearchView?.apply {
            setIconifiedByDefault(true)
            setOnQueryTextListener(this@MainFragment)
            queryHint = getString(R.string.characters_list_search_hint)
            updateSearchViewVisibility(viewModel.isSearchVisible.value ?: false)
            setOnSearchClickListener {
                viewModel.searchShowClicked()
            }
            setOnCloseListener {
                viewModel.searchCloseClicked()
                false
            }
        }
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.searchStringChanged(newText)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    private fun initSplashAnimation() {
        vb.imgSplash.setImageDrawable(requireContext().getDrawable(args.splashImageId))
        vb.brokenView.setCallback(object : BrokenCallback() {
            override fun onFallingEnd(v: View?) {
                resetSplashAnimation()
            }
        })
        if (viewModel.wasSplashAnimationShown) {
            resetSplashAnimation()
        } else {
            vb.imgSplash.addOneTimeOnGlobalLayoutListener {
                vb.brokenView.createAnimator(vb.imgSplash).start()
            }
        }
    }

    private fun updateSearchViewVisibility(isVisible: Boolean) {
        menuSearchView?.apply {
            setDisplayHomeAsUpEnabled(isVisible)
            onSearchBackPressedCallback.isEnabled = isVisible
            val newQuery = viewModel.lastSearchString?.takeIf { it.isNotEmpty() }
            if (newQuery != query.takeIf { it.isNotEmpty() }) {
                setQuery(newQuery, false)
            }
            if (isIconified == isVisible) {
                isIconified = !isVisible
            }
        }
    }

    private fun resetSplashAnimation() {
        vb.brokenView.reset()
        vb.imgSplash.visibility = View.GONE
        vb.brokenView.visibility = View.GONE
        viewModel.wasSplashAnimationShown = true
    }

    private class ViewPagerFragmentStateAdapter(
        activity: FragmentActivity,
        private val tabNames: List<String>
    ) :
        FragmentStateAdapter(activity) {

        override fun createFragment(position: Int): Fragment =
            CharactersListFragment.newInstance(tabNames[position])

        override fun getItemCount(): Int = tabNames.size

    }

}