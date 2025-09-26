package org.zendev.keepergen.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.zendev.keepergen.database.dao.AccountDAO
import org.zendev.keepergen.database.dao.BankCardDAO
import org.zendev.keepergen.database.dao.ContactDAO
import org.zendev.keepergen.database.dao.NoteDAO
import org.zendev.keepergen.database.entity.Account
import org.zendev.keepergen.database.entity.BankCard
import org.zendev.keepergen.database.entity.Contact
import org.zendev.keepergen.database.entity.Note

@Database(entities = [Account::class, BankCard::class, Contact::class, Note::class], version = 7, exportSchema = false)
abstract class MainDatabase : RoomDatabase() {
    abstract fun accountDAO(): AccountDAO
    abstract fun bankCardDAO(): BankCardDAO
    abstract fun contactDAO() : ContactDAO
    abstract fun noteDAO() : NoteDAO

    companion object {

        @Volatile
        private var INSTANCE: MainDatabase? = null

        fun getDatabase(context: Context): MainDatabase {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, MainDatabase::class.java, "KeeperGenDatabase"
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}