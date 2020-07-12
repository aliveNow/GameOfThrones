package ru.skillbranch.gameofthrones.ui.characters.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.RelativeCharacter
import ru.skillbranch.gameofthrones.repositories.RootRepository

class CharacterViewModel : ViewModel() {

    //FIXME:
    var characterId: String = ""
        set(value) {
            field = value
            loadCharacter()
        }
    val otherCharacter = MutableLiveData<RelativeCharacter>()

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var character: CharacterFull? = null

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun loadCharacter() {
        uiScope.launch {
            character = withContext(Dispatchers.IO) {
                RootRepository.findCharacterFullById(characterId)
            }
            otherCharacter.value = character?.father ?: character?.mother ?: RelativeCharacter(
                id = "",
                name = "Test",
                house = "x"
            )
        }
    }
}