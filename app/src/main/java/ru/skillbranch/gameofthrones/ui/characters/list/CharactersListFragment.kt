package ru.skillbranch.gameofthrones.ui.characters.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ru.skillbranch.gameofthrones.R

class CharactersListFragment : Fragment() {

    private lateinit var viewModel: CharactersListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.characters_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CharactersListViewModel::class.java)
        viewModel.houseName = checkNotNull(arguments?.getString(ARG_CHARACTERS_LIST_HOUSE_NAME))
        viewModel.names.observe(viewLifecycleOwner, Observer {
            view?.findViewById<TextView>(R.id.tvTest)?.text = it.joinToString(separator = "\n")
        })
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