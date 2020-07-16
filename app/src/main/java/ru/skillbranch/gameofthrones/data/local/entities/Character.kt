package ru.skillbranch.gameofthrones.data.local.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "character",
    indices = [Index("houseId"), Index("father"), Index("mother")],
    foreignKeys = [
        ForeignKey(
            entity = House::class,
            parentColumns = ["shortName"],
            childColumns = ["houseId"],
            onDelete = CASCADE
        )
    ]
)
data class Character(
    @PrimaryKey
    val id: String,
    val name: String,
    val gender: String,
    val culture: String,
    val born: String,
    val died: String,
    val titles: List<String> = listOf(),
    val aliases: List<String> = listOf(),
    val father: String, //rel
    val mother: String, //rel
    val spouse: String,
    val houseId: String //rel
)

data class CharacterItem(
    val id: String,
    @ColumnInfo(name = "houseId") val house: String, //rel
    val name: String,
    val titles: List<String>,
    val aliases: List<String>
)

data class CharacterFull(
    val id: String,
    val name: String,
    @Relation(
        entity = House::class,
        parentColumn = "houseId",
        entityColumn = "shortName",
        projection = ["words"]
    )
    val words: String,
    val born: String,
    val died: String,
    val titles: List<String>,
    val aliases: List<String>,
    @ColumnInfo(name = "houseId") val house: String, //rel
    @ColumnInfo(name = "father") val fatherId: String, //TODO: is there a way to make it simpler?
    @ColumnInfo(name = "mother") val motherId: String,
    @Relation(
        entity = Character::class,
        parentColumn = "father",
        entityColumn = "id",
        projection = ["id", "name", "houseId"]
    )
    val father: RelativeCharacter?,
    @Relation(
        entity = Character::class,
        parentColumn = "mother",
        entityColumn = "id",
        projection = ["id", "name", "houseId"]
    )
    val mother: RelativeCharacter?
)

data class RelativeCharacter(
    val id: String,
    val name: String,
    @ColumnInfo(name = "houseId") val house: String //rel
)