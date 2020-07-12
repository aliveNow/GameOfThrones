package ru.skillbranch.gameofthrones

import android.app.Application
import ru.skillbranch.gameofthrones.repositories.RootRepository

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //FIXME: make it more elegant?

        RootRepository.initRepository(this)
    }

}