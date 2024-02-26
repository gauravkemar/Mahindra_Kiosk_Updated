package com.kemarport.mahindrakiosk.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.text.Html
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.kemarport.mahindrakiosk.home.KioskHomeFlippinActivity
import com.kemarport.mahindrakiosk.login.LoginActivity

class SessionManager(context: Context) {
    // Shared Preferences
    var sharedPrefer: SharedPreferences

    // Editor for Shared preferences
    var editor: SharedPreferences.Editor

    // Context
    var context: Context

    // Shared Pref mode
    var PRIVATE_MODE = 0

    // Constructor
    init {
        this.context = context
        sharedPrefer = context.getSharedPreferences(Constants.SHARED_PREF, PRIVATE_MODE)
        editor = sharedPrefer.edit()
    }
    fun logoutUser() {
        editor.putBoolean(Constants.KEY_ISLOGGEDIN, false)
        editor.commit()
    }

    /**
     * Call this method on/after login to store the details in session
     */
    /*fun createLoginSession(
        userId: String?,
        userName: String?,
        rdtId: String?,
        terminal: String?,
        jwtToken: String?,
        refreshToken: String?
    ) {

        editor.putString(Constants.USER_ID, userId)
        editor.putString(Constants.USERNAME, userName)
        editor.putString(Constants.RDT_ID, rdtId)
        editor.putString(Constants.KEY_TERMINAL, terminal)
        //editor.putBoolean(Constants.KEY_ISLOGGEDIN, true)
        editor.putString(Constants.KEY_JWT_TOKEN, jwtToken)
        editor.putString(Constants.KEY_REFRESH_TOKEN, refreshToken)

        // commit changes
        editor.commit()
    }

    fun logoutUser() {
        editor.putBoolean(Constants.KEY_ISLOGGEDIN, false)
        editor.commit()
    }*/

    /**
     * Call this method anywhere in the project to Get the stored session data
     */
 /*   fun getUserDetails(): HashMap<String, String?> {
        val user = HashMap<String, String?>()
        user["userId"] = sharedPrefer.getString(Constants.USER_ID, null)
        user["userName"] = sharedPrefer.getString(Constants.USERNAME, null)
        user["rdtId"] = sharedPrefer.getString(Constants.RDT_ID, null)
        user["terminal"] = sharedPrefer.getString(Constants.KEY_TERMINAL, null)
        user["jwtToken"] = sharedPrefer.getString(Constants.KEY_JWT_TOKEN, null)
        user["refreshToken"] = sharedPrefer.getString(Constants.KEY_REFRESH_TOKEN, null)
        return user
    }

    fun isAlreadyLoggedIn(): HashMap<String, Boolean> {
        val user = HashMap<String, Boolean>()
        user["isLoggedIn"] = sharedPrefer.getBoolean(Constants.KEY_ISLOGGEDIN, false)
        return user
    }

    fun getAdminDetails(): HashMap<String, String?> {
        val admin = HashMap<String, String?>()
        admin["serverIp"] = sharedPrefer.getString(Constants.SERVER_IP, null)
        admin["port"] = sharedPrefer.getString(Constants.PORT, null)
        return admin
    }
    fun getHeaderDetails(): HashMap<String, String?> {
        val user_header = HashMap<String, String?>()
        user_header["UserId"] = sharedPrefer.getString(Constants.USER_ID, null)
        user_header["RDTId"] = sharedPrefer.getString(Constants.RDT_ID, null)
        user_header["TerminalId"] = sharedPrefer.getString(Constants.KEY_TERMINAL, null)
        user_header["Token"] = sharedPrefer.getString(Constants.KEY_JWT_TOKEN, null)
        return user_header
    }

    fun saveAdminDetails(serverIp: String?, portNumber: String?) {
        editor.putString(Constants.SERVER_IP, serverIp)
        editor.putString(Constants.PORT, portNumber)
        editor.putBoolean(Constants.KEY_ISLOGGEDIN, false)
        editor.commit()
    }*/
    fun createLoginSession(
        userName: String?,
        firstName: String?,
        lastName: String?,
        email: String?,
        mobileNumber: String?,
        roleName:String?,
        isVerified: String?,
        jwtToken: String?,
        refreshToken: String?,
        userRFID:String?,
        id:String?,
        CardNo:String?
    ) {

        //editor.putString(KEY_USERID, userId)
        editor.putString(Constants.KEY_USER_NAME, userName)
        editor.putString(Constants.KEY_USER_FIRST_NAME, firstName)
        editor.putString(Constants.KEY_USER_LAST_NAME, lastName)
        editor.putString(Constants.KEY_USER_EMAIL, email)
        editor.putString(Constants.KEY_USER_MOBILE_NUMBER, mobileNumber)
        editor.putString(Constants.KEY_USER_IS_VERIFIED, isVerified)
        editor.putString(Constants.USER_RFID, userRFID)
        editor.putString(Constants.USER_ID, id)
        editor.putString(Constants.CARD_NO, CardNo)



        //editor.putString(KEY_RDT_ID, rdtId)
        //editor.putString(KEY_TERMINAL, terminal)
        //editor.putBoolean(KEY_ISLOGGEDIN, true)
        editor.putString(Constants.KEY_JWT_TOKEN, jwtToken)
        editor.putString(Constants.KEY_REFRESH_TOKEN, refreshToken)
        editor.putString(Constants.ROLE_NAME, roleName)

        // commit changes
        editor.commit()
    }
    fun getUserDetails(): HashMap<String, Any?> {
        val user = HashMap<String, Any?>()
        user[Constants.KEY_USER_NAME] = sharedPrefer.getString(Constants.KEY_USER_NAME, null)
        user[Constants.KEY_JWT_TOKEN] = sharedPrefer.getString(Constants.KEY_JWT_TOKEN, null)
        user[Constants.ROLE_NAME] = sharedPrefer.getString(Constants.ROLE_NAME, null)
        user[Constants.PARENT_LOCATION_CODE] = sharedPrefer.getString(Constants.PARENT_LOCATION_CODE, null)
        user[Constants.LOCATION_NAME] = sharedPrefer.getString(Constants.LOCATION_NAME, null)
        user[Constants.USER_RFID] = sharedPrefer.getString(Constants.USER_RFID, null)
        user[Constants.USER_ID] = sharedPrefer.getString(Constants.USER_ID, null)
        user[Constants.SET_BASE_URL] = sharedPrefer.getString(Constants.SET_BASE_URL, null)
        user[Constants.BASE_URL_TYPE] = sharedPrefer.getBoolean(Constants.BASE_URL_TYPE, false)


        user[Constants.KEY_USER_FIRST_NAME] = sharedPrefer.getString(Constants.KEY_USER_FIRST_NAME, null)
        user[Constants.KEY_USER_LAST_NAME] = sharedPrefer.getString(Constants.KEY_USER_LAST_NAME, null)
        user[Constants.KEY_USER_EMAIL] = sharedPrefer.getString(Constants.KEY_USER_EMAIL, null)
        user[Constants.KEY_USER_MOBILE_NUMBER] = sharedPrefer.getString(Constants.KEY_USER_MOBILE_NUMBER, null)
        return user
    }/* fun getUserDetails(): HashMap<String, String?> {
        val user = HashMap<String, String?>()
        user["userName"] = sharedPrefer.getString(Constants.KEY_USER_NAME, null)
        user["jwtToken"] = sharedPrefer.getString(Constants.KEY_JWT_TOKEN, null)
        user["roleName"] = sharedPrefer.getString(Constants.ROLE_NAME, null)
        user["parentLocationCode"] = sharedPrefer.getString(Constants.PARENT_LOCATION_CODE, null)
        user["locationName"] = sharedPrefer.getString(Constants.LOCATION_NAME, null)
        user["userRFID"] = sharedPrefer.getString(Constants.USER_RFID, null)
        user["id"] = sharedPrefer.getString(Constants.USER_ID, null)

        return user
    }*/
    fun clearSharedPrefs() {
        editor.clear()
        editor.commit()
    }
    fun setSharedPrefsBoolean(context: Context, key: String?, value: Boolean) {
        val pref = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun sweetAlertSuccess(context: Context,value: String) {
        val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
        sweetAlertDialog.titleText = "Successful"
        sweetAlertDialog.contentText = value
        sweetAlertDialog.setCancelable(false)

        sweetAlertDialog.show()
        Handler().postDelayed({
            sweetAlertDialog.dismissWithAnimation()
        }, 3000)
    }

    fun sweetAlertFailure(context: Context,value: String) {
        val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
        sweetAlertDialog.titleText = "Failed"
        sweetAlertDialog.contentText = value
        sweetAlertDialog.setCancelable(false)

        sweetAlertDialog.show()
        Handler().postDelayed({
            sweetAlertDialog.dismissWithAnimation()
        }, 3000)
    }

    fun sweetAlertSuccessNavigateToMainActivity(context: Activity,value: String) {
        val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)

        sweetAlertDialog.titleText = "Successful"
        sweetAlertDialog.contentText = value
        sweetAlertDialog.setCancelable(false)

        sweetAlertDialog.show()
        Handler().postDelayed({
            startMainActivity(context)
            sweetAlertDialog.dismissWithAnimation()
        }, 3000)
    }
    fun showAlertLogOut(context: Activity) {
        val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)

        sweetAlertDialog.titleText = "Logout"
        sweetAlertDialog.contentText = "Are you sure you want to logout?"
        sweetAlertDialog.cancelText = "Cancel"
        sweetAlertDialog.confirmText = "Yes"

        sweetAlertDialog.setCancelable(true)
        sweetAlertDialog.setCancelClickListener {
            // Handle cancel button click here
            sweetAlertDialog.dismiss()
        }

        sweetAlertDialog.setConfirmClickListener {
            logoutUser()
            startLoginActivity(context)
            sweetAlertDialog.dismiss()
        }

        sweetAlertDialog.show()
    }

    fun showCustomDialog(title: String?, message: String?,context: Activity) {
        var alertDialog: AlertDialog? = null
        val builder: AlertDialog.Builder
        if (title.equals(""))
            builder = AlertDialog.Builder(context)
                .setMessage(Html.fromHtml(message))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Okay") { dialogInterface, which ->
                    alertDialog?.dismiss()
                }
        else if (message.equals(""))
            builder = AlertDialog.Builder(context)
                .setTitle(title)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Okay") { dialogInterface, which ->
                    alertDialog?.dismiss()
                }
        else
            builder = AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Okay") { dialogInterface, which ->
                    if (title.equals("Session Expired")) {
                        logout(context)
                    } else {
                        alertDialog?.dismiss()
                    }
                }
        alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    private fun logout(context: Activity) {
        logoutUser()
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        context.finish()
    }
    private fun startMainActivity(context: Activity) {
        val intent = Intent(context, KioskHomeFlippinActivity::class.java)
        context.startActivity(intent)
        context.finish()
    }
  private fun startLoginActivity(context: Activity) {
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        context.finish()
    }

/*    companion object {
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
        const val LOCATION_NAME = "locationName"
        const val PARENT_LOCATION_CODE = "parentLocationCode"

        // const val KEY_RDT_ID = Constants.RDT_ID
        //const val KEY_TERMINAL = Constants.TERMINAL_ID
        const val KEY_ISLOGGEDIN = "isLoggedIn"
        const val KEY_JWT_TOKEN = "jwtToken"
        const val KEY_REFRESH_TOKEN = "refreshToken"
        //Admin Shared Prefs
        const val KEY_SERVER_IP = "serverIp"
        const val KEY_PORT = "port"
    }*/
}