package com.pv_libs.coroutine_cache_pro.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pv_libs.coroutine_cache_pro.R
import com.pv_libs.coroutine_cache_pro.models.User

class UsersAdapter : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    var listItems: List<User> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindWith(listItems[position])
    }

    class UserViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        @SuppressLint("SetTextI18n")
        fun bindWith(user: User) {

            itemView.findViewById<TextView>(R.id.userNameView).text =
                "${user.firstName} ${user.lastName}"


        }

    }

}