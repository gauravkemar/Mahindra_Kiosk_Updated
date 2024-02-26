package com.kemarport.mahindrakiosk.dockleveler.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kemarport.mahindrakiosk.repository.KioskRepository

class DockLevelerViewModelFactory (
    private val application: Application,
    private val kioskRepository: KioskRepository,
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DockLevelerViewModel(application, kioskRepository) as T
    }
}