package com.utn.motorvibe.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "readings")
class Reading (name : String, reading: Double){

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id : Int = 0

    @ColumnInfo(name = "name")
    var name : String = name

    @ColumnInfo(name = "reading")
    var reading : Double = reading
}