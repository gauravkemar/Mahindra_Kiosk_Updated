package com.kemarport.mahindrakiosk.login

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kemarport.mahindrakiosk.AdminActivity
import com.kemarport.mahindrakiosk.R
import com.kemarport.mahindrakiosk.databinding.ActivityLoginBinding
import com.kemarport.mahindrakiosk.databinding.ManualApkDownloadDialogueBinding
import com.kemarport.mahindrakiosk.databinding.ManualGetUserIdDialogBinding
import com.kemarport.mahindrakiosk.helper.*
import com.kemarport.mahindrakiosk.home.KioskHomeFlippinActivity
import com.kemarport.mahindrakiosk.login.model.LoginRequest
import com.kemarport.mahindrakiosk.login.viewmodel.LoginViewModel
import com.kemarport.mahindrakiosk.login.viewmodel.LoginViewModelFactory
import com.kemarport.mahindrakiosk.repository.KioskRepository
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.NetworkInterface
import java.net.URL


class LoginActivity : AppCompatActivity(){
    lateinit var binding:ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    //api binding
   // val tcp = TCP()
    private val applicationApp: ApplicationClass by lazy { application as ApplicationClass }

    private lateinit var progress: ProgressDialog
    private lateinit var session: SessionManager
    private lateinit var progressDialog: ProgressDialog
    var isPasswordVisible = false
    var installedVersionCode = 0

    lateinit var manualGetUserIdDialogBinding: ManualGetUserIdDialogBinding
    var getUserIdDialog: Dialog?=null

    lateinit var manualApkDownloadDialogueBinding: ManualApkDownloadDialogueBinding
    var manualApkDownloadDialogue: Dialog?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_login)

        manualGetUserIdDialogBinding= ManualGetUserIdDialogBinding.inflate(getLayoutInflater());
        getUserIdDialog = Dialog(this)

        manualApkDownloadDialogueBinding= ManualApkDownloadDialogueBinding.inflate(getLayoutInflater());
        manualApkDownloadDialogue = Dialog(this)

        manualGetUserIdDialogBinding.tapId.requestFocus()
        hideKeyboard(manualGetUserIdDialogBinding.tapId)
        manualGetUserIdDialogBinding.tapId.inputType = InputType.TYPE_NULL


        session=SessionManager(this)
        val kioskRepository = KioskRepository()
        val viewModelProviderFactory =
            LoginViewModelFactory(application, kioskRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[LoginViewModel::class.java]
        progress = ProgressDialog(this)
        progress.setMessage("Loading...")

        val packageManager = applicationContext.packageManager

        try {
            // Get the package info for the app
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val versionCode = packageInfo.versionCode
            // Retrieve the version code and version name
            val versionName = packageInfo.versionName
            installedVersionCode = versionCode
            // binding.tvBuildNo.setText(installedVersionCode.toString())
            binding.tvAppVersion.setText("V ${versionName}")
            // Log or display the version information

            Log.d("Version", "Version Name: $versionName")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        Log.d("Version", "Version Code: $installedVersionCode")

        binding.buttonPasswordToggle.setOnClickListener {
            if (!isPasswordVisible) {
                binding.editTextPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.buttonPasswordToggle.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_visibility
                    )
                )
                isPasswordVisible = true
                binding.editTextPassword.setSelection( binding.editTextPassword.getText()!!.length)
            } else {
                binding.editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.buttonPasswordToggle.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_visibility_off
                    )
                )
                isPasswordVisible = false
                binding.editTextPassword.setSelection(binding.editTextPassword.getText()!!.length)
            }
        }

//(add another condition in the receivedData such that check if the data is 16 digit)
        applicationApp.receivedData.observe(this, Observer { data ->
            if(data!=null)
            {
                getUserId(data)
            }

        })
        validateKioskIp()
        viewModel.getDockShopDetailsMutableLiveData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    //hideProgressBar()
                    binding.loginSwipeRefresh.isRefreshing = false
                    response.data?.let { resultResponse ->
                        //Toasty.success(this, resultResponse.item1+resultResponse.item2, Toasty.LENGTH_SHORT).show()
                        if(resultResponse.item1!=null && resultResponse.item2!=null)
                        {
                            Utils.setSharedPrefs(this@LoginActivity, Constants.PARENT_LOCATION_CODE, resultResponse.item2)
                            Utils.setSharedPrefs(this@LoginActivity, Constants.LOCATION_NAME, resultResponse.item1)
                            binding.tvDockId.setText("Dock-"+resultResponse.item1)
                            binding.tvShopId.setText(resultResponse.item2)
                        }
                    }
                }
                is Resource.Error -> {
                    binding.loginSwipeRefresh.isRefreshing = false
                    //hideProgressBar()
                    response.message?.let { resultResponse ->
                       // Toasty.error(this, resultResponse, Toasty.LENGTH_SHORT).show()
                        session.sweetAlertFailure(this@LoginActivity,resultResponse)
                    }
                }
                is Resource.Loading -> {
                    //showProgressBar()
                }
            }
        }
        viewModel.getUserIdDetailsMutableList.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                   // hideProgressBar()
                    getUserIdDialog!!.dismiss()
                    manualGetUserIdDialogBinding.tapId.setText("")
                    response.data?.let { resultResponse ->
                        //Toasty.success(this, "sucess"+resultResponse.userName, Toasty.LENGTH_SHORT).show()
                        binding.edittextUsername.setText(resultResponse.userName)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    getUserIdDialog!!.dismiss()
                    manualGetUserIdDialogBinding.tapId.setText("")
                    response.message?.let { resultResponse ->
                        //Toasty.error(this, resultResponse, Toasty.LENGTH_SHORT).show()
                        session.sweetAlertFailure(this@LoginActivity,"Invalid User")
                    }
                }
                is Resource.Loading -> {
                    //showProgressBar()
                }
            }
        }
        binding.loginBtn.setOnClickListener {
            //startActivity()
            sendLoginRequest()
        }
        viewModel.loginUserMutableLiveData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        //session.sweetAlertSuccess(this@LoginActivity,"Logged In")
                        session.createLoginSession(
                            resultResponse.userName,
                            resultResponse.firstName,
                            resultResponse.lastName,
                            resultResponse.email,
                            resultResponse.mobileNumber.toString(),
                            resultResponse.roleName,
                            resultResponse.isVerified.toString(),
                            resultResponse.jwtToken,
                            resultResponse.refreshToken.toString(),
                            resultResponse.userRFID.toString(),
                            resultResponse.id.toString(),
                            resultResponse.cardNo.toString(),
                        )
                        Utils.setSharedPrefsBoolean(this@LoginActivity, Constants.KEY_ISLOGGEDIN, true)
                    }
                    startActivity()
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { resultResponse ->
                        session.sweetAlertFailure(this@LoginActivity,resultResponse)
                    }
                }
                is Resource.Loading -> {
                   // showProgressBar()
                }
            }
        }

        viewModel.getAppDetailsMutableLiveData.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    when (response.data?.statusCode) {
                        Constants.HTTP_OK -> {
                            response.data.responseObject?.let { resultResponse ->
                                try {
                                    Log.e("thisOutsideTry",installedVersionCode.toString()+" % "+resultResponse.apkVersion.toString())
                                    if (resultResponse.apkVersion > installedVersionCode) {
                                        Log.e("thisUpdate",installedVersionCode.toString()+" % "+resultResponse.apkVersion.toString())
                                        /*showUpdateDialog(
                                            resultResponse.apkFileUrl,
                                            resultResponse.isMandatory
                                        )*/
                                        setManualDownload(resultResponse.apkFileUrl)


                                    }
                                } catch (e: Exception) {
                                    session.sweetAlertFailure(this@LoginActivity,
                                        e.toString())
                                }
                            }
                        }
                        Constants.HTTP_ERROR ,Constants.HTTP_NOT_FOUND-> {
                            session.sweetAlertFailure(this@LoginActivity,
                                response.data.errorMessage.toString())

                        }
                        else -> {

                            session.sweetAlertFailure(this@LoginActivity,"Submission Failed, statusCode - ${response.data?.statusCode}")
                       /*     Toasty.error(
                                this,
                                "Submission Failed, statusCode - ${response.data?.statusCode}",
                                Toasty.LENGTH_LONG
                            ).show()*/
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        session.sweetAlertFailure(this@LoginActivity,errorMessage)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

      /*  binding.rfidScan.setOnClickListener {
            setDialog()
        }*/
        binding.clearBtn.setOnClickListener {
            clearBtn()
        }

        binding.loginSwipeRefresh.setOnRefreshListener { validateKioskIp() }
    }


    private fun validateKioskIp()
    {
        try {
            viewModel.getKiosIP(Constants.baseUrl2, getDeviceIPAddress()?:"0.0.0.0")
        }catch (e:Exception)
        {
            session.sweetAlertFailure(this@LoginActivity,e.toString())
        }

        //viewModel.getUserIdDetails(Constants.baseUrl, "EPC123")
    }
    private fun getUserId(data:String)
    {
        try{
            if(data!=null)
            {
                viewModel.getUserIdDetails( Constants.baseUrl2, data)
            }
        }
        catch (e:Exception)
        {
            session.sweetAlertFailure(this@LoginActivity,e.toString())
        }
    }

    override fun onPause() {
        super.onPause()
        applicationApp.clearReceivedData()
    }

    override fun onDestroy() {
        super.onDestroy()
        applicationApp.clearReceivedData()
    }
    private fun startActivity(){
        startActivity(Intent(this@LoginActivity,KioskHomeFlippinActivity::class.java))
        finish()
    }
    private fun sendLoginRequest() {
        try {
            var userName = binding.edittextUsername.text?.trim().toString()
            var  password = binding.editTextPassword.text?.trim().toString()
            var deviceIp = getDeviceIPAddress()?:"0.0.0.0"
            if (userName.isNotEmpty() && password.isNotEmpty() && deviceIp.isNotEmpty()
            ) {
                if(userName.equals("admin") && password.equals("admin@123"))
                {
                    startActivity(Intent(this@LoginActivity,AdminActivity::class.java))
                    finish()
                }
                else
                {
                    viewModel.loginUser( Constants.baseUrl2, LoginRequest(userName,password,deviceIp) )
                }
            } else {
                //Toasty.warning(this@LoginActivity, "Please enter required fields").show()
                session.sweetAlertFailure(this@LoginActivity,"Please enter required fields")
            }
        } catch (e: Exception) {
            Toasty.warning(
                this,
                Constants.EXCEPTION_ERROR,
                Toasty.LENGTH_SHORT
            ).show()
        }
    }

    fun showProgressBar() {
        progress.show()
    }
    fun hideProgressBar() {
        progress.cancel()
    }

    fun clearBtn() {
        binding.edittextUsername.setText("")
        binding.editTextPassword.setText("")
    }

    fun getDeviceIPAddress(): String? {
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address.isSiteLocalAddress) {
                        return address.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun onResume() {
        super.onResume()
        //getApplicationVersionDetails()
    }

    private fun getApplicationVersionDetails(){
        try {
            viewModel.getAppDetails(Constants.baseUrl)
        } catch (e: Exception) {
            session.sweetAlertFailure(this@LoginActivity,e.message.toString())
        }
    }

    private fun showUpdateDialog(appUrl: String, isMandatory: Boolean) {
        if (isMandatory) {
            AlertDialog.Builder(this)
                .setTitle("Update Available")
                .setMessage("A new version of the app is available. Do you want to update?")
                .setCancelable(false)
                .setPositiveButton("Update") { dialog, _ ->
                    try {
                        dialog.dismiss()
                        val destinationFolder =
                            Environment.getExternalStorageDirectory().toString() + "/download/"

                        progressDialog = ProgressDialog(this)
                        progressDialog.setMessage("Downloading...")
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                        progressDialog.setCancelable(false)

                        GlobalScope.launch(Dispatchers.IO) {
                            Log.e("thisUpdateDownloadFile",installedVersionCode.toString())
                            downloadFileNew(appUrl, destinationFolder)
                        }
                    } catch (e: Exception) {
                        Toasty.error(
                            this@LoginActivity,
                            "This is exception " + e.toString()
                        ).show()
                    }
                }
                .show()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Update Available (Optional)")
                .setMessage("A new version of the app is available. Do you want to update?")
                .setCancelable(true)
                .setPositiveButton("Update") { dialog, _ ->
                    try {
                        dialog.dismiss()
                        val destinationFolder =
                            Environment.getExternalStorageDirectory().toString() + "/download/"

                        progressDialog = ProgressDialog(this)
                        progressDialog.setMessage("Downloading...")
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                        progressDialog.setCancelable(false)

                        GlobalScope.launch(Dispatchers.IO) {
                            downloadFileNew(appUrl, destinationFolder)
                        }
                    } catch (e: Exception) {
                        Toasty.error(
                            this@LoginActivity,
                            "This is exception " + e.toString()
                        ).show()
                    }

                }
                .setNegativeButton("Skip") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun downloadFileNew(url: String, destinationFolder: String) {
        try {
            val fileUrl = URL(url)
            val fileName = fileUrl.file.substring(fileUrl.file.lastIndexOf("/") + 1)
            val destinationFile = File(destinationFolder, fileName)
            val outputStream = FileOutputStream(destinationFile)
            val connection = fileUrl.openConnection()
            val contentLength = connection.contentLength
            val contentLengthMB = contentLength / (1000 * 1000) // Convert bytes to megabytes
            val buffer = ByteArray(1024)
            var totalBytesRead = 0
            var bytesRead = connection.getInputStream().read(buffer)
            runOnUiThread {
                progressDialog.max = contentLengthMB
                progressDialog.show()
            }
            while (bytesRead != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
                val totalBytesReadMB = totalBytesRead / (1000 * 1000) // Convert bytes to megabytes
                runOnUiThread { progressDialog.progress = totalBytesReadMB }
                bytesRead = connection.getInputStream().read(buffer)
            }
            outputStream.close()
            runOnUiThread {
                progressDialog.dismiss()
                showMessageDialog("Update downloaded successfully")
                installApkFile(destinationFile)
            }
        } catch (e: Exception) {
            runOnUiThread {
                progressDialog.dismiss()
                showMessageDialog("Error downloading file: ${e.message}")
            }
        }
    }

    private fun installApkFile(apkFile: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val authority = "${applicationContext.packageName}.fileprovider"
            val apkUri = FileProvider.getUriForFile(this, authority, apkFile)
            val installIntent = Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
                data = apkUri
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(installIntent)
        } else {
            val apkUri = Uri.fromFile(apkFile)
            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(installIntent)
        }
    }

    private fun showMessageDialog(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun setDialog(){


        hideKeyboard( manualGetUserIdDialogBinding.tapId)
        getUserIdDialog!!.setContentView(manualGetUserIdDialogBinding.root)

        getUserIdDialog!!.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
            this@LoginActivity, android.R.color.transparent))
        getUserIdDialog!!.getWindow()?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        getUserIdDialog!!.setCancelable(true)
        textAllCapsMaxLength( manualGetUserIdDialogBinding.tapId,24)
        manualGetUserIdDialogBinding.tvDialogueTitle.setText("Login With RFID")
        //manualGetUserIdDialogBinding.edField2.setHint("Please Enter VRN.")
        //manualGetUserIdDialogBinding.edField.setHint("Please Enter ID Card No.")
        hideKeyboard(manualGetUserIdDialogBinding.tapId)
        manualGetUserIdDialogBinding.tapId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // hideKeyboard(manualDockFunctionOptionsDialogueBinding.tapId)
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                val str = s.toString().trim()
                if(checkIfLength24()) { getUserId(str) }
            }
        })
        manualGetUserIdDialogBinding.closeDialogueTopButton.setOnClickListener {
            getUserIdDialog!!.dismiss()
            manualGetUserIdDialogBinding.tapId.setText("")
        }
        getUserIdDialog!!.show()
    }
    private fun setManualDownload(apkLink:String){

        manualApkDownloadDialogue!!.setContentView(manualApkDownloadDialogueBinding.root)
        manualApkDownloadDialogue!!.getWindow()?.setBackgroundDrawable(AppCompatResources.getDrawable(this@LoginActivity, android.R.color.transparent))
        manualApkDownloadDialogue!!.getWindow()?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        manualApkDownloadDialogue!!.setCancelable(true)
        manualApkDownloadDialogueBinding.tvDownloadLink.setText(apkLink)
        manualApkDownloadDialogueBinding.tvDialogueTitle.setText("Download Apk")
        manualApkDownloadDialogueBinding.tvDownloadLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(apkLink))
            startActivity(intent)
        }
        manualApkDownloadDialogueBinding.closeDialogueTopButton.setOnClickListener {
            manualApkDownloadDialogue!!.dismiss()

        }
        manualApkDownloadDialogue!!.show()
    }
    private fun checkIfLength24():Boolean{
        val edFieldScan=manualGetUserIdDialogBinding.tapId.getText().toString().trim()
        return edFieldScan.length==24
    }
    private fun hideKeyboard(editText: EditText) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0)
    }
    fun textAllCapsMaxLength(editText: EditText, length: Int) {
        editText.filters = arrayOf( InputFilter.LengthFilter(length))
    }
}

/*  override fun onPause() {
      super.onPause()

      // Remove the data listener when the activity is paused
      applicationApp.removeDataListener()
      //TCPManager.stopTcpCommunication()
  }
  override fun onDestroy() {
      super.onDestroy()

      applicationApp.removeDataListener()
      //TCPManager.stopTcpCommunication()
      // TCPManager.stopTcpCommunication()
  }*/
/* override fun onDataReceived(data: String) {
     try{
         if(data!=null)
         {
             viewModel.getUserIdDetails( "http://192.168.1.4:5000/api/", data)
         }
     }
     catch (e:Exception)
     {
         Toasty.error(
             this,
             e.toString(),
             Toasty.LENGTH_SHORT
         ).show()
     }

 }*/
/* override fun onDataReceived(data: String) {
     viewModel.getUserIdDetails( "http://192.168.1.57:5000/api/", data)
 }
 fun startTcp() {
     CoroutineScope(Dispatchers.IO).launch {
         tcp.start(this@LoginActivity)
     }
 }
 override fun onDestroy() {
     super.onDestroy()
     tcp.cancelRunningJob()
 }*/