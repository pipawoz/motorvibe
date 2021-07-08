package com.utn.motorvibe.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class User (username : String, password : String, email : String){

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id : Int = 0

    @ColumnInfo(name = "username")
    var username : String = username

    @ColumnInfo(name = "password")
    var password : String = password

    @ColumnInfo(name = "email")
    var email : String = email
}