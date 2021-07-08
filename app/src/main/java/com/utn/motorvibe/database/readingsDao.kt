package com.utn.motorvibe.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.utn.motorvibe.entities.Reading

@Dao
interface readingsDao {

    @Query("SELECT * FROM readings ORDER BY id")
    fun loadAllReadings(): MutableList<Reading?>?

    @Query("SELECT * FROM readings WHERE name = :name")
    fun loadReadingsByName(name: String): MutableList<Reading?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReading(reading: Reading?)

    //@Query("UPDATE motors SET name = :name, model = :model, status = :status WHERE name = :name")
    //fun updateMotor(name: String, model: String, status: String)

    //@Query("DELETE FROM readings WHERE name = :name")
   // fun deleteMotorReadings(name: String)

    //@Query("SELECT count(name) FROM motors where name = :name")
    //fun loadReadingsByName(name: String): Float

    //@Query("SELECT * FROM motors WHERE id = :id")
   // fun loadMotorById(id: Int): Motor?

    @Query("SELECT COUNT(id) FROM readings")
    fun getCount(): Int
}