package com.example.rickandmorty.dataSource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rickandmorty.models.CharacterModel
import com.example.rickandmorty.models.converters.StringListConverters

@Database(entities = [CharacterModel::class], version = 1)
@TypeConverters(StringListConverters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun provideDaoDatabase() : DaoDatabase
}