package ru.skillbranch.gameofthrones.ui.characters.details

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import ru.skillbranch.gameofthrones.databinding.FragmentCharacterBinding

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
        viewModel = ViewModelProviders.of(this).get(CharacterViewModel::class.java)
        viewModel.characterId = args.characterId
        viewModel.otherCharacter.observe(viewLifecycleOwner, Observer {
            //vb.tvOtherCharacter.text = it.name
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

}