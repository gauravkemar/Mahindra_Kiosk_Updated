package com.kemarport.mahindrakiosk.login.model

class LoginResponse (
    val email: String?,
    val firstName: String?,
    val isVerified: Boolean?,
    val jwtToken: String?,
    val lastName: String?,
    val menuAccess: List<Any>?,
    val mobileNumber: Any?,
    val refreshToken: Any?,
    val roleName: String?,
    val userAccess: List<Any>?,
    val userName: String?,
    val userRFID:String?,
    val id:Int?,
    val cardNo:String?
)