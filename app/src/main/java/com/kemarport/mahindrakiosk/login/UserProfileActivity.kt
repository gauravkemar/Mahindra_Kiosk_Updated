package com.kemarport.mahindrakiosk.login

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.kemarport.mahindrakiosk.R
import com.kemarport.mahindrakiosk.databinding.ActivityLoginBinding
import com.kemarport.mahindrakiosk.databinding.ActivityUserProfileBinding
import com.kemarport.mahindrakiosk.databinding.ChangePasswordDialogueBinding
import com.kemarport.mahindrakiosk.databinding.CustomDialogRfidTcpBinding
import com.kemarport.mahindrakiosk.databinding.ManualApkDownloadDialogueBinding
import com.kemarport.mahindrakiosk.helper.Constants
import com.kemarport.mahindrakiosk.helper.Resource
import com.kemarport.mahindrakiosk.helper.SessionManager
import com.kemarport.mahindrakiosk.home.KioskHomeFlippinActivity
import com.kemarport.mahindrakiosk.login.model.changepassword.ChangePasswordRequest
import com.kemarport.mahindrakiosk.login.viewmodel.LoginViewModel
import com.kemarport.mahindrakiosk.login.viewmodel.LoginViewModelFactory
import com.kemarport.mahindrakiosk.repository.KioskRepository
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.HashMap
import java.util.Locale

class UserProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserProfileBinding

    //get values from session
    private lateinit var userDetails: HashMap<String, Any?>
    private lateinit var session: SessionManager
    private var userRfid: String? = ""
    private var shopNo: String? = ""
    private var dockNo: String? = ""
    private var token: String? = ""
    private var userName: String? = ""

    private var firstName: String? = ""
    private var lastName: String? = ""
    private var userEmail: String? = ""
    private var userMobileNumber: String? = ""
    private var userId: String? = ""
    private var roleName: String? = ""
    var coroutineScopeForCurrentTime = CoroutineScope(Dispatchers.Main)

    private lateinit var viewModel: LoginViewModel

    lateinit var changePasswordDialogueBinding: ChangePasswordDialogueBinding
    var changePasswordDialogue: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile)

        changePasswordDialogueBinding = ChangePasswordDialogueBinding.inflate(getLayoutInflater());
        changePasswordDialogue = Dialog(this)
        session = SessionManager(this)
        userDetails = session.getUserDetails()
        shopNo = userDetails[Constants.PARENT_LOCATION_CODE].toString()
        dockNo = userDetails[Constants.LOCATION_NAME].toString()
        token = userDetails[Constants.KEY_JWT_TOKEN].toString()
        userName = userDetails[Constants.KEY_USER_NAME].toString()

        firstName = userDetails[Constants.KEY_USER_FIRST_NAME].toString()
        lastName = userDetails[Constants.KEY_USER_LAST_NAME].toString()
        userEmail = userDetails[Constants.KEY_USER_EMAIL].toString()
        userMobileNumber = userDetails[Constants.KEY_USER_MOBILE_NUMBER].toString()
        userId = userDetails[Constants.USER_ID].toString()
        roleName = userDetails[Constants.ROLE_NAME].toString()

        binding.tvDockNo.setText(dockNo)
        binding.tvShopNo.setText(shopNo)
        startCurrentDateTime()

        binding.tvIdValue.setText(userId)
        binding.tvRoleValue.setText(roleName)
        binding.tvUserNameValue.setText(userName)
        binding.tvFirstNameValue.setText(firstName)
        binding.tvLastNameValue.setText(lastName)
        binding.tvEmailAddressValue.setText(userEmail)
        binding.tvMobileNumberValue.setText(userMobileNumber)

        binding.mcvProfile9.setOnClickListener {
            session.showAlertLogOut(this@UserProfileActivity)
        }
        binding.mcvProfile8.setOnClickListener {
            setDialog()
        }
        val kioskRepository = KioskRepository()
        val viewModelProviderFactory =
            LoginViewModelFactory(application, kioskRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[LoginViewModel::class.java]

        viewModel.changePasswordMutableLiveData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    startLoginActivity()
                    response.data?.let { resultResponse ->
                        var errorMessage = resultResponse.ErrorMessage
                        var responseMessage = resultResponse.ResponseMessage
                        if (errorMessage.isNotEmpty()) {
                            session.sweetAlertFailure(
                                this@UserProfileActivity,
                                errorMessage
                            )

                        } else if (responseMessage.isNotEmpty()) {
                            session.sweetAlertSuccess(
                                this@UserProfileActivity,
                                responseMessage
                            )
                        }
                    }
                }

                is Resource.Error -> {
                    response.message?.let { resultResponse ->
                        session.sweetAlertFailure(
                            this@UserProfileActivity,
                            resultResponse
                        )
                        if (resultResponse == "Unauthorized" || resultResponse == "Authentication token expired" ||
                            resultResponse == Constants.CONFIG_ERROR
                        ) {
                            session.showCustomDialog(
                                "Session Expired",
                                "Please re-login to continue",
                                this@UserProfileActivity
                            )
                        }
                    }
                }

                is Resource.Loading -> {
                    //showProgressBar()
                }
            }
        }

    }

    private fun setDialog() {
        changePasswordDialogue!!.setContentView(changePasswordDialogueBinding.root)
        changePasswordDialogue!!.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this@UserProfileActivity, android.R.color.transparent
            )
        )
        changePasswordDialogue!!.getWindow()?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        changePasswordDialogue!!.setCancelable(true)

        changePasswordDialogueBinding.tvDialogueTitle.setText("Change Password")

        changePasswordDialogueBinding.submitBtn.setOnClickListener {
            showConfirmDialog()
        }

        changePasswordDialogueBinding.clearBtn.setOnClickListener {
            clearBtn()
        }


        changePasswordDialogueBinding.closeDialogueTopButton.setOnClickListener {
            changePasswordDialogue!!.dismiss()
        }
        changePasswordDialogue!!.show()


    }

    private fun showConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setMessage("Are You Sure About this?")
            .setCancelable(true)
            .setPositiveButton("Submit") { dialog, _ ->
                try {
                    changePassSubmit()
                } catch (e: Exception) {
                    Toasty.error(
                        this@UserProfileActivity,
                        "This is exception " + e.toString()
                    ).show()
                }

            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }

    private fun clearBtn() {
        changePasswordDialogueBinding.edNewPassword.setText("")
        changePasswordDialogueBinding.edOldPassword.setText("")
        changePasswordDialogueBinding.edConfirmPassword.setText("")
    }

    private fun changePassSubmit() {
        // Fetching user credentials from input fields
        val edOldPassword = changePasswordDialogueBinding.edOldPassword.text.toString().trim()
        val edNewPassword = changePasswordDialogueBinding.edNewPassword.text.toString().trim()
        val edConfirmPassword =
            changePasswordDialogueBinding.edConfirmPassword.text.toString().trim()

        // Validate user input
        val validationMessage = validateInput(edOldPassword, edNewPassword, edConfirmPassword)
        if (validationMessage == null) {
            userName
                ?.let {
                    ChangePasswordRequest(edConfirmPassword, edOldPassword, edNewPassword, it)
                }
                ?.let {
                    viewModel.changePassword(Constants.baseUrl2, it)
                }

        } else {
            session.sweetAlertFailure(this@UserProfileActivity, "Password Not Changed!!")
        }
    }

    private fun validateInput(currentPass: String, newPass: String, confirmPass: String): String? {
        return when {
            currentPass.isEmpty() -> "Please enter the old password"
            newPass.isEmpty() -> "Please enter the new password"
            confirmPass.isEmpty() -> "Please confirm the new password"
            currentPass.length < 6 -> "Old password should have at least 6 characters"
            newPass.length < 6 -> "New password should have at least 6 characters"
            newPass != confirmPass -> "New password and confirm password do not match"
            else -> null
        }
    }

    private fun startCurrentDateTime() {
        coroutineScopeForCurrentTime.launch {
            try {
                while (isActive) {
                    if (isActive) {
                        binding.tvDateTime.setText(getCurrentDateTime())
                    }
                    delay(1000)
                }
            } catch (e: Exception) {
                Log.e("ErrorFromSetting", e.toString())
            }

        }
    }

    override fun onPause() {
        super.onPause()
        coroutineScopeForCurrentTime.cancel()
    }

    override fun onStop() {
        super.onStop()
        coroutineScopeForCurrentTime.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScopeForCurrentTime.cancel()
    }

    fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startProfileActivity()
    }

    private fun startProfileActivity() {
        startActivity(Intent(this@UserProfileActivity, KioskHomeFlippinActivity::class.java))
        finish()
    }

    private fun startLoginActivity() {
        startActivity(Intent(this@UserProfileActivity, LoginActivity::class.java))
        finish()
    }
}