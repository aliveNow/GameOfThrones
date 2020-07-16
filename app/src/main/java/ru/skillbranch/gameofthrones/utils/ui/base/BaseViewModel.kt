package ru.skillbranch.gameofthrones.utils.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

open class BaseViewModel : ViewModel() {

    @Suppress("MemberVisibilityCanBePrivate")
    protected val viewModelJob = SupervisorJob()
    protected val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }

}