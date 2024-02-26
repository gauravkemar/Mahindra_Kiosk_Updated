package com.kemarport.mahindrakiosk.home.model.unloading

data class VehicleUnloadingRequest(
    /*val JobMilestoneId: Int?,
    val KioskIp: String?,
    val LevelerId: String?,
    val MaterialName: String?,
    val MilestoneAction: String?,
    val MilestoneId: String?,
    val ProceedFor: String?,
    val Quantity: String?,
    val SecurityId: Int?,
    val SupervisorId: Int?,
    val UserId: String?,
    val VrnRFID: String?*/
    val KioskIp: String?,
    val LevelerId: Any?,
    val MilestoneAction: String?,
    val MilestoneId: Any?,
    val ProceedFor: Any?,
    val RgpTransactionRequest: List<RGPTransactionRequest>?,
    val SecurityId: Int?,
    val SupervisorId: Int?,
    val UserId: Int?,
    val Vrn: String?,
    val isRGP: Boolean?,
    val IsUpdate: Boolean?,
    val ReceivedQuantityType:String?,
    val OverTimeRegion:String?,
    val IsSafetyCompliance:Boolean?,
    val IsUnloadedbyMathadi:Boolean?,
)