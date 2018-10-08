package com.android.hermanissimo.passman

import android.app.KeyguardManager
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity


open class TemplateActivity : AppCompatActivity() {

    protected val REQUESTCODE = 7777

    protected var passwordManager: PasswordManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        passwordManager = (application as PassManApplication).passwordManager
    }
}
