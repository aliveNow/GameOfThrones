package ru.skillbranch.gameofthrones.ui.characters.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.koin.getViewModel
import org.koin.core.parameter.parametersOf
import ru.skillbranch.gameofthrones.databinding.FragmentCharacterBinding
import ru.skillbranch.gameofthrones.utils.ui.setDisplayHomeAsUpEnabled

class CharacterFragment : Fragment() {

    private lateinit var viewModel: CharacterViewModel
    private lateinit var vb: FragmentCharacterBinding
    private val args by navArgs<CharacterFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentCharacterBinding.inflate(inflater, container, false)
            .also { vb = it }
            .root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as AppCompatActivity).setSupportActionBar(vb.toolbar)
        setDisplayHomeAsUpEnabled(true)
        viewModel = getKoin().getViewModel(
            this,
            clazz = CharacterViewModel::class
        ) {
            parametersOf(args.shortHouseName, args.characterId)
        }
        vb.imgCoastOfArms.setImageDrawable(requireContext().getDrawable(viewModel.houseType.coatOfArmsId))
        viewModel.character.observe(viewLifecycleOwner, Observer {
            vb.ctwWords.setValueOrGone(it.words)
            vb.ctwBorn.setValueOrGone(it.born)
            vb.ctwTitles.setValueOrGone(it.titles.joinToString(separator = ", "))
            vb.ctwAliases.setValueOrGone(it.aliases.joinToString(separator = ", "))
            vb.bctFather.setValueOrGone(it.father?.name)
            vb.bctMother.setValueOrGone(it.mother?.name)
        })
        viewModel.title.observe(viewLifecycleOwner, Observer {
            vb.toolbar.title = it
        })
    }

    override fun onResume() {
        super.onResume()
        vb.toolbar.title = viewModel.title.value
    }

}