package com.mrspd.newsapp.db

import androidx.room.TypeConverter


class Converters {


    //Returning String from Source class
    @TypeConverter
    fun getSource(source: com.mrspd.newsapp.models.Source): String{
        return source.name
    }

    //Inserting String into Source class
    @TypeConverter
    fun toSource(name : String) : com.mrspd.newsapp.models.Source {
        return com.mrspd.newsapp.models.Source(name, name)
    }
}