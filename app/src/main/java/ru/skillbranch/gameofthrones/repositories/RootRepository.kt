package ru.skillbranch.gameofthrones.repositories

import android.app.Application
import android.net.Uri
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.*
import retrofit2.Call
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

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

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
        @Suppress("UNUSED_VARIABLE")
        val isDropped = suspendCoroutine { continuation: Continuation<Unit> ->
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
        val characters = mutableSetOf<CharacterRes>()
        housesWithCharacters.forEach { (house, houseCharacters) ->
            val houseId = AppConfig.HOUSE_NAMES_MAP[house.name]
            houses.add(house)
            houseCharacters.forEach { it.houseId = houseId }
            characters.addAll(houseCharacters)
        }
        suspendCoroutine { continuation: Continuation<Unit> ->
            insertHouses(houses) {
                continuation.resumeWith(Result.success(Unit))
            }
        }
        suspendCoroutine { continuation: Continuation<Unit> ->
            insertCharacters(characters.toList()) {
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
    @Suppress("unused")
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getAllHouses(result: (houses: List<HouseRes>) -> Unit) {
        uiScope.launch {
            val allHouses = withContext(Dispatchers.IO) {
                val houses = mutableListOf<HouseRes>()
                var pageNum = FIRST_PAGE_NUM
                do {
                    val newHouses = api.getAllHouses(pageNum, MAX_PAGE_SIZE).executeOrException()
                    houses.addAll(newHouses)
                    pageNum++
                } while (newHouses.isNotEmpty())
                houses
            }
            result(allHouses)
        }
    }

    /**
     * Получение данных о требуемых домах по их полным именам из сети
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouses(vararg houseNames: String, result: (houses: List<HouseRes>) -> Unit) {
        uiScope.launch {
            val neededHouses = withContext(Dispatchers.IO) {
                val houses = mutableListOf<HouseRes>()
                for (houseName in houseNames) {
                    houses.add(
                        api.getHouseByName(houseName).executeOrException()
                            .takeIf { it.isNotEmpty() }?.first()
                            ?: throw IllegalArgumentException("$houseName not found")
                    )
                }
                houses
            }
            result(neededHouses)
        }
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
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val allCharactersMap = getAllCharactersMap()
                getNeedHouses(*houseNames) { houses ->
                    val housesWithCharacters = mutableListOf<Pair<HouseRes, List<CharacterRes>>>()
                    for (house in houses) {
                        val characters = mutableListOf<CharacterRes>()
                        for (characterUrl in house.swornMembers) {
                            allCharactersMap[characterUrl.getId()]?.let {
                                characters.add(it)
                            }
                        }
                        housesWithCharacters.add(house to characters)
                    }
                    result(housesWithCharacters)
                }
            }
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
        uiScope.launch {
            @Suppress("UNUSED_VARIABLE")
            val result = withContext(Dispatchers.IO) {
                db.getHouseDao().insertAll(houses.map {
                    House(
                        id = it.url.getId(),
                        name = it.name,
                        shortName = checkNotNull(AppConfig.HOUSE_NAMES_MAP[it.name]),
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
            }
            complete.invoke()
        }
    }

    /**
     * Запись данных о пересонажах в DB
     * @param Characters - Список персонажей (модель CharacterRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertCharacters(Characters: List<CharacterRes>, complete: () -> Unit) {
        uiScope.launch {
            @Suppress("UNUSED_VARIABLE")
            val result = withContext(Dispatchers.IO) {
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
            }
            complete.invoke()
        }
    }

    /**
     * При вызове данного метода необходимо выполнить удаление всех записей в db
     * @param complete - колбек о завершении очистки db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun dropDb(complete: () -> Unit) {
        uiScope.launch {
            @Suppress("UNUSED_VARIABLE")
            val result = withContext(Dispatchers.IO) {
                db.getHouseDao().dropTable()
                db.getCharacterDao().dropTable()
            }
            complete.invoke()
        }
    }

    /**
     * Поиск всех персонажей по имени дома, должен вернуть список краткой информации о персонажах
     * дома - смотри модель CharacterItem
     * @param name - краткое имя дома (его первычный ключ)
     * @param result - колбек содержащий в себе список краткой информации о персонажах дома
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharactersByHouseName(name: String, result: (characters: List<CharacterItem>) -> Unit) {
        uiScope.launch {
            val characters = withContext(Dispatchers.IO) {
                db.getCharacterDao().getCharactersByHouseName(name)
            }
            result(characters)
        }
    }

    /**
     * Поиск персонажа по его идентификатору, должен вернуть полную информацию о персонаже
     * и его родственных отношения - смотри модель CharacterFull
     * @param id - идентификатор персонажа
     * @param result - колбек содержащий в себе полную информацию о персонаже
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharacterFullById(id: String, result: (character: CharacterFull) -> Unit) {
        uiScope.launch {
            val character = withContext(Dispatchers.IO) {
                db.getCharacterDao().getCharacterFullById(id)
            }
            result(character)
        }
    }

    /**
     * Метод возвращет true если в базе нет ни одной записи, иначе false
     * @param result - колбек результата проверки
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun isNeedUpdate(result: (isNeed: Boolean) -> Unit) {
        uiScope.launch {
            val isNeedUpdate = withContext(Dispatchers.IO) {
                db.getHouseDao().getRowCount() == 0 && db.getCharacterDao().getRowCount() == 0
            }
            result(isNeedUpdate)
        }
    }

    private fun getAllCharactersMap(): Map<String, CharacterRes> {
        val allCharacters = mutableMapOf<String, CharacterRes>()
        var pageNum = FIRST_PAGE_NUM
        do {
            val newCharacters = api.getAllCharacters(pageNum, MAX_PAGE_SIZE).executeOrException()
            newCharacters.forEach { allCharacters[it.url.getId()] = it }
            pageNum++
        } while (newCharacters.isNotEmpty())
        return allCharacters
    }

    private fun <T : Any> Call<T>.executeOrException(): T =
        with(execute()) {
            if (isSuccessful) {
                checkNotNull(body())
            } else {
                throw HttpException(this)
            }
        }

    private fun String.getId(): String = Uri.parse(this).lastPathSegment ?: this

}