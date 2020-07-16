package ru.skillbranch.gameofthrones.ui.characters.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.koin.getViewModel
import org.koin.core.parameter.parametersOf
import ru.skillbranch.gameofthrones.HouseType
import ru.skillbranch.gameofthrones.data.local.entities.RelativeCharacter
import ru.skillbranch.gameofthrones.databinding.FragmentCharacterBinding
import ru.skillbranch.gameofthrones.utils.ui.observeEvent
import ru.skillbranch.gameofthrones.utils.ui.setDisplayHomeAsUpEnabled
import ru.skillbranch.gameofthrones.utils.ui.view.ButtonCharacterTraitView

class CharacterFragment : Fragment() {

    private lateinit var viewModel: CharacterViewModel
    private lateinit var vb: FragmentCharacterBinding
    private val args by navArgs<CharacterFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        HouseType.findByShortName(args.shortHouseName)?.let {
            requireContext().setTheme(it.themeId)
        }
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
            vb.ctwTitles.setValueOrGone(it.titles.joinToString(separator = "\n "))
            vb.ctwAliases.setValueOrGone(it.aliases.joinToString(separator = "\n "))
            initButton(vb.bctFather, it.father)
            initButton(vb.bctMother, it.mother)
        })
        viewModel.title.observe(viewLifecycleOwner, Observer {
            vb.toolbar.title = it
        })
        viewModel.showMessage.observeEvent(this) {
            it?.let {
                Snackbar.make(vb.coordinator, it, Snackbar.LENGTH_INDEFINITE).show()
            }
        }
        viewModel.finish.observeEvent(this) {
            requireActivity().onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        vb.toolbar.title = viewModel.title.value
    }

    private fun initButton(
        buttonTrait: ButtonCharacterTraitView,
        relative: RelativeCharacter?
    ) {
        buttonTrait.apply {
            setValueOrGone(relative?.name)
            relative?.let {
                onButtonClicked = { _ ->
                    findNavController().navigate(
                        CharacterFragmentDirections.actionFromCharacterDetailFragmentToDetailFragment(
                            shortHouseName = args.shortHouseName,
                            characterId = it.id
                        )
                    )
                }
            }
        }
    }

}