package com.example.rickandmorty.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmorty.common.UseCase
import com.example.rickandmorty.common.UseCaseHandler
import com.example.rickandmorty.domain.GetPosts
import com.example.rickandmorty.models.CharacterModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharsViewModel @Inject constructor(
    private val getPosts: GetPosts,
    private val useCaseHandler: UseCaseHandler,
): ViewModel() {

    private val mSearchField = MutableStateFlow("")
    val searchField: StateFlow<String> = mSearchField.asStateFlow()
    private val mIsRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = mIsRefreshing.asStateFlow()
    private val mState = MutableStateFlow<List<CharacterModel>>(emptyList())
    val state: StateFlow<List<CharacterModel>> = combine(
        mState,
        searchField,
    ) { mState, searchField ->
        mState.filter { char ->
            val matchesQuery = searchField.isBlank() ||
                    char.name?.lowercase()?.contains(searchField.lowercase()) == true

            matchesQuery
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val mStateErr = MutableStateFlow(Throwable(""))
    val stateErr: StateFlow<Throwable> = mStateErr.asStateFlow()

    fun onRefresh() {
        viewModelScope.launch {
            mIsRefreshing.value = true
            mSearchField.value = ""
            getData()
        }
    }
    init {
        viewModelScope.launch {
            getData()
        }
    }

    fun searchByTextField(text: String) {
        mSearchField.value = text
    }
    private suspend fun getData() {
        useCaseHandler.execute(
            getPosts,
            object : UseCase.UseCaseCallback<GetPosts.ResponseValue> {
                override suspend fun onSuccess(responseValue: GetPosts.ResponseValue) {
                    mState.value = responseValue.anValue
                    mIsRefreshing.value = false
                }
                override fun onError(t: Throwable) {
                    mStateErr.value = t
                    mIsRefreshing.value = false
                }
            }
        )
    }
}