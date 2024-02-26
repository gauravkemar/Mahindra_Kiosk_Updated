package com.kemarport.mahindrakiosk.dockleveler.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kemarport.mahindrakiosk.dockleveler.model.CurrentDockLevelerResponse
import com.kemarport.mahindrakiosk.dockleveler.model.CurrentDockLevelerUpdate
import com.kemarport.mahindrakiosk.helper.*
import com.kemarport.mahindrakiosk.repository.KioskRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class DockLevelerViewModel(
    application: Application,
    private val kioskRepository: KioskRepository
)  : AndroidViewModel(application) {

    // docklevelers list
    val getDockLevelerMutableLiveData: MutableLiveData<Resource<ArrayList<CurrentDockLevelerResponse>>> =
        MutableLiveData()

    fun getDockLevelerList(
        token:String,
        baseUrl: String,
        kisokIp:String
    ) {
        viewModelScope.launch {
            safeAPICallGetDockLevelerList(  token,baseUrl,   kisokIp)
        }
    }

    private suspend fun safeAPICallGetDockLevelerList(token:String,baseUrl: String,   kisokIp:String) {
        getDockLevelerMutableLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = kioskRepository.getCurrentDockLevelerList("",baseUrl, kisokIp)
                getDockLevelerMutableLiveData.postValue(handleDockLevelerListResponse(response))
            } else {
                getDockLevelerMutableLiveData.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    getDockLevelerMutableLiveData.postValue(Resource.Error("${t.message}"))
                }
                else -> getDockLevelerMutableLiveData.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }

    private fun handleDockLevelerListResponse(response: Response<ArrayList<CurrentDockLevelerResponse>>):
            Resource<ArrayList<CurrentDockLevelerResponse>> {
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
                errorMessage = it.getString(Constants.HTTP_ERROR_MESSAGE)
            }
        }
        return Resource.Error(errorMessage)
    }


//update docklevelers
    val updateCurrentDockLevelerMutableLiveData: MutableLiveData<Resource<GeneralResponse>> =
        MutableLiveData()

    fun updateCurrentDockLeveler(
        token:String,
        baseUrl: String,
        currentDockLevelerUpdate: ArrayList<CurrentDockLevelerUpdate>
    ) {
        viewModelScope.launch {
            safeAPICallUpdateCurrentDockLeveler(token,baseUrl, currentDockLevelerUpdate)
        }
    }
    private suspend fun safeAPICallUpdateCurrentDockLeveler(token:String,baseUrl: String,currentDockLevelerUpdate: ArrayList<CurrentDockLevelerUpdate>) {
        updateCurrentDockLevelerMutableLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = kioskRepository.curentDockLevelerUpdate(token,baseUrl, currentDockLevelerUpdate)
                updateCurrentDockLevelerMutableLiveData.postValue(handleDockLevelerUpdateResponse(response))
            } else {
                updateCurrentDockLevelerMutableLiveData.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    updateCurrentDockLevelerMutableLiveData.postValue(Resource.Error("${t.message}"))
                }
                else -> updateCurrentDockLevelerMutableLiveData.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }
    private fun handleDockLevelerUpdateResponse(response: Response<GeneralResponse>): Resource<GeneralResponse> {
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
                errorMessage = it.getString(Constants.HTTP_ERROR_MESSAGE)
            }
        }
        return Resource.Error(errorMessage)
    }
}