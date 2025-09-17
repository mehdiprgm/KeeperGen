package org.zendev.keepergen.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "BankCards")
data class BankCard(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var userId: Int = 0,
    var cardName: String,
    var cardNumber: String,
    var cvv2: String,
    var month: String,
    var year: String,
    var password: String
) : Serializable
