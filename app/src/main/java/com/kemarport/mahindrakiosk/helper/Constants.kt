package com.kemarport.mahindrakiosk.helper

object Constants {


    const val SHARED_PREF = "shared_pref"
    const val ANTENNA_POWER = "antenna_power"
    const val SERVER_IP = "server_ip"
    const val SERVER_IP_2 = "server_ip_2"
    const val ISFIRSTTIME = "is_first_time"
    const val PORT = "port"
    const val PORT_2 = "port_2"

    const val SERVER_IP_SHARED = "192.168.1.205"
    const val DEFAULT_PORT = 5500
    const val GET = 1
    const val POST = 2
    const val HTTP_OK = 200
    const val HTTP_CREATED = 201
    const val HTTP_EXCEPTION = 202
    const val HTTP_FOUND = 302
    const val HTTP_NOT_FOUND = 404
    const val HTTP_CONFLICT = 409
    const val HTTP_INTERNAL_SERVER_ERROR = 500
    const val HTTP_ERROR = 400

    const val NO_INTERNET = "No Internet Connection"
    const val NETWORK_FAILURE = "Network Failure"
    const val CONFIG_ERROR = "Please configure network details"
    const val INCOMPLETE_DETAILS = "Please fill the required details"
    const val EXCEPTION_ERROR="No Data Found"
    const val SHARED_BASE_URL = "base_url"
    const val SHARED_BASE_URL_ALT = "base_url_alt"
    var BASE_URL = ""
    var BASE_URL_2 = ""
    const val IS_CONFIGURED = "isConfigured"
    const val IS_VERIFIED = "isVerified"
    const val IS_REGISTERED = "isRegistered"

    //public static final int GET_CLOTH_BY_RES_ID = 9;
    //public static final int GET_EXISTING_PLATE_CLOTH_MAPPING = 10;
    const val KEY_ISLOGGEDIN = "isLoggedIn"
    const val KEY_JWT_TOKEN = "jwtToken"
    const val KEY_REFRESH_TOKEN = "refreshToken"
    const val HTTP_ERROR_MESSAGE="message"
    const val HTTP_HEADER_AUTHORIZATION="Authorization"



    const val SET_BASE_URL = "base_url"
    const val BASE_URL_TYPE = "base_url_type"
    const val LOGGEDIN = "loggedIn"
    const val LOCATION_NAME = "locationName"
    const val GET_APP_DETAILS = "MobileApp/GetLatestApkVersion"
    const val LOGIN_URL = "login/login"


    const val baseUrldemo = "http://192.168.1.7:5000/api/"
/*    const val baseUrl = "http://urgetruckchakan.mahindra.com/service/api/"
    const val baseUrl2 = "http://urgetruckchakan.mahindra.com/service/api/"*/
 /*   const val baseUrl = "http://192.168.1.205:7300/Service/api/"
    const val baseUrl2 = "http://192.168.1.205:7300/Service/api/"*/
   const val baseUrl = "http://192.168.1.205:7300/Service/api/"
    const val baseUrl2 = "http://192.168.1.205:7300/Service/api/"
 /*   const val baseUrl = "http://192.168.1.35:5000/api/"
    const val baseUrl2 = "http://192.168.1.35:5000/api/"*/


   /* const val baseUrl = "http://10.192.68.36:9090/Service/api/"
    const val baseUrl2 = "http://10.192.68.36:9090/Service/api/"*/


/*    const val baseUrl = "http://192.168.1.33:5000/api/"
    const val baseUrl2 = "http://192.168.1.33:5000/api/"*/

/*      const val baseUrl = "http://10.192.68.36/Service/api/"
    const val baseUrl2 = "http://10.192.68.36/Service/api/"*/

    const val DOCK_REPORTING_CHECK = "Kiosk/DockReporting"
    const val KIOSK_IP = "Kiosk/GetLocationByKioskIP"
    const val LIST_OF_DOCK_REPORTING_VEHICLE = "Kiosk/ListOfDockReportingVehicle"
    const val GET_USERID = "UserManagement/GetUserId"
    const val LOGIN_USER = "UserLogin/LoginUser"
    const val  KIOSK_CURRENT_PARKING_QUEUE= "Kiosk/GetParkingVehicle"
    const val  KIOSK_VERIFY_SECURITY= "Kiosk/VerifySecurity"
    const val  KIOSK_CURRENT_LEVELER_LIST= "Kiosk/UnloadingLevelerList"
    const val  KIOSK_CURRENT_RGP_LIST= "Kiosk/PendingRGPVehicleList"
    const val  KIOSK_CURRENT_DOCK_LEVELER_LIST= "Kiosk/ListOfLevelerByKioskIp"
    const val  KIOSK_UNLOADING_BEGIN= "Kiosk/Unloading"
    const val  KIOSK_DOCK_LEVELER_UPDATE= "Kiosk/LevelerUpdate"
    const val  CHANGE_PASSWORD= "change-password"

    private const val PREF_NAME = "shared_pref"
    //const val KEY_USERID = Constants.USER_ID
    const val KEY_USER_NAME = "userName"
    const val KEY_USER_FIRST_NAME = "firstName"
    const val KEY_USER_LAST_NAME= "lastName"
    const val KEY_USER_EMAIL = "email"
    const val KEY_USER_MOBILE_NUMBER = "mobileNumber"
    const val KEY_USER_IS_VERIFIED = "isVerified"
    const val USER_RFID = "userRFID"
    const val USER_ID = "id"
    const val CARD_NO = "CardNo"
    const val COORDINATES = "coordinates"
    const val DEALER_CODE = "dealerCode"
    const val ROLE_NAME = "roleName"
    const val PARENT_LOCATION_CODE = "parentLocationCode"

    // const val KEY_RDT_ID = Constants.RDT_ID
    //const val KEY_TERMINAL = Constants.TERMINAL_ID

    //Admin Shared Prefs
    const val KEY_SERVER_IP = "serverIp"
    const val KEY_PORT = "port"


}