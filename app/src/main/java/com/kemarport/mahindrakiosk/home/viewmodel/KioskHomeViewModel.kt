package com.kemarport.mahindrakiosk.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kemarport.mahindrakiosk.helper.*
import com.kemarport.mahindrakiosk.home.model.dock.DockReportingRequest
import com.kemarport.mahindrakiosk.home.model.dock.ListOfDockReportingVehicleResponseItem
import com.kemarport.mahindrakiosk.home.model.leveler.LevelerListResponse
import com.kemarport.mahindrakiosk.home.model.parking.ParkingListResponse
import com.kemarport.mahindrakiosk.home.model.unloading.VehicleUnloadingRequest
import com.kemarport.mahindrakiosk.repository.KioskRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class KioskHomeViewModel (
    application: Application,
    private val kioskRepository: KioskRepository
)  : AndroidViewModel(application) {
    val validateDockReporting: MutableLiveData<Resource<GeneralResponse>> =
        MutableLiveData()

    fun getIpValidation(
        token:String,
        baseUrl: String,
        kioskIpRequest: DockReportingRequest
    ) {
        viewModelScope.launch {
            safeAPICallValidateKioskIp(token,baseUrl, kioskIpRequest)
        }
    }

    private suspend fun safeAPICallValidateKioskIp(token:String,baseUrl: String, kioskIpRequest: DockReportingRequest) {
        validateDockReporting.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = kioskRepository.validateDockReporting(token,baseUrl, kioskIpRequest)
                validateDockReporting.postValue(handleValidateKioskIp(response))
            } else {
                validateDockReporting.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    validateDockReporting.postValue(Resource.Error("${t.message}"))
                }
                else -> validateDockReporting.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }

    private fun handleValidateKioskIp(response: Response<GeneralResponse>): Resource<GeneralResponse>? {
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


    //////get Current Dock vehicle list
    val getListOfDockReportingVehicleMutableLiveData: MutableLiveData<Resource<ArrayList<ListOfDockReportingVehicleResponseItem>>> =
        MutableLiveData()

    fun getListOfDockReportingVehicleMutableLiveData(
        token:String,
        baseUrl: String,
        kioskIp:String
        ) {
        viewModelScope.launch {
            safeAPICallGetDockQueue(token,baseUrl, kioskIp)
        }
    }

    private suspend fun safeAPICallGetDockQueue(token:String,baseUrl: String, kioskIp: String) {
        getListOfDockReportingVehicleMutableLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = kioskRepository.ListOfDockReportingVehicle (token,baseUrl, kioskIp)
                getListOfDockReportingVehicleMutableLiveData.postValue(handleListOfDockReportingVehicle(response))
            } else {
                getListOfDockReportingVehicleMutableLiveData.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    getListOfDockReportingVehicleMutableLiveData.postValue(Resource.Error("${t.message}"))
                }
                else -> getListOfDockReportingVehicleMutableLiveData.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }

    private fun handleListOfDockReportingVehicle(response: Response<ArrayList<ListOfDockReportingVehicleResponseItem>>): Resource<ArrayList<ListOfDockReportingVehicleResponseItem>>? {
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
 //////get Current parking vehicle list
    val getCurrentParkingVehicleMutableLiveData: MutableLiveData<Resource<ArrayList<ParkingListResponse>>> =
        MutableLiveData()

    fun getCurrentParkingVehicle(
        token:String,
        baseUrl: String,
        kioskIp:String
        ) {
        viewModelScope.launch {
            safeAPICallGetParkingList(token,baseUrl, kioskIp)
        }
    }

    private suspend fun safeAPICallGetParkingList(token:String,baseUrl: String, kioskIp: String) {
        getCurrentParkingVehicleMutableLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = kioskRepository.getCurrentParkinglistDetails (token,baseUrl, kioskIp)
                getCurrentParkingVehicleMutableLiveData.postValue(handleGetParkingListResponse(response))
            } else {
                getCurrentParkingVehicleMutableLiveData.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    getCurrentParkingVehicleMutableLiveData.postValue(Resource.Error("${t.message}"))
                }
                else -> getCurrentParkingVehicleMutableLiveData.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }

    private fun handleGetParkingListResponse(response: Response<ArrayList<ParkingListResponse>>): Resource<ArrayList<ParkingListResponse>>? {
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


    //////get Current Leveler list
    val getCurrentLevelerListMutableLiveData: MutableLiveData<Resource<ArrayList<LevelerListResponse>>> =
        MutableLiveData()

    fun getCurrentLevelerListVehicle(
        token:String,
        baseUrl: String,
        kioskIp:String
    ) {
        viewModelScope.launch {
            safeAPICallCurrentLevelerListVehicle(token,baseUrl, kioskIp)
        }
    }

    private suspend fun safeAPICallCurrentLevelerListVehicle(token:String,baseUrl: String, kioskIp: String) {
        getCurrentLevelerListMutableLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = kioskRepository.getCurrentLevelerListDetails (token,baseUrl, kioskIp)
                getCurrentLevelerListMutableLiveData.postValue(handleGetCurrentLevelerListVehicleResponse(response))
            } else {
                getCurrentLevelerListMutableLiveData.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    getCurrentLevelerListMutableLiveData.postValue(Resource.Error("${t.message}"))
                }
                else -> getCurrentLevelerListMutableLiveData.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }

    private fun handleGetCurrentLevelerListVehicleResponse(response: Response<ArrayList<LevelerListResponse>>): Resource<ArrayList<LevelerListResponse>>? {
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

    //////get Current RGP vehicle list
    val getCurrentRGPListMutableLiveData: MutableLiveData<Resource<ArrayList<LevelerListResponse>>> =
        MutableLiveData()

    fun getCurrentRGPList(
        token:String,
        baseUrl: String,
        kioskIp:String
    ) {
        viewModelScope.launch {
            safeAPICallCurrentRGPListVehicle(token,baseUrl, kioskIp)
        }
    }

    private suspend fun safeAPICallCurrentRGPListVehicle(token:String,baseUrl: String, kioskIp: String) {
        getCurrentRGPListMutableLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = kioskRepository.getCurrentRGPSListDetails (token,baseUrl, kioskIp)
                getCurrentRGPListMutableLiveData.postValue(handleCurrentRGPListVehicle(response))
            } else {
                getCurrentRGPListMutableLiveData.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    getCurrentRGPListMutableLiveData.postValue(Resource.Error("${t.message}"))
                }
                else -> getCurrentRGPListMutableLiveData.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }

    private fun handleCurrentRGPListVehicle(response: Response<ArrayList<LevelerListResponse>>): Resource<ArrayList<LevelerListResponse>>? {
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


    //////////////////unloading begin
    val unloadingBeginMutableLiveData: MutableLiveData<Resource<GeneralResponse>> =
        MutableLiveData()

    fun unloadingBegin (
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
                val response = kioskRepository.vehicleUnloading (token,baseUrl, vehicleUnloadingRequest)
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
                errorMessage = it.getString(Constants.HTTP_ERROR_MESSAGE)
            }
        }
        return Resource.Error(errorMessage)
    }



}


///get Dock Queue Details

// getCurrentDockQueueDetails

/*   val getCurrentDockQueueDetailsMutableList: MutableLiveData<Resource<DockQueueResponse>> =
       MutableLiveData()

   fun getCurrentDockQueueDetails(
       baseUrl: String,
       kisokIp: String
   ) {
       viewModelScope.launch {
           safeAPICallGetCurrentDockQueue(baseUrl, kisokIp)
       }
   }

   private suspend fun safeAPICallGetCurrentDockQueue(baseUrl: String, kisokIp: String) {
       getCurrentDockQueueDetailsMutableList.postValue(Resource.Loading())
       try {
           if (Utils.hasInternetConnection(getApplication())) {
               val response = kioskRepository.getCurrentDockQueueDetails("",baseUrl, kisokIp)
               getCurrentDockQueueDetailsMutableList.postValue(handleCurrentDockQueue(response))
           } else {
               getCurrentDockQueueDetailsMutableList.postValue(Resource.Error(Constants.NO_INTERNET))
           }
       } catch (t: Throwable) {
           when (t) {
               is Exception -> {
                   getCurrentDockQueueDetailsMutableList.postValue(Resource.Error("${t.message}"))
               }
               else -> getCurrentDockQueueDetailsMutableList.postValue(Resource.Error(Constants.CONFIG_ERROR))
           }
       }
   }

   private fun handleCurrentDockQueue(response: Response<DockQueueResponse>): Resource<DockQueueResponse>? {
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
   }*/