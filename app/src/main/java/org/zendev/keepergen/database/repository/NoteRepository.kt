package org.zendev.keepergen.database.repository

import androidx.lifecycle.LiveData
import org.zendev.keepergen.database.dao.NoteDAO
import org.zendev.keepergen.database.entity.Contact
import org.zendev.keepergen.database.entity.Note

class NoteRepository(private val noteDAO: NoteDAO) {
    val allNotes : LiveData<List<Note>> = noteDAO.getAll()

    fun add(note: Note) {
        noteDAO.add(note)
    }

    fun delete(note: Note) {
        noteDAO.delete(note)
    }

    fun get(name: String) : Note? {
        return noteDAO.get(name)
    }

    fun update(note: Note) {
        noteDAO.update(note)
    }

    fun count() : Int {
        return noteDAO.count()
    }

    fun search(query: String): LiveData<List<Note>> {
        return noteDAO.search(query)
    }
}