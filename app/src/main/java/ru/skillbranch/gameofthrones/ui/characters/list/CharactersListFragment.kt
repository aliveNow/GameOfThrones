package ru.skillbranch.gameofthrones.ui.characters.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.databinding.FragmentCharactersListBinding
import ru.skillbranch.gameofthrones.ui.main.MainFragmentDirections

class CharactersListFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var viewModel: CharactersListViewModel
    private lateinit var vb: FragmentCharactersListBinding
    private val adapter: CharactersListAdapter
        get() = vb.rvList.adapter as CharactersListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentCharactersListBinding.inflate(inflater, container, false)
            .also { vb = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb.rvList.adapter = CharactersListAdapter().apply {
            onItemClickListener = {
                findNavController().navigate(
                    //FIXME: from there it's bad -_-
                    MainFragmentDirections.actionFromCharactersListFragmentToDetailFragment(it.id)
                )
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CharactersListViewModel::class.java)
        viewModel.houseName = checkNotNull(arguments?.getString(ARG_CHARACTERS_LIST_HOUSE_NAME))
        viewModel.itemsList.observe(viewLifecycleOwner, Observer {
            adapter.items = it
            adapter.notifyDataSetChanged()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.menu_characters_list, menu)
        val searchItem = menu.findItem(R.id.search)
        (searchItem.actionView as? SearchView)?.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.searchStringChanged(newText)
        return true
    }

    companion object {
        private const val ARG_CHARACTERS_LIST_HOUSE_NAME = "ARG_CHARACTERS_LIST_HOUSE_NAME"

        fun newInstance(houseName: String) = CharactersListFragment().apply {
            arguments = bundleOf(
                ARG_CHARACTERS_LIST_HOUSE_NAME to houseName
            )
        }
    }

}