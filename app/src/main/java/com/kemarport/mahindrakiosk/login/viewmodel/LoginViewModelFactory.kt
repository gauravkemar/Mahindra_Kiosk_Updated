package com.kemarport.mahindrakiosk.login.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kemarport.mahindrakiosk.dockleveler.viewmodel.DockLevelerViewModel
import com.kemarport.mahindrakiosk.repository.KioskRepository

class LoginViewModelFactory (
    private val application: Application,
    private val kioskRepository: KioskRepository,
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(application, kioskRepository) as T
    }
}