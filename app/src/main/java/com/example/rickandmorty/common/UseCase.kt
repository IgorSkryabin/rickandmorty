package com.example.rickandmorty.common

abstract class UseCase<P : UseCase.ResponseValue>{

    var callback: UseCaseCallback<P>? = null


    interface UseCaseCallback<R> {
        suspend fun onSuccess(responseValue: R)
        fun onError(t: Throwable)
    }

    interface ResponseValue

    suspend fun run() {
        executeUseCase()
    }

    abstract suspend fun executeUseCase()

}