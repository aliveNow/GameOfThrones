package ru.skillbranch.gameofthrones.ui.main

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.koin.getViewModel
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.databinding.FragmentMainBinding
import ru.skillbranch.gameofthrones.ui.characters.list.CharactersListFragment

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).setSupportActionBar(vb.toolbar)
        vb.viewPager.adapter = ViewPagerFragmentStateAdapter(requireActivity())
        TabLayoutMediator(vb.tabs, vb.viewPager) { tab, position ->
            tab.text = AppConfig.NEED_HOUSES[position] //FIXME: to VM
        }.attach()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getKoin().getViewModel(this, clazz = MainViewModel::class)
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
            CharactersListFragment.newInstance(AppConfig.NEED_HOUSES[position])

        //FIXME: to VM
        override fun getItemCount(): Int = AppConfig.NEED_HOUSES.size

    }

}