package ru.skillbranch.gameofthrones.data.local.typeConverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StringListTypeConverter {

    @TypeConverter
    fun stringToStringList(data: String?): List<String> {
        if (data == null) {
            return emptyList()
        }
        return gson.fromJson(data, object : TypeToken<List<String>>() {}.type)
    }

    @TypeConverter
    fun stringListToString(list: List<String>): String {
        return gson.toJson(list)
    }

    companion object {
        private val gson = Gson()
    }

}