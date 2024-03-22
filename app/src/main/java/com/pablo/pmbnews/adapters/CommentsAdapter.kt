package com.pablo.pmbnews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pablo.pmbnews.R
import com.pablo.pmbnews.bbdd.Comment

class CommentsAdapter(private var comments: List<Comment>) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val commentTextView: TextView = view.findViewById(R.id.commentContentTextView)
        val usernameTextView: TextView = view.findViewById(R.id.usernameTextView)
        val timestampTextView: TextView = view.findViewById(R.id.commentTimestampTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.commentTextView.text = comments[position].content
        holder.usernameTextView.text = comments[position].username
        holder.timestampTextView.text = comments[position].timestamp.toString()
    }

    override fun getItemCount() = comments.size

    fun updateData(newComments: List<Comment>) {
        comments = newComments
        notifyDataSetChanged()
    }
}
