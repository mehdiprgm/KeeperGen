package org.zendev.keepergen.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "Notes")
data class Note(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var userId: Int = 0,
    var name: String,
    var content: String,
    var modifyDate: String
) : Parcelable {
    override fun toString(): String {
        return "Name: $name\nContent: $content\nModify Date: $modifyDate\n\n"
    }
}
