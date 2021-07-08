package com.utn.motorvibe.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.utn.motorvibe.entities.Motor

@Dao
interface motorDao {

    @Query("SELECT * FROM motors ORDER BY id")
    fun loadAllMotors(): MutableList<Motor?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMotor(motor: Motor?)

    @Query("UPDATE motors SET name = :name, model = :model, status = :status WHERE name = :name")
    fun updateMotor(name: String, model: String, status: String)

    @Query("DELETE FROM motors WHERE name = :name")
    fun delete(name: String)
    
    @Query("SELECT count(name) FROM motors where name = :name")
    fun loadMotorCountByName(name: String): Int

    @Query("SELECT * FROM motors where name = :name")
    fun loadMotorByName(name: String): Motor?

    @Query("SELECT * FROM motors WHERE id = :id")
    fun loadMotorById(id: Int): Motor?

    @Query("SELECT COUNT(id) FROM motors")
    fun getCount(): Int
}