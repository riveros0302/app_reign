package com.riveros0302.app_news.database


import java.io.Serializable


data class DataModelSQLite (
    var created_at : String = "",
    var story_title : String = "",
    var author : String = "",
    var story_url : String = "") : Serializable