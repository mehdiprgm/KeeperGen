package org.zendev.keepergen.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var userId: Int = 0,
    var name: String,
    var content: String,
    var modifyDate: String
) : Serializable {
    override fun toString(): String {
        return "Name: $name\nContent: $content\nModify Date: $modifyDate\n\n"
    }
}
