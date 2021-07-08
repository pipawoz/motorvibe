package com.utn.motorvibe.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.utn.motorvibe.entities.Reading


@Database(entities = [Reading::class], version = 1, exportSchema = false)
public  abstract class readingsDatabase : RoomDatabase() {

    abstract fun readingsDao(): readingsDao

    companion object {
        var INSTANCE: readingsDatabase? = null

        fun getAppDataBase(context: Context): readingsDatabase? {
            if (INSTANCE == null) {
                synchronized(readingsDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            readingsDatabase::class.java,
                            "readingsDB"
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