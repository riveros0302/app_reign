package com.riveros0302.app_news.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.lang.Exception

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object{
        private const val DB_VERSION = 4
        private const val DB_NAME = "DBnews.db"
        private const val TB_NEWS = "news "
        private const val TB_DELETE = "tbdelete"
        private const val ID_DELETE = "id_delete "
        private const val STORY_DELETE = " story_delete "
        private const val ID = "id "
        private const val CREATE_AT = "created_at "
        private const val AUTHOR = "author "
        private const val STORY_TITLE = "story_title "
        private const val URL = "url "
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = (" CREATE TABLE "+ TB_NEWS + "("+ ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " + CREATE_AT + "TEXT, "+ STORY_TITLE + "TEXT, "+ AUTHOR+"TEXT, "+ URL+"TEXT "+")")
        db?.execSQL(createTable)
        val createTable2 = (" CREATE TABLE "+ TB_DELETE + "("+ ID_DELETE+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + STORY_DELETE+ "TEXT "+")")
        db?.execSQL(createTable2)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TB_NEWS")
        db!!.execSQL("DROP TABLE IF EXISTS $TB_DELETE")
        onCreate(db)

    }


    fun deleteNewsById(id: Int): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, id)

        val success = db.delete(TB_NEWS, "id=$id", null)
        Log.e("deleteIDDDD", "${id} eliminadooo")
        db.close()
        return success
    }

    fun deleteNews(): Int{
        val db = this.writableDatabase
        Log.e("deleeteeee", "taabla limpia")
        val success = db.delete(TB_NEWS, null, null)
        db.close()
        return success
    }



    fun insertNews(news: DataModelSQLite): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        //contentValues.put(ID, news.id)
        contentValues.put(CREATE_AT, news.created_at)
        contentValues.put(STORY_TITLE, news.story_title)
        contentValues.put(AUTHOR, news.author)
        contentValues.put(URL, news.story_url)

        val success = db.insert(TB_NEWS, null, contentValues)
        db.close()
        return success
    }

    fun getAllNews(): ArrayList<DataModelSQLite> {
        val newsList: ArrayList<DataModelSQLite> = ArrayList()
        val selectQuery = "SELECT * FROM $TB_NEWS"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        //var id: Int
        var create: String
        var story: String
        var author: String
        var url: String

        if (cursor.moveToFirst()) {
            do {
              //  id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                create = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
                story = cursor.getString(cursor.getColumnIndexOrThrow("story_title"))
                author = cursor.getString(cursor.getColumnIndexOrThrow("author"))
                url = cursor.getString(cursor.getColumnIndexOrThrow("url"))

                val nws = DataModelSQLite( created_at = create, story_title = story, author = author, story_url = url)
                newsList.add(nws)
            }while (cursor.moveToNext())
        }
        return newsList
    }

    //delete
    fun insertDEL(dlt: DataModelSQLite): Long {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(STORY_DELETE, dlt.story_title)
        Log.e("aaaadddd", "${values}")
        val success = db.insert(TB_DELETE, null, values)
        db.close()
        return success
    }

    fun getAllDlt(): ArrayList<DataModelDelete> {
        val dltList: ArrayList<DataModelDelete> = ArrayList()
        val selectQuery = "SELECT * FROM $TB_DELETE"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        //var id: Int
        var story_dlt: String

        if (cursor.moveToFirst()) {
            do {
                //  id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                story_dlt = cursor.getString(cursor.getColumnIndexOrThrow("story_delete"))

                val dlt = DataModelDelete(story_delete = story_dlt)
                dltList.add(dlt)
            }while (cursor.moveToNext())
        }
        return dltList
    }
}