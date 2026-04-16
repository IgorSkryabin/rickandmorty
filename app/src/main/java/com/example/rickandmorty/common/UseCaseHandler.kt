package com.example.rickandmorty.common

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UseCaseHandler @Inject constructor() {

    suspend fun<R : UseCase.ResponseValue>execute(
        useCase: UseCase<R>,
        callback: UseCase.UseCaseCallback<R>
    ) {
        useCase.callback = UiCallbackWrapper(this,callback)
        withContext(Dispatchers.Main) {
            Log.e("UseCaseHandler___________________","UseCaseHandler_______________")
        }
        useCase.run()
    }

    private suspend fun<V : UseCase.ResponseValue>notifyResponse(
        responseValue: V,
        callback: UseCase.UseCaseCallback<V>
    ) {
        callback.onSuccess(responseValue)
    }
    private fun<V : UseCase.ResponseValue>notifyError(
        t: Throwable,
        callback: UseCase.UseCaseCallback<V>
    ) {
        callback.onError(t)
    }

    class UiCallbackWrapper<V : UseCase.ResponseValue>(
        private val handler: UseCaseHandler,
        private val callback: UseCase.UseCaseCallback<V>
    ) : UseCase.UseCaseCallback<V> {

        override suspend fun onSuccess(responseValue: V) {
            handler.notifyResponse(responseValue,callback)
        }

        override fun onError(t: Throwable) {
            handler.notifyError(t,callback)
        }

    }
}