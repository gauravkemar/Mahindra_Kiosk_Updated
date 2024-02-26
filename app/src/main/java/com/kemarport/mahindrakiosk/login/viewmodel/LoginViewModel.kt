package com.kemarport.mahindrakiosk.login.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import com.kemarport.mahindrakiosk.helper.*
import com.kemarport.mahindrakiosk.login.model.*
import com.kemarport.mahindrakiosk.login.model.appDetails.GetAppDetailsResponse
import com.kemarport.mahindrakiosk.login.model.changepassword.ChangePasswordRequest
import com.kemarport.mahindrakiosk.login.model.changepassword.ChangePasswordResponse
import com.kemarport.mahindrakiosk.repository.KioskRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Query

class LoginViewModel (
    application: Application,
    private val kioskRepository: KioskRepository
)  : AndroidViewModel(application) {

    //KIOSK IP DETAILS
    val getDockShopDetailsMutableLiveData: MutableLiveData<Resource<KioskIPResponse>> =
        MutableLiveData()

    fun getKiosIP(
        baseUrlKioskIp: String,
        ipAddress: String
    ) {
        viewModelScope.launch {
            safeAPICallGetDockDetails(baseUrlKioskIp, ipAddress)
        }
    }

    private suspend fun safeAPICallGetDockDetails(baseUrl: String,  ipAddress: String) {
        getDockShopDetailsMutableLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = kioskRepository.getDockShopDetails(baseUrl, ipAddress)
                getDockShopDetailsMutableLiveData.postValue(handleKioskIpDetailsResponse(response))
            } else {
                getDockShopDetailsMutableLiveData.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    getDockShopDetailsMutableLiveData.postValue(Resource.Error("${t.message}"))
                }
                else -> getDockShopDetailsMutableLiveData.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }

    private fun handleKioskIpDetailsResponse(response: Response<KioskIPResponse>): Resource<KioskIPResponse> {
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


/////////Login Api
    val loginUserMutableLiveData: MutableLiveData<Resource<LoginResponse>> =
        MutableLiveData()

    fun loginUser(
        baseUrl: String,
        loginRequest: LoginRequest
    ) {
        viewModelScope.launch {
            safeAPICallLoginUserDetails(baseUrl, loginRequest)
        }
    }

    private suspend fun safeAPICallLoginUserDetails(baseUrl: String,   loginRequest: LoginRequest) {
        loginUserMutableLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = kioskRepository.loginUser(baseUrl, loginRequest)
                loginUserMutableLiveData.postValue(handleUserLoginResponse(response))
            } else {
                loginUserMutableLiveData.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    loginUserMutableLiveData.postValue(Resource.Error("${t.message}"))
                }
                else -> loginUserMutableLiveData.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }

    private fun handleUserLoginResponse(response: Response<LoginResponse>): Resource<LoginResponse> {
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

    /////////USER DETAILS API

    val getUserIdDetailsMutableList: MutableLiveData<Resource<GetUserIDDetailsResponse>> =
        MutableLiveData()

    fun getUserIdDetails(
        baseUrl: String,
        epcCode: String
    ) {
        viewModelScope.launch {
            safeAPICallGetUserIdDetails(baseUrl, epcCode)
        }
    }

    private suspend fun safeAPICallGetUserIdDetails(baseUrl: String, epcCode: String) {
        getUserIdDetailsMutableList.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = kioskRepository.getUserIdDetails(baseUrl, epcCode)
                getUserIdDetailsMutableList.postValue(handleUserIdDetailsResponse(response))
            } else {
                getUserIdDetailsMutableList.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    getUserIdDetailsMutableList.postValue(Resource.Error("${t.message}"))
                }
                else -> getUserIdDetailsMutableList.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }

    private fun handleUserIdDetailsResponse(response: Response<GetUserIDDetailsResponse>): Resource<GetUserIDDetailsResponse> {
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

    //getApp details
    val getAppDetailsMutableLiveData: MutableLiveData<Resource<GetAppDetailsResponse>> = MutableLiveData()

    fun getAppDetails(
        baseUrl: String
    ) = viewModelScope.launch {
        safeAPICallGetAppDetails(baseUrl)
    }

    private suspend fun safeAPICallGetAppDetails(baseUrl: String) {
        getAppDetailsMutableLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = kioskRepository.getAppDetails(baseUrl)
                getAppDetailsMutableLiveData.postValue(handleGetAppDetailsResponse(response))
            } else {
                getAppDetailsMutableLiveData.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    getAppDetailsMutableLiveData.postValue(Resource.Error("${t.message}"))

                }
                else -> getAppDetailsMutableLiveData.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }


    private fun handleGetAppDetailsResponse(response: Response<GetAppDetailsResponse>): Resource<GetAppDetailsResponse> {
        var errorMessage = ""
        if (response.isSuccessful) {
            response.body()?.let { appDetailsResponse ->
                return Resource.Success(appDetailsResponse)
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

    /////////changepasword Api
    val changePasswordMutableLiveData: MutableLiveData<Resource<ChangePasswordResponse>> =
        MutableLiveData()

    fun changePassword(
        baseUrl: String,
        changePasswordRequest: ChangePasswordRequest
    ) {
        viewModelScope.launch {
            safeAPICallChangePasswordDetails(baseUrl,changePasswordRequest)
        }
    }

    private suspend fun safeAPICallChangePasswordDetails(baseUrl: String,   changePasswordRequest: ChangePasswordRequest) {
        changePasswordMutableLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = kioskRepository.changePassword(baseUrl, changePasswordRequest)
                changePasswordMutableLiveData.postValue(handleChangePasswordResponse(response))
            } else {
                changePasswordMutableLiveData.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    changePasswordMutableLiveData.postValue(Resource.Error("${t.message}"))
                }
                else -> changePasswordMutableLiveData.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }

    private fun handleChangePasswordResponse(response: Response<ChangePasswordResponse>): Resource<ChangePasswordResponse> {
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