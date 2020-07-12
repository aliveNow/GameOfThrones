package ru.skillbranch.gameofthrones.ui.characters

import ru.skillbranch.gameofthrones.AppConfig

class CharactersInteractor {

    var searchString: String? = null
        set(value) {
            if (value != field) {
                field = if (value.isNullOrEmpty()) null else value
                searchStringListeners.forEach { it.onSearchStringChanged(value) }
            }
        }

    private val searchStringListeners = mutableListOf<OnSearchStringChangeListener>()

    fun addOnSearchStringChangeListener(listener: OnSearchStringChangeListener) {
        removeOnSearchStringChangeListener(listener)
        searchStringListeners.add(listener)
    }

    fun removeOnSearchStringChangeListener(listener: OnSearchStringChangeListener) {
        searchStringListeners.remove(listener)
    }

    interface OnSearchStringChangeListener {
        fun onSearchStringChanged(searchString: String?)
    }

}