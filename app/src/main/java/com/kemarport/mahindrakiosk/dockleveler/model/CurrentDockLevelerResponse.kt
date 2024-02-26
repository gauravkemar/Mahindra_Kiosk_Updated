package com.kemarport.mahindrakiosk.dockleveler.model

data class CurrentDockLevelerResponse(
    var isActive: Boolean,
    val levelerId: Int,
    val levelerNumber: String
)