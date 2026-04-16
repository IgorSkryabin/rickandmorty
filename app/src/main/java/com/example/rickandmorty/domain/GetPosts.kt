package com.example.rickandmorty.domain

import android.util.Log
import com.example.rickandmorty.common.UseCase
import com.example.rickandmorty.dataSource.BaseDataSource
import com.example.rickandmorty.dataSource.DataSourceRepo
import com.example.rickandmorty.models.CharacterModel
import javax.inject.Inject

class GetPosts @Inject constructor(
    private val dataSourceRepo: DataSourceRepo,
): UseCase<GetPosts.ResponseValue>() {

    override suspend fun executeUseCase() {
        dataSourceRepo.getData(object: BaseDataSource.DataSourceCallback {
            override suspend fun onSuccess(charactersList: List<CharacterModel>) {
        Log.e("executeUseCase___________________","executeUseCase_______________")
                val responseValue = ResponseValue(charactersList)
                callback?.onSuccess(responseValue)
            }

            override fun onError(errorMsg: Throwable) {
                callback?.onError(errorMsg)
            }
        })
    }

    class ResponseValue(val anValue: List<CharacterModel>): UseCase.ResponseValue
}