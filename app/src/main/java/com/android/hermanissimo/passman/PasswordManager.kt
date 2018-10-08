package com.android.hermanissimo.passman

import android.os.AsyncTask
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import com.android.hermanissimo.passman.model.PasswordModel
import com.android.hermanissimo.passman.model.PasswordList
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.OutputStreamWriter
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class PasswordManager(var context: android.content.Context) {

    var passwordList: PasswordList?=null

    private var masterPassword: String?=null

    private fun getSHA256Hash(encTarget: String): String {
        var mdEnc: MessageDigest? = null
        try {
            mdEnc = MessageDigest.getInstance("SHA-256")
        } catch (e: NoSuchAlgorithmException) {
        }

        if(mdEnc == null)
            return ""

        mdEnc.update(encTarget.toByteArray(), 0, encTarget.length)
        var md5 = BigInteger(1, mdEnc.digest()).toString(34)
        while (md5.length < 24) {
            md5 += "0"
        }
        return md5
    }

    fun clearMasterPassword() {
        masterPassword = ""
    }

    fun setMasterPassword(password: String) {
        masterPassword = password
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Constants.SharedPrefs.MASTER_PASSWORD_HASH,getSHA256Hash(password).substring(2)).apply()
    }

    fun checkMasterPassword(password: String): Boolean {
        val md5Password = getSHA256Hash(password).substring(2)
        if(PreferenceManager.getDefaultSharedPreferences(context)?.getString(Constants.SharedPrefs.MASTER_PASSWORD_HASH,"").equals(md5Password)) {
            masterPassword = password
            return true
        }
        return false
    }

    fun loadPasswordList(callback: ()-> Unit) {
        class GetPaswordListTask: AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+Constants.Misc.LOCAL_FILENAME)

                if (file.exists()) {
                    val reader = JsonReader(FileReader(file))
                    passwordList = Gson().fromJson<PasswordList>(reader, PasswordList::class.java)
                    if (passwordList == null)
                        passwordList = PasswordList(HashMap())
                    return null
                } else {
                    passwordList = PasswordList(HashMap())
                    return null
                }
            }

            override fun onPostExecute(result: Void?) {
                callback()
            }
        }

        if(passwordList == null)
            GetPaswordListTask().execute()
        else
            callback()

    }

    inner class PutPasswordListTask(private var callback:()->Unit): AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg p0: Void?): Boolean {
            try {
                val passwordFileString = Gson().toJson(passwordList)
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+Constants.Misc.LOCAL_FILENAME)
                file.createNewFile()
                val fOut = FileOutputStream(file)
                val outWriter = OutputStreamWriter(fOut)
                outWriter.append(passwordFileString)
                outWriter.close()
                fOut.close()
                return true

            } catch (e:Exception) {
                Log.e("PutPasswordListTask", e.toString())
            }
            return false
        }

        override fun onPostExecute(result: Boolean) {
            if(result) {
                Toast.makeText(context, R.string.toast_save_success, Toast.LENGTH_LONG).show()
                callback()
            }
            else
                Toast.makeText(context,R.string.toast_save_error,Toast.LENGTH_LONG).show()
        }
    }

    fun addOrUpdatePassword(passwordModel: PasswordModel, callback: () -> Unit) {
        fun addOrUpdatePasswordCallback()
        {
            passwordList!!.passwords[passwordModel.id] = passwordModel
            PutPasswordListTask(callback).execute()
        }
        loadPasswordList(::addOrUpdatePasswordCallback)
    }

    fun deletePassword(passwordModel: PasswordModel, callback: () -> Unit) {
        fun deletePasswordCallback() {
            if(passwordList!!.passwords.containsKey(passwordModel.id))
            {
                passwordList!!.passwords.remove(passwordModel.id)

                PutPasswordListTask(callback).execute()
            }
        }
        loadPasswordList(::deletePasswordCallback)
    }
}