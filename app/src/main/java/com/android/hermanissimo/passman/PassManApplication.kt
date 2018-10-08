package com.android.hermanissimo.passman

import android.app.Application

class PassManApplication : Application() {

    var passwordManager:PasswordManager?=null

    override fun onCreate() {
        super.onCreate()
        passwordManager = PasswordManager(this)
    }
}
