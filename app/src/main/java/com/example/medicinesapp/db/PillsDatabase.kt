package com.example.medicinesapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.medicinesapp.data.PillDB
import com.example.medicinesapp.data.PillOrganizerDB
import com.example.medicinesapp.data.PillTimeDB
import com.example.medicinesapp.data.WithoutInternetPillDB
import com.example.medicinesapp.utill.converters.ArrayTypeConverters


@TypeConverters(ArrayTypeConverters::class)
@Database(entities = [PillDB::class,WithoutInternetPillDB::class,PillTimeDB::class,PillOrganizerDB::class],version=3,exportSchema = false)
abstract class PillsDatabase:RoomDatabase() {

    abstract fun pillDao():PillDao
    abstract fun noInternetPillDao():NoInternetPillDao
    abstract fun pillTimeDao():PillTimeDao
    abstract fun pillOrganizerDao():PillOrganizerDao

    companion object{

        private val migration_1_2 = object:Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
               database.execSQL("ALTER TABLE pills_organizer ADD COLUMN name TEXT ")
            }
        }

        private val migration_2_3 = object:Migration(2,3){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE pills_time ADD COLUMN done INTEGER ")
            }
        }


        @Volatile private var instance: PillsDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                PillsDatabase::class.java, "pillsEntries.db")
                .addMigrations(migration_1_2, migration_2_3)
                .build()
    }
}