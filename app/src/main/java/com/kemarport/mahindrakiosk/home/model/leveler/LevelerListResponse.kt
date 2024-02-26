package com.kemarport.mahindrakiosk.home.model.leveler


data class LevelerListResponse(
/*    val expectedMaterialUnloadingTime: Any,
    val leverlerId: Int,
    val leverlerName: String?,
    val SupplierName: String?,
    val totalUnloadingTime: Int?,
    val unloadingStartTime: String?,
    val Vrn: String?,
    val jobmilestoneId:String?,
    val  isLevelerFree: Boolean?,
    val  channelType: String?,
    val receivedQuantityType:String?*/
    val channelType: String?,
    val expectedLevelerTime: String?,
    val expectedMaterialUnloadingTime: Int?,
    val isLevelerFree: Boolean?,
    val isOverTime: Boolean?,
    val jobmilestoneId: String?,
    val leverlerId: Int?,
    val leverlerName: String?,
    val overTimeRegion: String?,
    val receivedQuantityType: String?,
    val supplierName: String?,
    val totalUnloadingTime: Int?,
    val unloadingStartTime: String?,
    val vrn: String?

)