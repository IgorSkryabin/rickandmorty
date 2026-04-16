package com.example.rickandmorty.dataSource

import com.example.rickandmorty.dataSource.local.LocalDataSource
import com.example.rickandmorty.models.CharacterModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSourceRepo @Inject constructor(
    private val localDataSource: BaseDataSource,
    private val remoteDataSource: BaseDataSource
): BaseDataSource {
    private var isCacheDirty = false
    private val cacheTimeout = 5 * 60 * 1000
    override suspend fun getData(
        callback: BaseDataSource.DataSourceCallback
    ) {
        localDataSource.getData(object: BaseDataSource.DataSourceCallback {
                    override suspend fun onSuccess(charactersList: List<CharacterModel>) {
                        checkCacheDirty()
                        if (charactersList.isNotEmpty() && !isCacheDirty) {
                            loadFromLocal(callback)
                        } else {
                            loadFromRemote(callback)
                        }
                    }
                    override fun onError(errorMsg: Throwable) {
                        callback.onError(errorMsg)
                    }
                })
    }
    suspend fun loadFromRemote(
        callback: BaseDataSource.DataSourceCallback
    ) {
        remoteDataSource.getData(
            object : BaseDataSource.DataSourceCallback {
                override suspend fun onSuccess(charactersList: List<CharacterModel>) {
                    callback.onSuccess(charactersList)
                    rewriteLocalStorage(charactersList)
                    isCacheDirty = false
                }
                override fun onError(errorMsg: Throwable) {
                    callback.onError(errorMsg)
                }
            }
        )
    }
    suspend fun loadFromLocal(
        callback: BaseDataSource.DataSourceCallback
    ) {
        localDataSource.getData(
            object : BaseDataSource.DataSourceCallback {
                override suspend fun onSuccess(charactersList: List<CharacterModel>) {
                    callback.onSuccess(charactersList)
                }
                override fun onError(errorMsg: Throwable) {
                    callback.onError(errorMsg)
                }
            }
        )
    }
    fun checkCacheDirty() {
        isCacheDirty = System.currentTimeMillis() - (localDataSource as LocalDataSource)
            .lastUpdate > cacheTimeout
    }
    override suspend fun saveData(characterModel: CharacterModel) {
        localDataSource.saveData(characterModel)
    }
    suspend fun rewriteLocalStorage(charactersList: List<CharacterModel>) {
        charactersList.forEach { saveData(it) }
    }
}