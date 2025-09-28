package org.zendev.keepergen.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "Contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var userId: Int = 0,
    var name: String,
    var phoneNumber: String,
    var comment: String
) : Parcelable {
    override fun toString(): String {
        return "Name: $name\nPhone Number: $phoneNumber\nComment: $comment\n\n"
    }
}
