package ru.skillbranch.gameofthrones.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "house"
)
data class House(
    val id: String,
    val name: String,
    @PrimaryKey
    val shortName: String,
    val region: String,
    val coatOfArms: String,
    val words: String,
    val titles: List<String>,
    val seats: List<String>,
    val currentLord: String, //rel
    val heir: String, //rel
    val overlord: String,
    val founded: String,
    val founder: String, //rel
    val diedOut: String,
    val ancestralWeapons: List<String>
)