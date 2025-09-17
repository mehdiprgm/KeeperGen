package org.zendev.keepergen.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Accounts")
data class Account(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var userId : Int = 0,
    var name: String,
    var phoneNumber: String,
    var username: String,
    var password: String,
    var accountType: Int,
    var comment: String
) : Serializable