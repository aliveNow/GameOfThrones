package ru.skillbranch.gameofthrones.data.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes

interface IceAndFireApi {

    //TODO: RootRepo
    @GET("/api/houses")
    fun getAllHouses(
        @Query("page") pageNum: Int,
        @Query("pageSize") pageSize: Int
    ): Call<List<HouseRes>>

    @GET("/api/houses")
    fun getHouseByName(@Query("name") name: String): Call<List<HouseRes>>

    @GET
    fun getCharacter(@Url url: String): Call<CharacterRes>

}