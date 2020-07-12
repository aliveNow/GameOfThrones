package ru.skillbranch.gameofthrones.data.local.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "character",
    foreignKeys = [
        ForeignKey(
            entity = House::class,
            parentColumns = ["name"],
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
        parentColumn = "id",
        entityColumn = "houseId",
        projection = ["words"]
    )
    val words: String,
    val born: String,
    val died: String,
    val titles: List<String>,
    val aliases: List<String>,
    val house: String, //rel
    @Relation(
        entity = Character::class,
        parentColumn = "id",
        entityColumn = "father",
        projection = ["id", "name", "houseId"]
    )
    val father: RelativeCharacter?,
    @Relation(
        entity = Character::class,
        parentColumn = "id",
        entityColumn = "father",
        projection = ["id", "name", "houseId"]
    )
    val mother: RelativeCharacter?
)

data class RelativeCharacter(
    val id: String,
    val name: String,
    val house: String //rel
)