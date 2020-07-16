package ru.skillbranch.gameofthrones.ui

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.HouseType
import ru.skillbranch.gameofthrones.ui.characters.CharactersInteractor
import ru.skillbranch.gameofthrones.ui.characters.details.CharacterViewModel
import ru.skillbranch.gameofthrones.ui.characters.list.CharactersListViewModel
import ru.skillbranch.gameofthrones.ui.main.MainViewModel
import ru.skillbranch.gameofthrones.ui.splash.SplashViewModel

val uiModule = module {
    single { CharactersInteractor() }
    viewModel { SplashViewModel(HouseType.values().map { it.coatOfArmsId }.shuffled()) }
    viewModel { MainViewModel(get(), HouseType.values().toList()) }
    viewModel { (houseName: String) -> CharactersListViewModel(get(), houseName) }
    viewModel { (shortHouseName: String, characterId: String) ->
        CharacterViewModel(shortHouseName, characterId)
    }
}