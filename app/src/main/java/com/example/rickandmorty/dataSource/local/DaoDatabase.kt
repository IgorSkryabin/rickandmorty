package com.example.rickandmorty.dataSource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.rickandmorty.models.CharacterModel

@Dao
interface DaoDatabase {

    @Query("SELECT * FROM characters")
    suspend fun getAll(): List<CharacterModel>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertModel(character: CharacterModel)
}