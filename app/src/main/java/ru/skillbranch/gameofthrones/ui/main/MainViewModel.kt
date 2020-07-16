package ru.skillbranch.gameofthrones.ui.main

import androidx.lifecycle.MutableLiveData
import ru.skillbranch.gameofthrones.HouseType
import ru.skillbranch.gameofthrones.ui.characters.CharactersInteractor
import ru.skillbranch.gameofthrones.utils.ui.base.BaseViewModel

class MainViewModel(
    private val interactor: CharactersInteractor,
    val houseTypes: List<HouseType>
) : BaseViewModel() {

    var wasSplashAnimationShown: Boolean = false
    val houseNames: List<String> = houseTypes.map { it.shortName }

    val isSearchVisible = MutableLiveData<Boolean>()
    val lastSearchString: String?
        get() = interactor.searchString

    fun searchShowClicked() {
        isSearchVisible.value = true
    }

    fun searchCloseClicked() {
        searchStringChanged(null)
        isSearchVisible.value = false
    }

    fun searchStringChanged(string: String?) {
        interactor.searchString = string
    }

}