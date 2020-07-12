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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun loadCharacters() {
        uiScope.launch {
            //TODO: remove
            val fullChars = withContext(Dispatchers.IO) {
                RootRepository.findCharactersByHouseName2(houseName)
            }
            itemsList.value = withContext(Dispatchers.IO) {
                RootRepository.findCharactersByHouseName(houseName)
            }
        }
    }


}