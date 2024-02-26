package com.kemarport.mahindrakiosk.home.model.unloading

data class RGPTransactionRequest(
    val JobMilestoneId: Int,
    val MaterialName: String,
    val Quantity: String
)