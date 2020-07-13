package ru.skillbranch.gameofthrones.ui.characters.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.skillbranch.gameofthrones.HouseType
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.repositories.RootRepository

class CharacterViewModel(
    shortHouseName: String,
    private val characterId: String
) : ViewModel() {

    val houseType = checkNotNull(HouseType.findByShortName(shortHouseName))
    val title = MutableLiveData<String>()
    val character = MutableLiveData<CharacterFull>()

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        loadCharacter()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun loadCharacter() {
        uiScope.launch {
            character.value = withContext(Dispatchers.IO) {
                RootRepository.findCharacterFullById(characterId)
            }
            title.value = character.value?.name
        }
    }
}