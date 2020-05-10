package com.pv_libs.coroutine_cache_pro.ui

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pv_libs.coroutine_cache_pro.models.GetUsersResponse
import com.pv_libs.coroutine_cache_pro.models.User
import com.pv_libs.coroutine_cache_pro.network.Retrofit
import com.pv_libs.coroutine_cachepro.ApiResult
import com.pv_libs.coroutine_cachepro.utils.isFromCache
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Response

class SampleActivityViewModel(app: Application) : AndroidViewModel(app) {

    private val apiService = Retrofit.getCoroutineApiService(app)
    private val apiCaller = apiService.getUsersApiCaller()

    val inApiRunningLiveData = apiCaller.isApiInProgressLiveData
    val usersListLiveData = MutableLiveData<List<User>>()

    init {
        viewModelScope.launch {
            initOnFetchUsersApi()
        }
    }

    private suspend fun initOnFetchUsersApi() {
        apiCaller.getResponseFlow().collect {
            when (it) {
                is ApiResult.Success -> {
                    onResponse(it.data)
                }
                is ApiResult.Error -> {
                    showToast(it.exception.localizedMessage)
                }
            }
        }
    }

    private fun onResponse(response: Response<GetUsersResponse>) {
        if (response.isSuccessful) {
            showToast(
                if (response.isFromCache()) "Loaded data from cache" else "Loaded data from server"
            )
            usersListLiveData.postValue(response.body()!!.users)
        } else {
            showToast(response.message())
        }
    }

    private fun showToast(message: String?) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }

    fun fetchUsers() {
        apiCaller.fetchFromServer()
    }

}