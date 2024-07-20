package com.example.librarys

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.librarys.model.Book
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import java.lang.Exception

class BookAdapter(private val context: Context, private var booksList: List<Book>) : BaseAdapter() {

    override fun getCount(): Int = booksList.size

    override fun getItem(position: Int): Any = booksList[position]

    override fun getItemId(position: Int): Long = booksList[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val book = booksList[position]
        holder.titleTextView.text = "Title: ${book.title}"
        holder.authorTextView.text = "Author: ${book.author}"
        holder.publisherTextView.text = "Publisher: ${book.publisher}"

        val imageUrl = "http://192.168.0.56:3001" + book.image

        Picasso.get()
            .load(imageUrl)
            .into(holder.imageView)

        return view!!
    }

    fun updateData(newList: List<Book>) {
        booksList = newList
        notifyDataSetChanged()
    }

    private class ViewHolder(view: View) {
        val titleTextView: TextView = view.findViewById(R.id.textViewTitle)
        val authorTextView: TextView = view.findViewById(R.id.textViewAuthor)
        val publisherTextView: TextView = view.findViewById(R.id.textViewPublisher)
        val imageView: ImageView = view.findViewById(R.id.imageViewBook)
    }
}
