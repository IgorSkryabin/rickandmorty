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
    private val mIsLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = mIsLoading.asStateFlow()

    private val mFilterStatus = MutableStateFlow<String?>(null)
    private val mFilterGender = MutableStateFlow<String?>(null)
    private val mFilterSpecies = MutableStateFlow<String?>(null)
    private val mFilterOrigin = MutableStateFlow<String?>(null)
    private val mFilterLocation = MutableStateFlow<String?>(null)

    private val mState = MutableStateFlow<List<CharacterModel>>(emptyList())
    val state: StateFlow<List<CharacterModel>> = combine(
        mState,
        searchField,
        mFilterStatus,
        mFilterGender,
        mFilterSpecies,
        mFilterOrigin,
        mFilterLocation,
    ) { args: Array<Any?> ->

        var filteredList = args[0] as List<CharacterModel>
        var searchField = args[1] as String
        var statusFilter = args[2] as String?
        var genderFilter = args[3] as String?
        var speciesFilter = args[4] as String?
        var originFilter = args[5] as String?
        var locationFilter = args[6] as String?


        filteredList.filter { char ->

            val matchesQuery = searchField.isBlank() ||
                    char.name?.lowercase()?.contains(searchField.lowercase()) == true
            val matchesStatus = statusFilter == null ||
                    char.status.equals(statusFilter, ignoreCase = true)
            val matchesGender = genderFilter == null ||
                    char.gender.equals(genderFilter, ignoreCase = true)
            val matchesSpecies = speciesFilter == null ||
                    char.species.equals(speciesFilter, ignoreCase = true)
            val matchesOrigin = originFilter == null ||
                    char.origin?.name.equals(originFilter, ignoreCase = true)
            val matchesLocation = locationFilter == null ||
                    char.location?.name.equals(locationFilter, ignoreCase = true)

            matchesQuery && matchesStatus && matchesGender && matchesSpecies && matchesOrigin && matchesLocation
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
            clearFilters()
            getData()
        }
    }

    fun setStatusFilter(status: String?) { mFilterStatus.value = status }
    fun setGenderFilter(gender: String?) { mFilterGender.value = gender }
    fun setSpeciesFilter(species: String?) { mFilterSpecies.value = species }
    fun setOriginFilter(origin: String?) { mFilterOrigin.value = origin }
    fun setLocationFilter(location: String?) { mFilterLocation.value = location }

    fun clearFilters() {
        mFilterStatus.value = null
        mFilterGender.value = null
        mFilterSpecies.value = null
        mFilterOrigin.value = null
        mFilterLocation.value = null
    }

    fun getFilterValues(): Map<String, String?> {
        return mapOf(
            "Status" to mFilterStatus.value,
            "Gender" to mFilterGender.value,
            "Species" to mFilterSpecies.value,
            "Origin" to mFilterOrigin.value,
            "Location" to mFilterLocation.value
        )
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
        mIsLoading.value = true
        useCaseHandler.execute(
            getPosts,
            object : UseCase.UseCaseCallback<GetPosts.ResponseValue> {
                override suspend fun onSuccess(responseValue: GetPosts.ResponseValue) {
                    mState.value = responseValue.anValue
                    mIsLoading.value = false
                    mIsRefreshing.value = false
                }
                override fun onError(t: Throwable) {
                    mStateErr.value = t
                    mIsLoading.value = false
                    mIsRefreshing.value = false
                }
            }
        )
    }
}