package org.zendev.keepergen.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "BankCards")
data class BankCard(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var userId: Int = 0,
    var cardName: String,
    var cardNumber: String,
    var cvv2: String,
    var month: String,
    var year: String,
    var password: String
) : Parcelable {
    override fun toString(): String {
        return "Card Name: $cardName\nCard Number: $cardNumber\nCvv2: $cvv2\nMonth: $month\nYear: $year\nPassword: $password\n\n"
    }
}
