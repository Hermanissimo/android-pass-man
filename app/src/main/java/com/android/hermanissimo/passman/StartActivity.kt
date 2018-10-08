package com.android.hermanissimo.passman

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.preference.PreferenceManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : TemplateActivity() {

    private var toolbar: Toolbar? = null
    private val MY_PERMISSIONS_REQUEST_STORAGE = 12

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar?.setTitleTextColor(resources.getColor(R.color.white))
        toolbar?.setTitle(R.string.title_login)
        login.setOnClickListener{onLoginButtonClicked()}

        if(PreferenceManager.getDefaultSharedPreferences(this).contains(Constants.SharedPrefs.MASTER_PASSWORD_HASH)) {
            passwordLayout.hint = SpannableStringBuilder(resources?.getString(R.string.type_master_password))
            passwordLayout.invalidate()
            confirmPassword.visibility = View.GONE
        }
        checkPermissions()
    }

    fun onLoginButtonClicked() {
        if(PreferenceManager.getDefaultSharedPreferences(this).contains(Constants.SharedPrefs.MASTER_PASSWORD_HASH)) {
            if(passwordManager!!.checkMasterPassword(password.text.toString()))
            {
                startActivity(Intent(this, MainActivity::class.java))
                return
            }
            Toast.makeText(this, R.string.error_password_wrong, Toast.LENGTH_SHORT).show()
        }
        else {
            if(confirmPassword.text.toString() == password.text.toString())
            {
                passwordManager!!.setMasterPassword(password.text.toString())
                startActivity(Intent(this, MainActivity::class.java))
                return
            }
            Toast.makeText(this,R.string.error_password_mismatch,Toast.LENGTH_LONG).show()
        }

    }

    fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
            {

            }
            else
            {
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),MY_PERMISSIONS_REQUEST_STORAGE);
            }
        }
    }

    override fun onResume() {
        super.onResume()
        passwordManager!!.clearMasterPassword()
        password.setText("")
        confirmPassword.setText("")

        if(PreferenceManager.getDefaultSharedPreferences(this).contains(Constants.SharedPrefs.MASTER_PASSWORD_HASH)) {
            passwordLayout.hint = SpannableStringBuilder(resources?.getString(R.string.type_master_password))
            passwordLayout.invalidate()
            confirmPassword.visibility = View.GONE
        }
    }
}
