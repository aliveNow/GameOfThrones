package ru.skillbranch.gameofthrones.ui.characters.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.repositories.RootRepository

class CharactersListViewModel() : ViewModel() {

    //FIXME: ! to constructor !
    var houseName: String = ""

    val itemsList = MutableLiveData<List<CharacterItem>>()
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        uiScope.launch {
            itemsList.value = withContext(Dispatchers.IO) {
                RootRepository.findCharactersByHouseName(houseName)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}