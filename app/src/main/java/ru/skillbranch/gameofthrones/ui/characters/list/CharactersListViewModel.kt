package ru.skillbranch.gameofthrones.ui.characters.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.skillbranch.gameofthrones.HouseType
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.repositories.RootRepository
import ru.skillbranch.gameofthrones.ui.characters.CharactersInteractor

class CharactersListViewModel(
    private val interactor: CharactersInteractor,
    shortHouseName: String
) : ViewModel() {

    val houseType = checkNotNull(HouseType.findByShortName(shortHouseName))
    val itemsList = MutableLiveData<List<CharacterItem>>()
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
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
        viewModelJob.cancel()
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
            //TODO: remove
            val fullChars = withContext(Dispatchers.IO) {
                RootRepository.findCharactersByHouseName2(houseType.fullName)
            }
            sourceItems = withContext(Dispatchers.IO) {
                RootRepository.findCharactersByHouseName(houseType.fullName)
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