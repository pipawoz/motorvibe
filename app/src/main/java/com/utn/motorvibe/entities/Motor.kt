package com.utn.motorvibe.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "motors")
class Motor (name : String, model : String, status : String){

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id : Int = 0

    @ColumnInfo(name = "name")
    var name : String = name

    @ColumnInfo(name = "model")
    var model : String = model

    @ColumnInfo(name = "status")
    var status : String = status

}