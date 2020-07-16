package ru.skillbranch.gameofthrones.data.local.dao

import androidx.room.*
import ru.skillbranch.gameofthrones.data.local.entities.*

@Dao
interface CharacterDao {

    @Insert
    fun insertAll(characters: List<Character>)

    @Query("SELECT id, houseId, name, titles, aliases FROM character WHERE houseId = :name")
    fun getCharactersByHouseName(name: String): List<CharacterItem>

    @Transaction
    @Query("SELECT * FROM character WHERE id = :id")
    fun getCharacterFullById(id: String): CharacterFull

    @Query("SELECT COUNT(id) FROM character")
    fun getRowCount(): Int

    @Query("DELETE FROM character")
    fun dropTable()

}