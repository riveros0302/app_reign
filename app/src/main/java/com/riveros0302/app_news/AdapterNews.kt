package com.riveros0302.app_news

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.riveros0302.app_news.database.DataModelDelete
import com.riveros0302.app_news.database.DataModelSQLite
import com.riveros0302.app_news.database.SQLiteHelper

class AdapterNews (context: Context, private val news: NewsItemClicked):  RecyclerView.Adapter<AdapterNews.NewsViewHolder>(){

    private var items: ArrayList<DataModelSQLite> = ArrayList()
    private var sqliteHelper = SQLiteHelper(context)
    private var posicion = 0

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val desc : TextView = itemView.findViewById(R.id.tv_description)
        val author: TextView = itemView.findViewById(R.id.tv_author)
        val time : TextView =  itemView.findViewById(R.id.tv_time)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_news, parent, false)


        val viewHolder =  NewsViewHolder(view)
        view.setOnClickListener {
            news.onItemClicked(items[viewHolder.adapterPosition])

        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem =items[position]
        holder.desc.text = currentItem.story_title
        holder.author.text = currentItem.author
        holder.time.text = currentItem.created_at
    }

    override fun getItemCount() = items.size

    fun addNews(item: ArrayList<DataModelSQLite>) {

        this.items = item
        notifyDataSetChanged()
    }

    fun deleteNews(i : Int){

        sqliteHelper.insertDEL(items.get(i))
        items.removeAt(i)
        notifyDataSetChanged()
    }

    fun updatedNews(updatedNews: List<DataModelSQLite>){

        items.clear()
        items.addAll(updatedNews)

        notifyDataSetChanged()
    }

    interface NewsItemClicked{
        fun onItemClicked(item: DataModelSQLite)
    }
}