package com.example.rickandmorty.di.modules

import com.example.rickandmorty.common.UseCaseHandler
import com.example.rickandmorty.dataSource.DataSourceRepo
import com.example.rickandmorty.domain.GetPosts
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {
    @Provides
    fun provideGetPosts(dataSourceRepo: DataSourceRepo): GetPosts {
        return GetPosts(dataSourceRepo)
    }
    @Provides
    fun provideHandler(): UseCaseHandler {
        return UseCaseHandler()
    }
}