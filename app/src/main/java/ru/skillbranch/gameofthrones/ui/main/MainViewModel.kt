package ru.skillbranch.gameofthrones.ui.main

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.skillbranch.gameofthrones.ui.characters.CharactersInteractor

class MainViewModel(
    private val interactor: CharactersInteractor,
    val houseNames: List<String>
) : ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val showAnimation = MutableLiveData<Boolean>()

    val lastSearchString: String?
        get() = interactor.searchString

    init {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                delay(500)
            }
            showAnimation.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun searchStringChanged(string: String?) {
        interactor.searchString = string
    }

}