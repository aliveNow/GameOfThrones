package ru.skillbranch.gameofthrones.ui.main

import androidx.lifecycle.ViewModel
import ru.skillbranch.gameofthrones.ui.characters.CharactersInteractor

class MainViewModel(
    private val interactor: CharactersInteractor,
    val houseNames: List<String>
) : ViewModel() {

    val lastSearchString: String?
        get() = interactor.searchString

    fun searchStringChanged(string: String?) {
        interactor.searchString = string
    }

}