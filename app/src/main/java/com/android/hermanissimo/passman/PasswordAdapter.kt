package com.android.hermanissimo.passman

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.android.hermanissimo.passman.model.PasswordModel

import kotlinx.android.synthetic.main.item_password.view.*
import java.util.*
import java.util.regex.Pattern

class PasswordAdapter(var passwordModels: List<PasswordModel>, var onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var filtered:List<PasswordModel>?=null


    override fun getFilter(): Filter? {
        return object :Filter()
        {
            override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults?) {
                setAndSortData(results!!.values as ArrayList<PasswordModel>)
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence?): Filter.FilterResults? {
                val filterResults = Filter.FilterResults()
                val tmp = ArrayList<PasswordModel>()
                if (constraint == null || "" == constraint)
                {
                    tmp.addAll(passwordModels)
                }
                else
                {
                    for (item in passwordModels)
                    {
                        if (matchString(item.label, constraint))
                            tmp.add(item)
                    }
                }
                filterResults.values = tmp
                filterResults.count = tmp.size
                return filterResults
            }

        }
    }

    init
    {
        setAndSortData(passwordModels)
    }

    fun setAndSortData(list:List<PasswordModel>)
    {
        filtered = list
        Collections.sort(filtered,{ lhs, rhs -> lhs.label.compareTo(rhs.label)});
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_password, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).itemView.name.text = filtered!![position].label
    }

    override fun getItemCount(): Int {
        return filtered!!.size;
    }

    interface OnItemClickListener {
        fun onClick(viewHolder: ViewHolder, item: PasswordModel, position: Int)
        fun onLongClick(viewHolder: ViewHolder, item: PasswordModel, position: Int)
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view),View.OnClickListener, View.OnLongClickListener
    {
        init
        {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onLongClick(p0: View?): Boolean
        {
            var pos = layoutPosition
            if (pos < 0) {
                pos = 0
            }
            onItemClickListener.onLongClick(this,filtered!![pos], pos)
            return true
        }

        override fun onClick(v: View)
        {
            var pos = layoutPosition
            if (pos < 0) {
                pos = 0
            }
            onItemClickListener.onClick(this,filtered!![pos], pos)
        }
    }

    fun matchString(value:String,constraint: CharSequence?):Boolean{
        if (TextUtils.isEmpty(value)) {
            return false
        }
        var s = constraint.toString()
        if (TextUtils.isEmpty(s)) {
            return false
        }
        s = Pattern.quote(s)
        return Pattern.compile("$s.*|.*[-\\s/]$s.*", Pattern.CASE_INSENSITIVE).matcher(value).matches()
    }
}
