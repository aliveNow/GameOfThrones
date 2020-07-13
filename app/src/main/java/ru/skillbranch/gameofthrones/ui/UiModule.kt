package ru.skillbranch.gameofthrones.ui

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.ui.characters.CharactersInteractor
import ru.skillbranch.gameofthrones.ui.characters.details.CharacterViewModel
import ru.skillbranch.gameofthrones.ui.characters.list.CharactersListViewModel
import ru.skillbranch.gameofthrones.ui.main.MainViewModel

val uiModule = module {
    single { CharactersInteractor() }
    viewModel { MainViewModel(get(), AppConfig.NEED_HOUSES.toList()) }
    viewModel { (houseName: String) -> CharactersListViewModel(get(), houseName) }
    viewModel { (shortHouseName: String, characterId: String) ->
        CharacterViewModel(shortHouseName, characterId)
    }
}