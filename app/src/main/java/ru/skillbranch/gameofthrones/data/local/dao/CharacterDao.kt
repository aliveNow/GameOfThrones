package ru.skillbranch.gameofthrones.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem

@Dao
interface CharacterDao {

    @Insert
    fun insertAll(characters: List<Character>)

    @Query("SELECT id, houseId, name, titles, aliases FROM character WHERE houseId = :name")
    fun getCharactersByHouseName(name: String): List<CharacterItem>

    @Query("SELECT COUNT(id) FROM character")
    fun getRowCount(): Int

    @Query("DELETE FROM character")
    fun dropTable()

}