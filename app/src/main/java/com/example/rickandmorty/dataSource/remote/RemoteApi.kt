package com.example.rickandmorty.dataSource.remote

import com.example.rickandmorty.models.CharacterModel
import com.example.rickandmorty.models.DataModel
import retrofit2.http.GET
import retrofit2.http.Path

interface RemoteApi {

    @GET("character")
    suspend fun getBaseCharsInfo() : DataModel

    @GET("character/{ids}")
    suspend fun getCharsCount(
        @Path("ids") ids: String?
    ) : List<CharacterModel>
}