package com.android.hermanissimo.passman

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import com.android.hermanissimo.passman.model.PasswordModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.Toolbar

class MainActivity: TemplateActivity(), PasswordAdapter.OnItemClickListener, TextWatcher {

    private var adapter: PasswordAdapter?=null
    private var toolbar: Toolbar? = null
    private var nextIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar?.setTitleTextColor(resources.getColor(R.color.white))
        toolbar?.title = resources?.getString(R.string.app_name)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        searchBox.addTextChangedListener(this)
        addPassword.setOnClickListener{startActivity(Intent(this, EditPasswordActivity::class.java))}
        loadPasswordList()
    }

    override fun afterTextChanged(s: Editable?) {
        adapter?.filter?.filter(s)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun onLongClick(viewHolder: PasswordAdapter.ViewHolder, item: PasswordModel, position: Int) {
        nextIntent = Intent(this, EditPasswordActivity::class.java)
        nextIntent?.putExtra(Constants.IntentKey.PASSWORD,item)
        startActivity(nextIntent)
    }

    override fun onClick(viewHolder: PasswordAdapter.ViewHolder, item: PasswordModel, position: Int) {
        nextIntent = Intent(this, ViewPasswordActivity::class.java)
        nextIntent?.putExtra(Constants.IntentKey.PASSWORD,item)
        startActivity(nextIntent)
    }

    private fun loadPasswordList() {
        fun callback()
        {
            adapter = PasswordAdapter(ArrayList(passwordManager!!.passwordList!!.passwords.values),this)
            recyclerView.adapter = adapter
            adapter?.onItemClickListener = this
        }
        passwordManager?.loadPasswordList(::callback)
    }

    override fun onResume() {
        super.onResume()
        loadPasswordList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUESTCODE)
        {
            startActivity(nextIntent)
        }
    }
}

