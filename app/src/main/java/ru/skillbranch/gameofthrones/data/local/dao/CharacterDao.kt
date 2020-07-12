package ru.skillbranch.gameofthrones.data.local.dao

import androidx.room.*
import ru.skillbranch.gameofthrones.data.local.entities.*

@Dao
interface CharacterDao {

    @Insert
    fun insertAll(characters: List<Character>)

    //TODO: remove
    @Query("SELECT * FROM character WHERE houseId = :name")
    fun getCharactersByHouseName2(name: String): List<Character>

    @Query("SELECT id, houseId, name, titles, aliases FROM character WHERE houseId = :name")
    fun getCharactersByHouseName(name: String): List<CharacterItem>

    //@Query("SELECT id, name, born, died, titles, aliases, houseId as house, father, mother FROM character WHERE id = :id")
    @Transaction
    @Query("SELECT * FROM character WHERE id = :id")
    fun getCharacterFullById(id: String): CharacterFull

    @Query("SELECT COUNT(id) FROM character")
    fun getRowCount(): Int

    @Query("DELETE FROM character")
    fun dropTable()

}