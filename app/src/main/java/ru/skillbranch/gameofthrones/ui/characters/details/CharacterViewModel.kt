package ru.skillbranch.gameofthrones.ui.characters.details

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import ru.skillbranch.gameofthrones.HouseType
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.repositories.RootRepository
import ru.skillbranch.gameofthrones.utils.ui.base.BaseViewModel
import ru.skillbranch.gameofthrones.utils.ui.data.Event
import ru.skillbranch.gameofthrones.utils.ui.data.EventLiveData
import java.lang.Exception

class CharacterViewModel(
    shortHouseName: String,
    private val characterId: String
) : BaseViewModel() {

    val houseType = checkNotNull(HouseType.findByShortName(shortHouseName))
    val title = MutableLiveData<String>()
    val character = MutableLiveData<CharacterFull>()
    val showMessage = EventLiveData<String>()
    val finish = EventLiveData<Unit>()

    init {
        loadCharacter()
    }

    private fun loadCharacter() {
        uiScope.launch {
            try {
                character.value = withContext(Dispatchers.IO) {
                    RootRepository.findCharacterFullById(characterId)
                }.also {
                    title.value = it.name
                    if (it.died.isNotEmpty()) {
                        showMessage.value = Event("Died ${it.died}")
                    }
                }
            } catch (e: Exception) {
                finish.value = Event(Unit)
            }
        }
    }
}