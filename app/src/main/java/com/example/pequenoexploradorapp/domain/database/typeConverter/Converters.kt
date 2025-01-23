package com.example.pequenoexploradorapp.domain.database.typeConverter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.util.Date

@ProvidedTypeConverter
class Converters {
  @TypeConverter
  fun fromTimestamp(value: Long?): Date? {
    return value?.let { Date(it) }
  }

  @TypeConverter
  fun dateToTimestamp(date: Date?): Long? {
    return date?.time?.toLong()
  }

  @TypeConverter
  fun fromListStringToString(stringList: List<String>): String = stringList.toString()

  @TypeConverter
  fun toListStringFromString(stringList: String): List<String> {
    val result = ArrayList<String>()
    val split =stringList.replace("[","").replace("]","").replace(" ","").split(",")
    for (n in split) {
      try {
        result.add(n)
      } catch (e: Exception) {
        println(e)
      }
    }
    return result
  }
}