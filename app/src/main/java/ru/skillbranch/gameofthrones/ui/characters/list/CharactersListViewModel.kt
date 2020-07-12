package ru.skillbranch.gameofthrones.ui.characters.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.skillbranch.gameofthrones.repositories.RootRepository

class CharactersListViewModel() : ViewModel() {

    //FIXME: ! to constructor !
    var houseName: String = ""

    val names = MutableLiveData<List<String>>()
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        uiScope.launch {
            val characters = withContext(Dispatchers.IO) {
                RootRepository.findCharactersByHouseName(houseName)
            }
            names.value = characters.map { it.name }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}