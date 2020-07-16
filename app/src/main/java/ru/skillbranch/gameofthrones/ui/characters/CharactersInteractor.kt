package ru.skillbranch.gameofthrones.ui.characters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class CharactersInteractor {

    val searchStringLiveData: LiveData<String>
        get() = _searchStringLiveData

    var searchString: String? = null
        set(value) {
            if (value != field) {
                field = if (value.isNullOrEmpty()) null else value
                _searchStringLiveData.value = field
            }
        }

    private val _searchStringLiveData = MutableLiveData<String>()
}