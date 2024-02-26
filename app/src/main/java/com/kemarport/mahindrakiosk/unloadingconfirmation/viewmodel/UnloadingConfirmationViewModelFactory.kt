package com.kemarport.mahindrakiosk.unloadingconfirmation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kemarport.mahindrakiosk.dockleveler.viewmodel.DockLevelerViewModel
import com.kemarport.mahindrakiosk.repository.KioskRepository

class UnloadingConfirmationViewModelFactory(
    private val application: Application,
    private val kioskRepository: KioskRepository,
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UnloadingConfirmationViewModel(application, kioskRepository) as T
    }
}