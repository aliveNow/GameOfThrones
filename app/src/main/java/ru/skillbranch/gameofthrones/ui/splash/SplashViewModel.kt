package ru.skillbranch.gameofthrones.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.skillbranch.gameofthrones.repositories.RootRepository

class SplashViewModel : ViewModel() {

    val showAnimation = MutableLiveData<Boolean>()
    val navigateToMain = MutableLiveData<Boolean>() //FIXME: eventLiveData
    var needToNavigateToMain: Boolean = false
        private set
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        loadData()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun loadData() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                delay(500)
            }
            showAnimation.value = true
            withContext(Dispatchers.IO) {
                delay(2000) //FIXME: !!!
                with(RootRepository) {
                    val isNeedUpdate = isNeedUpdate()
                    if (isNeedUpdate) {
                        loadDataAndInsertToDB()
                    }
                }
            }
            navigateToMain.value = true
            needToNavigateToMain = true
        }
    }
}