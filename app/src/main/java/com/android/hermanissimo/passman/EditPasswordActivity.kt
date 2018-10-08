package com.android.hermanissimo.passman

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import com.android.hermanissimo.passman.model.PasswordModel
import kotlinx.android.synthetic.main.activity_edit_password.*
import android.content.Intent
import android.view.Menu


class EditPasswordActivity : TemplateActivity()
{

    private var toolbar: Toolbar? = null
    private var passwordModel:PasswordModel?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_password)
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar?.setTitleTextColor(resources.getColor(R.color.white))
        supportActionBar?.setTitle(R.string.title_edit_password)
        if(intent.hasExtra(Constants.IntentKey.PASSWORD))
            passwordModel = intent.getSerializableExtra(Constants.IntentKey.PASSWORD) as PasswordModel
        updateButton.setOnClickListener{onUpdateButtonClicked()}
        if(passwordModel != null)
        {
            labelTextView.setText(passwordModel!!.label)
            usernameTextView.setText(passwordModel!!.username)
            passwordTextView.setText(passwordModel!!.password)
        }
    }

    private fun onDeleteClicked() {
        AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                .setPositiveButton(android.R.string.ok,{ _, _ -> deletePassword()})
                .setNegativeButton(android.R.string.cancel,{ dialogInterface, _ -> dialogInterface.dismiss()})
                .show()
    }

    private fun viewPassword() {
        val nextIntent = Intent(this, ViewPasswordActivity::class.java)
        nextIntent.putExtra(Constants.IntentKey.PASSWORD, passwordModel!!)
        nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(nextIntent)
        finish()
    }

    private fun returnToMainActivity() {
        val nextIntent = Intent(this,MainActivity::class.java)
        nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(nextIntent)
    }

    private fun deletePassword() {
        passwordManager!!.deletePassword(passwordModel!!, ::returnToMainActivity)
    }

    private fun onUpdateButtonClicked() {
        if (isFieldsValid()) {
            if (passwordModel != null) {
                passwordModel!!.label = labelTextView.text.toString()
                passwordModel!!.username = usernameTextView.text.toString()
                passwordModel!!.password = passwordTextView.text.toString()
                passwordModel!!.length = passwordTextView.text.toString().length
                passwordManager!!.addOrUpdatePassword(passwordModel!!, ::viewPassword)
            } else {
                passwordModel = PasswordModel(
                        System.currentTimeMillis().toString(),
                        labelTextView.text.toString(),
                        usernameTextView.text.toString(),
                        passwordTextView.text.toString(),
                        passwordTextView.text.toString().length)
                passwordManager!!.addOrUpdatePassword(passwordModel!!, ::returnToMainActivity)
            }
        }
    }

    private fun isFieldsValid() : Boolean {
        when {
            labelTextView.text.toString().isNullOrEmpty() -> {
                Toast.makeText(this,R.string.error_label_empty,Toast.LENGTH_SHORT).show()
                return false
            }
            usernameTextView.text.toString().isNullOrEmpty() -> {
                Toast.makeText(this,R.string.error_username_empty,Toast.LENGTH_SHORT).show()
                return false
            }
            passwordTextView.text.toString().isNullOrEmpty() -> {
                Toast.makeText(this,R.string.error_password_empty,Toast.LENGTH_SHORT).show()
                return false
            }
            else -> return true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (passwordModel != null) {
            menuInflater.inflate(R.menu.menu_delete, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.action_delete -> {
                onDeleteClicked()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

}
