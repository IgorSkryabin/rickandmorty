package com.example.rickandmorty.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmorty.common.UseCase
import com.example.rickandmorty.common.UseCaseHandler
import com.example.rickandmorty.domain.GetPosts
import com.example.rickandmorty.models.CharacterModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharsViewModel @Inject constructor(
    private val getPosts: GetPosts,
    private val useCaseHandler: UseCaseHandler,
): ViewModel() {

    private val mState = MutableStateFlow<List<CharacterModel>>(emptyList())
    val state: StateFlow<List<CharacterModel>> = mState.asStateFlow()

    private val mStateErr = MutableStateFlow(Throwable(""))
    val stateErr: StateFlow<Throwable> = mStateErr.asStateFlow()

    init {
        viewModelScope.launch {
            getData()
        }
    }

    private suspend fun getData() {
        useCaseHandler.execute(
            getPosts,
            object : UseCase.UseCaseCallback<GetPosts.ResponseValue> {
                override suspend fun onSuccess(responseValue: GetPosts.ResponseValue) {
                    mState.value = responseValue.anValue
                }
                override fun onError(t: Throwable) {
                    mStateErr.value = t
                }
            }
        )
    }
}