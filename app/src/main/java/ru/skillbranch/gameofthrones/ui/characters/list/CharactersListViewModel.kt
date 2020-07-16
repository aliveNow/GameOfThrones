package ru.skillbranch.gameofthrones.ui.characters.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.*
import ru.skillbranch.gameofthrones.HouseType
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.repositories.RootRepository
import ru.skillbranch.gameofthrones.ui.characters.CharactersInteractor
import ru.skillbranch.gameofthrones.utils.ui.base.BaseViewModel

class CharactersListViewModel(
    private val interactor: CharactersInteractor,
    shortHouseName: String
) : BaseViewModel() {

    val houseType = checkNotNull(HouseType.findByShortName(shortHouseName))
    val itemsList = MutableLiveData<List<CharacterItem>>()

    private var sourceItems: List<CharacterItem> = emptyList()
    private var lastSearchString: String? = null
    private var searchJob: Job? = null
    private val searchStringObserver = Observer<String?> { onSearchStringChanged(it) }

    init {
        interactor.searchStringLiveData.observeForever(searchStringObserver)
        lastSearchString = interactor.searchString
        loadCharacters()
    }

    override fun onCleared() {
        interactor.searchStringLiveData.removeObserver(searchStringObserver)
        super.onCleared()
    }

    private fun onSearchStringChanged(searchString: String?) {
        if (searchString != lastSearchString) {
            lastSearchString = searchString
            if (sourceItems.isNotEmpty()) {
                filterCharacters(searchString)
            }
        }
    }

    private fun loadCharacters() {
        uiScope.launch {
            sourceItems = withContext(Dispatchers.IO) {
                RootRepository.findCharactersByHouseName(houseType.shortName)
            }
            filterCharacters(lastSearchString)
        }
    }

    private fun filterCharacters(searchString: String?) {
        searchJob?.cancel()
        searchJob = uiScope.launch {
            delay(300)
            itemsList.value = if (searchString.isNullOrEmpty()) {
                sourceItems
            } else {
                withContext(Dispatchers.IO) {
                    sourceItems.filter {
                        it.name.contains(searchString, ignoreCase = true)
                    }
                }
            }
        }
    }

}