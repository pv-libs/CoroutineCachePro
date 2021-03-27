package com.pv_libs.coroutine_cache_pro.ui

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pv_libs.coroutine_cache_pro.models.GetUsersResponse
import com.pv_libs.coroutine_cache_pro.network.Retrofit
import com.pv_libs.coroutine_cache_pro.utils.unWrapResponse
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SampleActivityViewModel(app: Application) : AndroidViewModel(app) {

    private val apiService = Retrofit.getCoroutineApiService(app)
    private val apiCaller = apiService.getUsersApiCaller()

    val inApiRunningStateFlow = apiCaller.isApiInProgressSateFlow

    val usersListFlow: StateFlow<GetUsersResponse?> = apiCaller.getResponseFlow()
        .unWrapResponse {
            showToast(it.localizedMessage)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)


    private fun showToast(message: String?) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }

    fun fetchUsers() {
        apiCaller.fetchFromServer()
    }

}