package com.example.rickandmorty.dataSource.remote

import android.util.Log
import com.example.rickandmorty.dataSource.BaseDataSource
import com.example.rickandmorty.dataSource.BaseDataSource.DataSourceCallback
import com.example.rickandmorty.models.CharacterModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val remoteApi: RemoteApi
) : BaseDataSource {
    override suspend fun getData(
        callback: DataSourceCallback
    ) {
        Log.e("RemoteDataSource___________________","${remoteApi.getBaseCharsInfo()}")
        try {
            val count = (1..remoteApi.getBaseCharsInfo().info?.count!!)
                .toMutableList().joinToString(",")

            withContext(Dispatchers.Main) {
                callback.onSuccess(remoteApi.getCharsCount(count))
            }
        } catch(e: Exception) {
            callback.onError(Throwable("Error remote data fetch: ${e.message}"))
        }
    }

    override suspend fun saveData(characterModel: CharacterModel) {}
}