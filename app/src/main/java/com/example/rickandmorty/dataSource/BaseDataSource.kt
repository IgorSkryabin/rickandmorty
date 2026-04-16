package com.example.rickandmorty.dataSource

import com.example.rickandmorty.models.CharacterModel

interface BaseDataSource {

    interface DataSourceCallback {
        suspend fun onSuccess(charactersList: List<CharacterModel>)
        fun onError(errorMsg: Throwable)
    }

    suspend fun getData(
        callback: DataSourceCallback
    )
    suspend fun saveData(characterModel: CharacterModel)
}