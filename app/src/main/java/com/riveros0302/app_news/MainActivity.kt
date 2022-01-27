package com.riveros0302.app_news

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.get
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.riveros0302.app_news.database.DataModelDelete
import com.riveros0302.app_news.database.DataModelSQLite
import com.riveros0302.app_news.database.SQLiteHelper

class MainActivity : AppCompatActivity(), AdapterNews.NewsItemClicked {

    private lateinit var mAdapter : AdapterNews
    private var sqliteHelper = SQLiteHelper(this)
    lateinit var swipeRefresh : SwipeRefreshLayout
    val url = "https://hn.algolia.com/api/v1/search_by_date?query=mobile"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<RecyclerView>(R.id.myRecyclerView)
        mAdapter = AdapterNews( this, this)
        recycler.adapter = mAdapter
        recycler.layoutManager = LinearLayoutManager(this)

        fetchData()
        Refreshing()
        swipeDelete(recycler)
    }



    private fun Refreshing(){
        swipeRefresh = findViewById(R.id.swipeRefreshLayout)
        swipeRefresh.setOnRefreshListener {

            fetchData()
            if (checkForInternet(this)){
                swipeRefresh.isRefreshing = false
                Toast.makeText(this, "updated list", Toast.LENGTH_SHORT).show()
            }else {
                swipeRefresh.isRefreshing = false
                Toast.makeText(this, "data can't be updated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun swipeDelete(recycler: RecyclerView){

        val swipeGesture = object : SwipeGesture(this){

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                when(direction){
                    ItemTouchHelper.LEFT ->{
                        mAdapter.deleteNews(viewHolder.adapterPosition)
                    }
                }
            }
        }

        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(recycler)
    }

    private fun checkForInternet(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }


    private fun fetchData() {
        if (checkForInternet(this)){
            sqliteHelper.deleteNews()
        }else{
            Toast.makeText(this, "No connection", Toast.LENGTH_SHORT).show()
        }

        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener {

                val newsJsonArray = it.getJSONArray("hits")
                val newsArray =ArrayList<DataModelSQLite>()

                for(i in 0 until newsJsonArray.length()){
                    val objeto = newsJsonArray.getJSONObject(i)
                    val news= DataModelSQLite(
                        objeto.getString("created_at"),
                        objeto.getString("story_title"),
                        objeto.getString("author"),
                        objeto.getString("story_url")
                    )


                    newsArray.add(news)
                    sqliteHelper.insertNews(news)
                }


                val newsList = sqliteHelper.getAllNews()
                mAdapter?.addNews(newsList)


            },
            Response.ErrorListener { error ->
                Log.e("Error: ", error.toString())
                val newsList = sqliteHelper.getAllNews()
                mAdapter?.addNews(newsList)
            }
        )

        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }



    override fun onItemClicked(item: DataModelSQLite) {
        Toast.makeText(this, "Item: "+item.author, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("url", item.story_url)
        startActivity(intent)
    }
}