package com.utn.motorvibe.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.utn.motorvibe.entities.User


@Database(entities = [User::class], version = 1, exportSchema = false)
public  abstract class userDatabase : RoomDatabase() {

    abstract fun userDao(): userDao

    companion object {
        var INSTANCE: userDatabase? = null

        fun getAppDataBase(context: Context): userDatabase? {
            if (INSTANCE == null) {
                synchronized(userDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        userDatabase::class.java,
                        "userDB"
                    ).allowMainThreadQueries().build() // No es lo mas recomendable que se ejecute en el mainthread
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}