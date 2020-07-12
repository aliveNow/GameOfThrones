package ru.skillbranch.gameofthrones.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.skillbranch.gameofthrones.data.local.entities.House

@Dao
interface HouseDao {

    @Insert
    fun insertAll(houses: List<House>)

    @Query("SELECT * FROM house WHERE name = :name limit 1")
    fun getHouseByName(name: String): House

    @Query("DELETE FROM character")
    fun dropTable()

}