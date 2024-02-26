package com.kemarport.mahindrakiosk.unloadingconfirmation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kemarport.mahindrakiosk.helper.*
import com.kemarport.mahindrakiosk.home.model.unloading.VehicleUnloadingRequest
import com.kemarport.mahindrakiosk.repository.KioskRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class UnloadingConfirmationViewModel (
    application: Application,
    private val kioskRepository: KioskRepository
)  : AndroidViewModel(application) {

    //////////////////unloading complete
    val unloadingBeginMutableLiveData: MutableLiveData<Resource<GeneralResponse>> =
        MutableLiveData()

    fun unloadingComplete (
        token:String,
        baseUrl: String,
        vehicleUnloadingRequest: VehicleUnloadingRequest
    ) {
        viewModelScope.launch {
            safeAPICallVehicleUnloading(token,baseUrl, vehicleUnloadingRequest)
        }
    }

    private suspend fun safeAPICallVehicleUnloading(token:String,baseUrl: String, vehicleUnloadingRequest: VehicleUnloadingRequest) {
        unloadingBeginMutableLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = kioskRepository.vehicleUnloadingComplete (token,baseUrl, vehicleUnloadingRequest)
                unloadingBeginMutableLiveData.postValue(handleVehicleUnloading(response))
            } else {
                unloadingBeginMutableLiveData.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    unloadingBeginMutableLiveData.postValue(Resource.Error("${t.message}"))
                }
                else -> unloadingBeginMutableLiveData.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }

    private fun handleVehicleUnloading(response: Response<GeneralResponse>): Resource<GeneralResponse>? {
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




     val verifySecurityRFIDMutableLive: MutableLiveData<Resource<GeneralResponse>> =
         MutableLiveData()

     fun verifySecurityRFID(
         token:String,
         baseUrl: String,
         rfid: String,
           cardNo:String
     ) {
         viewModelScope.launch {
             safeAPICallVerifySecurityRFID(token,baseUrl, rfid,cardNo)
         }
     }

     private suspend fun safeAPICallVerifySecurityRFID(
         token: String,
         baseUrl: String,
         rfid: String,
         cardNo: String
     ) {
         verifySecurityRFIDMutableLive.postValue(Resource.Loading())
         try {
             if (Utils.hasInternetConnection(getApplication())) {
                 val response = kioskRepository.verifySecurityRFID(token,baseUrl, rfid,cardNo)
                 verifySecurityRFIDMutableLive.postValue(handleVerifySecurityRFIDResponse(response))
             } else {
                 verifySecurityRFIDMutableLive.postValue(Resource.Error(Constants.NO_INTERNET))
             }
         } catch (t: Throwable) {
             when (t) {
                 is Exception -> {
                     verifySecurityRFIDMutableLive.postValue(Resource.Error("${t.message}"))
                 }
                 else -> verifySecurityRFIDMutableLive.postValue(Resource.Error(Constants.CONFIG_ERROR))
             }
         }
     }

     private fun handleVerifySecurityRFIDResponse(response: Response<GeneralResponse>): Resource<GeneralResponse> {
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