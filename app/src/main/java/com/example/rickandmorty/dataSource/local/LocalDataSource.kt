package com.example.rickandmorty.dataSource.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import coil3.ImageLoader
import coil3.asDrawable
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.example.rickandmorty.dataSource.BaseDataSource
import com.example.rickandmorty.dataSource.BaseDataSource.DataSourceCallback
import com.example.rickandmorty.models.CharacterModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val daoDatabase: DaoDatabase,
    @param:ApplicationContext private val context: Context
) : BaseDataSource {

    var lastUpdate: Long = 0

    override suspend fun getData(
        callback: DataSourceCallback
    ) {
        Log.e("LocalDataSource___________________","${daoDatabase.getAll()}")
        lastUpdate = System.currentTimeMillis()

        try {
            delay(800)

            val rawCharacters = daoDatabase.getAll()

            val charactersWithLocalImages = rawCharacters.map { character ->
                val localFile = File(context.filesDir, "${character.id}.jpg")
                if (localFile.exists()) {
                    character.copy(image = localFile.absolutePath)
                } else {
                    character
                }
            }
            withContext(Dispatchers.Main) {
                callback.onSuccess(charactersWithLocalImages)
            }
        } catch(e: Exception) {
            callback.onError(Throwable("Error local data fetch: ${e.message}"))
        }
    }

    override suspend fun saveData(characterModel: CharacterModel) {
        daoDatabase.insertModel(characterModel)

        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(characterModel.image)
            .allowHardware(false)
            .build()

        val result = loader.execute(request)
        val image = result.image
        
        if (image != null) {
            val file = File(context.filesDir, "${characterModel.id}.jpg")
            withContext(Dispatchers.IO) {
                try {
                    FileOutputStream(file).use { outputStream ->
                        val bitmap = (image.asDrawable(context.resources) as? BitmapDrawable)?.bitmap
                        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    }
                } catch (e: Exception) {
                    Log.e("LocalDataSource", "Error saving image for char ${characterModel.id}: ${e.message}")
                }
            }
        }
    }
}