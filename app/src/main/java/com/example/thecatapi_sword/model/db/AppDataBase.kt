package com.example.thecatapi_sword.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.thecatapi_sword.model.BreedDao
import com.example.thecatapi_sword.model.BreedEntity
import com.example.thecatapi_sword.model.FavouriteBreedDao
import com.example.thecatapi_sword.model.FavouriteBreedEntity

@Database(
    entities = [FavouriteBreedEntity::class, BreedEntity::class],
    version = 6
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteBreedDao(): FavouriteBreedDao
    abstract fun breedDao(): BreedDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cat_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
