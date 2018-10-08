package com.android.hermanissimo.passman.model

import java.io.Serializable

data class PasswordModel (var id: String, var label:String, var username:String, var password:String, var length:Int) : Serializable

