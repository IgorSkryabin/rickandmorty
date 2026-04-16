package com.example.rickandmorty.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

data class DataModel(
    val info: PageInfo? = PageInfo(),
    val results: List<CharacterModel>? = mutableListOf()
)

data class PageInfo(
    val count: Int? = null,
    val pages: Int? = null,
    val next: String? = null,
    val prev: String? = null,
    val results: List<CharacterModel> = listOf()
)

@Serializable
@Entity(tableName = "characters")
data class CharacterModel(
    @PrimaryKey val id: Int? = null,
    @ColumnInfo("name") val name: String? = null,
    @ColumnInfo("status") val status: String? = null,
    @ColumnInfo("species") val species: String? = null,
    @ColumnInfo("type") val type: String? = null,
    @ColumnInfo("gender") val gender: String? = null,
    @Embedded val origin: Origin? = Origin(),
    @Embedded val location: Location? = Location(),
    @ColumnInfo("image") val image: String? = null,
    @ColumnInfo("episode") val episode: List<String> = listOf(),
    @ColumnInfo("url") val url: String? = null,
    @ColumnInfo("created") val created: String? = null,
    )

@Serializable
data class Origin(
    @ColumnInfo("origin") val name: String? = null,
    @ColumnInfo("origin url") val url: String? = null,
)

@Serializable
data class Location(
    @ColumnInfo("location") val name: String? = null,
    @ColumnInfo("location url") val url: String? = null,
)
