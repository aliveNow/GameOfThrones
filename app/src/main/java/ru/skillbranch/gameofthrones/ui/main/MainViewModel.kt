package ru.skillbranch.gameofthrones.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.repositories.RootRepository

class MainViewModel : ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        loadData()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun loadData() {
        uiScope.launch {
            val characters = withContext(Dispatchers.IO) {
                with(RootRepository) {
                    val isNeedUpdate = isNeedUpdate()
                    if (isNeedUpdate) {
                        loadDataAndInsertToDB()
                    }
                    findCharactersByHouseName(AppConfig.NEED_HOUSES[0])
                }
            }
            val names = characters.map { it.name }
        }
    }
}