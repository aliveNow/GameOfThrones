package ru.skillbranch.gameofthrones.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.skillbranch.gameofthrones.data.local.dao.CharacterDao
import ru.skillbranch.gameofthrones.data.local.dao.HouseDao
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.local.typeConverters.StringListTypeConverter

@Database(
    entities = [House::class, Character::class],
    version = 1
)
@TypeConverters(value = [StringListTypeConverter::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun getHouseDao(): HouseDao
    abstract fun getCharacterDao(): CharacterDao

    companion object {
        private const val DATABASE_NAME = "ice_and_fire_db"

        @Volatile
        private var db: AppDatabase? = null

        @Synchronized
        fun getDatabase(appContext: Context): AppDatabase =
            db ?: Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration() //cause it's draft app
                .build()
                .also { db = it }
    }
}