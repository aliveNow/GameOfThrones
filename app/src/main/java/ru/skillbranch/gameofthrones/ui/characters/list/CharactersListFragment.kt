package ru.skillbranch.gameofthrones.ui.characters.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.koin.getViewModel
import org.koin.core.parameter.parametersOf
import ru.skillbranch.gameofthrones.HouseType
import ru.skillbranch.gameofthrones.databinding.FragmentCharactersListBinding
import ru.skillbranch.gameofthrones.ui.main.MainFragmentDirections
import ru.skillbranch.gameofthrones.utils.ui.observeState
import ru.skillbranch.gameofthrones.utils.ui.scrollToPositionAnimated


class CharactersListFragment : Fragment() {

    private lateinit var viewModel: CharactersListViewModel
    private lateinit var vb: FragmentCharactersListBinding
    private val adapter: CharactersListAdapter
        get() = vb.rvList.adapter as CharactersListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentCharactersListBinding.inflate(inflater, container, false)
            .also { vb = it }
            .root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val houseName = checkNotNull(arguments?.getString(ARG_CHARACTERS_LIST_HOUSE_NAME))
        viewModel = getKoin().getViewModel(this, CharactersListViewModel::class) {
            parametersOf(houseName)
        }
        initRecycleView(viewModel.houseType)
        viewModel.itemsList.observeState(this) {
            adapter.items = it
            adapter.notifyDataSetChanged()
            if (it.isNotEmpty()) {
                vb.rvList.scrollToPositionAnimated(0, true)
            }
        }
    }

    private fun initRecycleView(houseType: HouseType) {
        vb.rvList.adapter = CharactersListAdapter(houseType).apply {
            onItemClickListener = {
                findNavController().navigate(
                    //FIXME: from there it's bad -_-
                    //UPD: but time is too short >_< In two hours a deadline is approaching, brrr
                    MainFragmentDirections.actionFromCharactersListFragmentToDetailFragment(
                        shortHouseName = viewModel.houseType.shortName,
                        characterId = it.id
                    )
                )
            }
        }
        //FIXME: дизайн у сепаратора другой, но уже потом...
        vb.rvList.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
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