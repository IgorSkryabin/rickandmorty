package com.example.rickandmorty.di.modules

import android.content.Context
import androidx.room.Room
import com.example.rickandmorty.dataSource.BaseDataSource
import com.example.rickandmorty.dataSource.DataSourceRepo
import com.example.rickandmorty.dataSource.local.AppDatabase
import com.example.rickandmorty.dataSource.local.DaoDatabase
import com.example.rickandmorty.dataSource.local.LocalDataSource
import com.example.rickandmorty.dataSource.remote.RemoteApi
import com.example.rickandmorty.dataSource.remote.RemoteDataSource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideRepo(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource
    ): DataSourceRepo {
        return DataSourceRepo(
            localDataSource,
            remoteDataSource
        )
    }
    @Singleton
    @Provides
    fun provideLocalDataSource(
        daoDatabase: DaoDatabase,
        @ApplicationContext context: Context
    ): BaseDataSource {
        return LocalDataSource(
            daoDatabase,
            context
        )
    }
    @Singleton
    @Provides
    fun provideDaoDatabase(@ApplicationContext context: Context): DaoDatabase {
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "characters"
        ).build()
        return db.provideDaoDatabase()
    }
    @Singleton
    @Provides
    fun provideRemoteDataSource(
        remoteApi: RemoteApi
    ): BaseDataSource {
        return RemoteDataSource(
            remoteApi
        )
    }
    @Singleton
    @Provides
    fun provideRetrofitApi(): RemoteApi {
        val BASE_URL = "https://rickandmortyapi.com/api/"
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(RemoteApi::class.java)
    }
}