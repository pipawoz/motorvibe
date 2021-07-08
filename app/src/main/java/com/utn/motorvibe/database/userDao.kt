package com.utn.motorvibe.database

import androidx.room.*
import com.utn.motorvibe.entities.User

@Dao
interface userDao {

    @Query("SELECT * FROM users ORDER BY id")
    fun loadAllUsers(): MutableList<User?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User?)

    @Update
    fun updateUser(user: User?)

    @Delete
    fun delete(user: User?)

    //@Query("SELECT * FROM users WHERE id = :id")
    //fun loadUserById(id: Int): User?
}