package ru.skillbranch.gameofthrones.repositories

import android.app.Application
import android.net.Uri
import androidx.annotation.VisibleForTesting
import retrofit2.HttpException
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.local.AppDatabase
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.remote.NetworkService
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

object RootRepository {

    private val api = NetworkService.getApi()
    private lateinit var db: AppDatabase

    private const val FIRST_PAGE_NUM = 1
    private const val MAX_PAGE_SIZE = 50

    fun initRepository(app: Application) {
        db = AppDatabase.getDatabase(app)
    }

    suspend fun isNeedUpdate(): Boolean {
        return suspendCoroutine { continuation: Continuation<Boolean> ->
            isNeedUpdate {
                continuation.resumeWith(Result.success(it))
            }
        }
    }

    suspend fun loadDataAndInsertToDB() {
        suspendCoroutine { continuation: Continuation<Unit> ->
            dropDb {
                continuation.resumeWith(Result.success(Unit))
            }
        }
        val housesWithCharacters =
            suspendCoroutine { continuation: Continuation<List<Pair<HouseRes, List<CharacterRes>>>> ->
                getNeedHouseWithCharacters(*AppConfig.NEED_HOUSES) {
                    continuation.resumeWith(Result.success(it))
                }
            }
        val houses = mutableListOf<HouseRes>()
        val characters = mutableListOf<CharacterRes>()
        housesWithCharacters.forEach { (house, houseCharacters) ->
            val houseId = house.name
            houses.add(house)
            houseCharacters.forEach { it.houseId = houseId }
            //TODO: remove it
            val firstChar = houseCharacters.first()
            val testCharacters = houseCharacters.map { character ->
                character.copy(father = firstChar.url.takeIf { character.url != it } ?: "")
            }
            //supposing that character can be loyal only to one house...
            characters.addAll(testCharacters)
        }
        suspendCoroutine { continuation: Continuation<Unit> ->
            insertHouses(houses) {
                continuation.resumeWith(Result.success(Unit))
            }
        }
        suspendCoroutine { continuation: Continuation<Unit> ->
            insertCharacters(characters) {
                continuation.resumeWith(Result.success(Unit))
            }
        }
    }

    suspend fun findCharactersByHouseName(name: String): List<CharacterItem> =
        suspendCoroutine { continuation: Continuation<List<CharacterItem>> ->
            findCharactersByHouseName(name) {
                continuation.resumeWith(Result.success(it))
            }
        }

    //TODO: remove
    suspend fun findCharactersByHouseName2(name: String): List<Character> =
        db.getCharacterDao().getCharactersByHouseName2(name)

    suspend fun findCharacterFullById(id: String): CharacterFull =
        suspendCoroutine { continuation: Continuation<CharacterFull> ->
            findCharacterFullById(id) {
                continuation.resumeWith(Result.success(it))
            }
        }

    /**
     * Получение данных о всех домах из сети
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getAllHouses(result: (houses: List<HouseRes>) -> Unit) {
        //TODO implement me
    }

    /**
     * Получение данных о требуемых домах по их полным именам из сети
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouses(vararg houseNames: String, result: (houses: List<HouseRes>) -> Unit) {
        val houses = mutableListOf<HouseRes>()
        for (houseName in houseNames) {
            with(api.getHouseByName(houseName).execute()) {
                if (isSuccessful) {
                    houses.add(
                        body()?.takeIf { it.isNotEmpty() }?.first()
                            ?: throw IllegalArgumentException("$houseName not found")
                    )
                } else {
                    throw HttpException(this)
                }
            }
        }
        result(houses)
    }

    /**
     * Получение данных о требуемых домах по их полным именам и персонажах в каждом из домов из сети
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о доме и персонажей в нем (Дом - Список Персонажей в нем)
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouseWithCharacters(
        vararg houseNames: String,
        result: (houses: List<Pair<HouseRes, List<CharacterRes>>>) -> Unit
    ) {
        getNeedHouses(*houseNames) { houses ->
            val housesWithCharacters = mutableListOf<Pair<HouseRes, List<CharacterRes>>>()
            for (house in houses) {
                val characters = mutableListOf<CharacterRes>()
                for (characterUrl in house.swornMembers) {
                    with(api.getCharacter(characterUrl).execute()) {
                        if (isSuccessful) {
                            body()?.let { characters.add(it) }
                        } else {
                            throw HttpException(this)
                        }
                    }
                    //FIXME: для тестирования
                    if (characters.size == 3) {
                        break
                    }
                }
                housesWithCharacters.add(house to characters)
            }
            result(housesWithCharacters)
        }
    }

    /**
     * Запись данных о домах в DB
     * @param houses - Список персонажей (модель HouseRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertHouses(houses: List<HouseRes>, complete: () -> Unit) {
        db.getHouseDao().insertAll(houses.map {
            House(
                id = it.url.getId(),
                name = it.name,
                region = it.region,
                coatOfArms = it.coatOfArms,
                words = it.words,
                titles = it.titles,
                seats = it.seats,
                currentLord = it.currentLord.getId(),
                heir = it.heir.getId(),
                overlord = it.overlord.getId(),
                founded = it.founded,
                founder = it.founder.getId(),
                diedOut = it.diedOut,
                ancestralWeapons = it.ancestralWeapons
            )
        })
        complete.invoke()
    }

    /**
     * Запись данных о пересонажах в DB
     * @param Characters - Список персонажей (модель CharacterRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertCharacters(Characters: List<CharacterRes>, complete: () -> Unit) {
        db.getCharacterDao().insertAll(
            Characters.map {
                Character(
                    id = it.url.getId(),
                    name = it.name,
                    gender = it.gender,
                    culture = it.culture,
                    born = it.born,
                    died = it.died,
                    titles = it.titles,
                    aliases = it.aliases,
                    father = it.father.getId(),
                    mother = it.mother.getId(),
                    spouse = it.spouse.getId(),
                    houseId = checkNotNull(it.houseId)
                )
            }
        )
        complete.invoke()
    }

    /**
     * При вызове данного метода необходимо выполнить удаление всех записей в db
     * @param complete - колбек о завершении очистки db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun dropDb(complete: () -> Unit) {
        db.getHouseDao().dropTable()
        db.getCharacterDao().dropTable()
        complete.invoke()
    }

    /**
     * Поиск всех персонажей по имени дома, должен вернуть список краткой информации о персонажах
     * дома - смотри модель CharacterItem
     * @param name - краткое имя дома (его первычный ключ)
     * @param result - колбек содержащий в себе список краткой информации о персонажах дома
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharactersByHouseName(name: String, result: (characters: List<CharacterItem>) -> Unit) {
        result(db.getCharacterDao().getCharactersByHouseName(name))
    }

    /**
     * Поиск персонажа по его идентификатору, должен вернуть полную информацию о персонаже
     * и его родственных отношения - смотри модель CharacterFull
     * @param id - идентификатор персонажа
     * @param result - колбек содержащий в себе полную информацию о персонаже
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharacterFullById(id: String, result: (character: CharacterFull) -> Unit) {
        result(db.getCharacterDao().getCharacterFullById(id))
    }

    /**
     * Метод возвращет true если в базе нет ни одной записи, иначе false
     * @param result - колбек результата проверки
     */
    fun isNeedUpdate(result: (isNeed: Boolean) -> Unit) {
        result(db.getCharacterDao().getRowCount() == 0)
    }

    private fun String.getId(): String = Uri.parse(this).lastPathSegment ?: this

}