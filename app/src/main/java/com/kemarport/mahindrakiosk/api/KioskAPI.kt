package com.kemarport.mahindrakiosk.api


import com.kemarport.mahindrakiosk.dockleveler.model.CurrentDockLevelerResponse
import com.kemarport.mahindrakiosk.dockleveler.model.CurrentDockLevelerUpdate
import com.kemarport.mahindrakiosk.helper.Constants
import com.kemarport.mahindrakiosk.helper.GeneralRequest
import com.kemarport.mahindrakiosk.helper.GeneralResponse
import com.kemarport.mahindrakiosk.home.model.dock.DockReportingRequest
import com.kemarport.mahindrakiosk.home.model.dock.ListOfDockReportingVehicleResponseItem
import com.kemarport.mahindrakiosk.home.model.leveler.LevelerListResponse
import com.kemarport.mahindrakiosk.home.model.parking.ParkingListResponse
import com.kemarport.mahindrakiosk.home.model.unloading.VehicleUnloadingRequest
import com.kemarport.mahindrakiosk.login.model.*
import com.kemarport.mahindrakiosk.login.model.appDetails.GetAppDetailsResponse
import com.kemarport.mahindrakiosk.login.model.changepassword.ChangePasswordRequest
import com.kemarport.mahindrakiosk.login.model.changepassword.ChangePasswordResponse
import retrofit2.Response
import retrofit2.http.*


interface KioskAPI {

    /*   @GET(Constants.GET_APP_DETAILS)
       suspend fun getAppDetails(
       ): Response<GetAppDetailsResponse>

       @POST(Constants.LOGIN_URL)
       suspend fun etmsLogin(
           @Body
           etmsLoginRequest: EtmsLoginRequest
       ): Response<EtmsLoginResponse>
   */

    @GET(Constants.GET_APP_DETAILS)
    suspend fun getAppDetails(
    ): Response<GetAppDetailsResponse>

    @POST(Constants.LOGIN_URL)
    suspend fun generalDemo(
        @Body
        generalRequest: GeneralRequest
    ): Response<GeneralResponse>


    @POST(Constants.DOCK_REPORTING_CHECK)
    suspend fun validateDockReporting(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        @Body
        dockReportingRequest: DockReportingRequest
    ): Response<GeneralResponse>

    @GET(Constants.KIOSK_IP)
    suspend fun getDockShopDetails(
        @Query("IpAddress") ipAddress: String
    ): Response<KioskIPResponse>

    @POST(Constants.KIOSK_UNLOADING_BEGIN)
    suspend fun vehicleUnloading(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        @Body
        vehicleUnloadingRequest: VehicleUnloadingRequest
    ): Response<GeneralResponse>


    @POST(Constants.KIOSK_UNLOADING_BEGIN)
    suspend fun vehicleUnloadingComplete(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        @Body
        vehicleUnloadingRequest: VehicleUnloadingRequest
    ): Response<GeneralResponse>

    @POST(Constants.KIOSK_DOCK_LEVELER_UPDATE)
    suspend fun curentDockLevelerUpdate(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        @Body
        currentDockLevelerUpdate: ArrayList<CurrentDockLevelerUpdate>
    ): Response<GeneralResponse>


    @POST(Constants.LOGIN_USER)
    suspend fun loginUser(
        @Body
        loginRequest: LoginRequest,
    ): Response<LoginResponse>

    @GET(Constants.GET_USERID)
    suspend fun getUserIdDetails(
        @Query("RFID") epcCode: String
    ): Response<GetUserIDDetailsResponse>

    @GET(Constants.LIST_OF_DOCK_REPORTING_VEHICLE)
    suspend fun ListOfDockReportingVehicle(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        @Query("KisokIp") kioskIp: String
    ): Response<ArrayList<ListOfDockReportingVehicleResponseItem>>

/*    @GET(Constants.KIOSK_CURRENT_DOCK_QUEUE)
    suspend fun getCurrentDockQueueDetails(
        @Header("Authorization") bearerToken: String,
        @Query("KisokIp") kisokIp: String
    ): Response<DockQueueResponse>*/

    @GET(Constants.KIOSK_CURRENT_PARKING_QUEUE)
    suspend fun getCurrentParkinglistDetails(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        @Query("IPAddress") ipAddress: String
    ): Response<ArrayList<ParkingListResponse>>

    //Home Levelers List
    @GET(Constants.KIOSK_CURRENT_LEVELER_LIST)
    suspend fun getCurrentLevelerListDetails(
     @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
     @Query("KisokIp") ipAddress: String
    ): Response<ArrayList<LevelerListResponse>>

    //Home RGP LIST
    @GET(Constants.KIOSK_CURRENT_RGP_LIST)
    suspend fun getCurrentRGPSListDetails(
     @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
     @Query("KisokIp") kisokIp: String
    ): Response<ArrayList<LevelerListResponse>>

 @GET(Constants.KIOSK_CURRENT_DOCK_LEVELER_LIST)
    suspend fun getCurrentDockLevelerList(
     @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
     @Query("KisokIp") kisokIp: String
    ): Response<ArrayList<CurrentDockLevelerResponse>>

    @GET(Constants.KIOSK_VERIFY_SECURITY)
    suspend fun verifySecurityRFID(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        @Query("RFID") ipAddress: String,
        @Query("CardNo") cardNo:String
    ): Response<GeneralResponse>

    @POST(Constants.CHANGE_PASSWORD)
    suspend fun changePassword(
        @Body
        changePasswordRequest: ChangePasswordRequest,
    ): Response<ChangePasswordResponse>
}