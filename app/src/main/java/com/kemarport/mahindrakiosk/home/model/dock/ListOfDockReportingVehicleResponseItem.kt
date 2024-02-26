package com.kemarport.mahindrakiosk.home.model.dock

data class ListOfDockReportingVehicleResponseItem(
    val dockReportingTime: String?,
    val isGreenChannel: Boolean?,
    val listofLevelerResponse: List<ListofLevelerResponse>?,
    val pendingDock: List<PendingDock>?,
    val supplier: String?,
    val vrn: String?,
    val rfid: String?,
    val parkingOut: String?,
)