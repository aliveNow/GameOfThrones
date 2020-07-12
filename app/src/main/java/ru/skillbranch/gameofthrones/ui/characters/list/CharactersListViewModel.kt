package ru.skillbranch.gameofthrones.ui.characters.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.repositories.RootRepository

class CharactersListViewModel() : ViewModel() {

    //FIXME: ! to constructor !
    var houseName: String = ""
        set(value) {
            field = value
            loadCharacters()
        }

    val itemsList = MutableLiveData<List<CharacterItem>>()
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var sourceItems: List<CharacterItem> = emptyList()
    private var lastSearchString: String? = null
    private var searchJob: Job? = null

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun searchStringChanged(searchString: String?) {
        if (searchString != lastSearchString) {
            lastSearchString = searchString
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

    private fun loadCharacters() {
        uiScope.launch {
            //TODO: remove
            val fullChars = withContext(Dispatchers.IO) {
                RootRepository.findCharactersByHouseName2(houseName)
            }
            sourceItems = withContext(Dispatchers.IO) {
                RootRepository.findCharactersByHouseName(houseName)
            }
            itemsList.value = sourceItems
        }
    }


}