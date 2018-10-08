package com.android.hermanissimo.passman

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.android.hermanissimo.passman.model.PasswordModel
import kotlinx.android.synthetic.main.activity_view_password.*


class ViewPasswordActivity : TemplateActivity(), View.OnClickListener {

    private var toolbar: Toolbar? = null
    private var passwordModel: PasswordModel?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_password)
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar?.setTitleTextColor(resources.getColor(R.color.white))
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.title_view_password)

        if (intent.hasExtra(Constants.IntentKey.PASSWORD))
            passwordModel = intent.getSerializableExtra(Constants.IntentKey.PASSWORD) as PasswordModel

        if (passwordModel != null)
        {
            if (!passwordModel!!.label.isEmpty()) {
                supportActionBar?.title = passwordModel!!.label
            }
            passwordTextView.text = passwordModel!!.password
            usernameTextView.text = passwordModel!!.username

            passwordTextView.setOnClickListener(this)
            usernameTextView.setOnClickListener(this)
        }

        editButton.setOnClickListener { onEditClicked() }
    }

    private fun returnToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun deletePassword() {
        passwordManager!!.deletePassword(passwordModel!!, ::returnToMainActivity)
    }

    private fun onEditClicked() {
        val nextIntent = Intent(this, EditPasswordActivity::class.java)
        nextIntent.putExtra(Constants.IntentKey.PASSWORD, passwordModel)
        startActivity(nextIntent)
    }

    private fun onDeleteClicked() {
        AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                .setPositiveButton(android.R.string.ok,{ _, _ -> deletePassword()})
                .setNegativeButton(android.R.string.cancel,{ dialogInterface, _ -> dialogInterface.dismiss()})
                .show()
    }

    override fun onClick(p0: View?) {
        if (p0 is TextView) {
            (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip = ClipData.newPlainText("password", p0.text.toString())
            Toast.makeText(this, getString(R.string.copied_to_clipboard,p0.text.toString()), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_delete, menu)
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

    override fun onBackPressed() {
        val nextIntent = Intent(this, MainActivity::class.java)
        nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(nextIntent)
    }

}