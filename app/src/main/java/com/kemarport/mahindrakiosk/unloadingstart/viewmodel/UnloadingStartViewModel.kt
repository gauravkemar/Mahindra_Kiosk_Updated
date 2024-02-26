package com.kemarport.mahindrakiosk.unloadingstart.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kemarport.mahindrakiosk.helper.*
import com.kemarport.mahindrakiosk.repository.KioskRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class UnloadingStartViewModel (
    application: Application,
    private val kioskRepository: KioskRepository
)  : AndroidViewModel(application) {
    val getApiResponseMutableLiveData: MutableLiveData<Resource<GeneralResponse>> =
        MutableLiveData()

    fun getContainerDetails(
        baseUrl: String,
        requestModel: GeneralRequest
    ) {
        viewModelScope.launch {
            safeAPICallGetContainerDetails(baseUrl, requestModel)
        }
    }

    private suspend fun safeAPICallGetContainerDetails(baseUrl: String, generalRequest: GeneralRequest) {
        getApiResponseMutableLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = kioskRepository.generalDemo(baseUrl, generalRequest)
                getApiResponseMutableLiveData.postValue(handleContainerDetailsResponse(response))
            } else {
                getApiResponseMutableLiveData.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    getApiResponseMutableLiveData.postValue(Resource.Error("${t.message}"))
                }
                else -> getApiResponseMutableLiveData.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }

    private fun handleContainerDetailsResponse(response: Response<GeneralResponse>): Resource<GeneralResponse> {
        var errorMessage = ""
        if (response.isSuccessful) {
            response.body()?.let { response ->
                return Resource.Success(response)
            }
        } else if (response.errorBody() != null) {
            val errorObject = response.errorBody()?.let {
                JSONObject(it.charStream().readText())
            }
            errorObject?.let {
                errorMessage = it.getString("errorMessage")
            }
        }
        return Resource.Error(errorMessage)
    }
}