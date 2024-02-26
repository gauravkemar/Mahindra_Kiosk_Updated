package com.kemarport.mahindrakiosk.repository

import com.kemarport.mahindrakiosk.api.RetrofitInstance
import com.kemarport.mahindrakiosk.dockleveler.model.CurrentDockLevelerUpdate
import com.kemarport.mahindrakiosk.helper.Constants
import com.kemarport.mahindrakiosk.helper.GeneralRequest
import com.kemarport.mahindrakiosk.home.model.dock.DockReportingRequest
import com.kemarport.mahindrakiosk.home.model.unloading.VehicleUnloadingRequest
import com.kemarport.mahindrakiosk.login.model.KioskIPRequest
import com.kemarport.mahindrakiosk.login.model.LoginRequest
import com.kemarport.mahindrakiosk.login.model.changepassword.ChangePasswordRequest
import retrofit2.http.Header
import retrofit2.http.Query

class KioskRepository {
    suspend fun getAppDetails(
        baseUrl: String
    ) = RetrofitInstance.api(baseUrl).getAppDetails()
    suspend fun validateDockReporting(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        kioskIpRequest: DockReportingRequest
    ) = RetrofitInstance.api(baseUrl).validateDockReporting(bearerToken, kioskIpRequest)

    suspend fun vehicleUnloading(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        vehicleUnloadingRequest: VehicleUnloadingRequest
    ) = RetrofitInstance.api(baseUrl).vehicleUnloading(bearerToken, vehicleUnloadingRequest)

    suspend fun vehicleUnloadingComplete(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        vehicleUnloadingRequest: VehicleUnloadingRequest
    ) = RetrofitInstance.api(baseUrl).vehicleUnloadingComplete(bearerToken, vehicleUnloadingRequest)

    suspend fun curentDockLevelerUpdate(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        currentDockLevelerUpdate: ArrayList<CurrentDockLevelerUpdate>
    ) = RetrofitInstance.api(baseUrl).curentDockLevelerUpdate(bearerToken, currentDockLevelerUpdate)
    suspend fun getUserIdDetails(
        baseUrl: String,
        epcCode: String
    ) = RetrofitInstance.api(baseUrl).getUserIdDetails(epcCode)

    suspend fun getDockShopDetails(
        baseUrl: String,
        @Query("IpAddress") ipAddress: String
    ) = RetrofitInstance.api(baseUrl).getDockShopDetails(ipAddress)

    suspend fun ListOfDockReportingVehicle(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("KisokIp") kioskIp: String
    ) = RetrofitInstance.api(baseUrl).ListOfDockReportingVehicle(bearerToken, kioskIp)

    suspend fun getCurrentParkinglistDetails(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("IPAddress") ipAddress: String
    ) = RetrofitInstance.api(baseUrl).getCurrentParkinglistDetails(bearerToken, ipAddress)

    suspend fun getCurrentLevelerListDetails(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("KisokIp") ipAddress: String
    ) = RetrofitInstance.api(baseUrl).getCurrentLevelerListDetails(bearerToken, ipAddress)

    suspend fun getCurrentRGPSListDetails(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("KisokIp") kisokIp: String
    ) = RetrofitInstance.api(baseUrl).getCurrentRGPSListDetails(bearerToken, kisokIp)

    suspend fun getCurrentDockLevelerList(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("KisokIp") kisokIp: String
    ) = RetrofitInstance.api(baseUrl).getCurrentDockLevelerList(bearerToken, kisokIp)


    suspend fun generalDemo(
        //@Header("Authorization") bearerToken: String,
        baseUrl: String,
        generalRequest: GeneralRequest
    ) = RetrofitInstance.api(baseUrl).generalDemo(generalRequest)

    suspend fun loginUser(
        baseUrl: String,
        loginRequest: LoginRequest
    ) = RetrofitInstance.api(baseUrl).loginUser(loginRequest)

    suspend fun changePassword(
        baseUrl: String,
        changePasswordRequest: ChangePasswordRequest
    ) = RetrofitInstance.api(baseUrl).changePassword(changePasswordRequest)
    suspend fun verifySecurityRFID(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("RFID") rfid: String,
        @Query("CardNo") cardNo:String
    ) = RetrofitInstance.api(baseUrl).verifySecurityRFID(bearerToken, rfid,cardNo)
/*    suspend fun getCurrentDockQueueDetails(
        @Header("Authorization") bearerToken: String,
        baseUrl: String,
        kisokIp: String
    ) = RetrofitInstance.api(baseUrl).getCurrentDockQueueDetails(bearerToken,kisokIp)*/

/*    suspend fun getUserDetails(
        baseUrl: String,
        epcCode: String
    ) = RetrofitInstance.api(baseUrl).getUserDetails(epcCode)*/
}