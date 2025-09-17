package org.zendev.keepergen.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import org.zendev.keepergen.database.MainDatabase
import org.zendev.keepergen.database.entity.Account
import org.zendev.keepergen.database.entity.BankCard
import org.zendev.keepergen.database.entity.Contact
import org.zendev.keepergen.database.entity.Note
import org.zendev.keepergen.database.repository.AccountRepository
import org.zendev.keepergen.database.repository.BankCardRepository
import org.zendev.keepergen.database.repository.ContactRepository
import org.zendev.keepergen.database.repository.NoteRepository

class DatabaseViewModel(application: Application) : AndroidViewModel(application) {

    private val accountRepository: AccountRepository
    private val bankCardRepository: BankCardRepository
    private val contactRepository: ContactRepository
    private val noteRepository: NoteRepository

    private val accountSearchQuery = MutableLiveData<String>()
    private val bankCardSearchQuery = MutableLiveData<String>()
    private val contactSearchQuery = MutableLiveData<String>()
    private val noteSearchQuery = MutableLiveData<String>()

    val allAccounts: LiveData<List<Account>>
    val allBankCards: LiveData<List<BankCard>>
    val allContacts: LiveData<List<Contact>>
    val allNotes: LiveData<List<Note>>

    init {
        val accountDAO = MainDatabase.getDatabase(application).accountDAO()
        val bankCardDAO = MainDatabase.getDatabase(application).bankCardDAO()
        val contactDAO = MainDatabase.getDatabase(application).contactDAO()
        val noteDAO = MainDatabase.getDatabase(application).noteDAO()

        accountRepository = AccountRepository(accountDAO)
        bankCardRepository = BankCardRepository(bankCardDAO)
        contactRepository = ContactRepository(contactDAO)
        noteRepository = NoteRepository(noteDAO)

        allAccounts = accountRepository.allAccounts
        allBankCards = bankCardRepository.allBankCards
        allContacts = contactRepository.allContacts
        allNotes = noteRepository.allNotes
    }

    val accountSearchResults: LiveData<List<Account>> = accountSearchQuery.switchMap { query ->
        accountRepository.search(query)
    }

    val bankCardSearchResults: LiveData<List<BankCard>> = bankCardSearchQuery.switchMap { query ->
        bankCardRepository.search(query)
    }

    val contactSearchResults: LiveData<List<Contact>> = contactSearchQuery.switchMap { query ->
        contactRepository.search(query)
    }

    val noteSearchResults: LiveData<List<Note>> = noteSearchQuery.switchMap { query ->
        noteRepository.search(query)
    }

    fun addAccount(account: Account) {
        accountRepository.add(account)
    }

    fun getAccount(name: String): Account? {
        return accountRepository.get(name)
    }

    fun deleteAccount(account: Account) {
        accountRepository.delete(account)
    }

    fun updateAccount(account: Account) {
        accountRepository.update(account)
    }

    fun countAccounts(): Int {
        return accountRepository.count()
    }

    fun addBankCard(bankCard: BankCard) {
        bankCardRepository.add(bankCard)
    }

    fun getBankCard(cardName: String): BankCard? {
        return bankCardRepository.get(cardName)
    }

    fun deleteBankCard(bankCard: BankCard) {
        bankCardRepository.delete(bankCard)
    }

    fun updateBankCard(bankCard: BankCard) {
        bankCardRepository.update(bankCard)
    }

    fun countBankCards(): Int {
        return bankCardRepository.count()
    }

    fun addContact(contact: Contact) {
        contactRepository.add(contact)
    }

    fun getContact(name: String): Contact? {
        return contactRepository.get(name)
    }

    fun deleteContact(contact: Contact) {
        contactRepository.delete(contact)
    }

    fun updateContact(contact: Contact) {
        contactRepository.update(contact)
    }

    fun countContacts(): Int {
        return contactRepository.count()
    }

    fun addNote(note: Note) {
        noteRepository.add(note)
    }

    fun getNote(name: String): Note? {
        return noteRepository.get(name)
    }

    fun deleteNote(note: Note) {
        noteRepository.delete(note)
    }

    fun updateNote(note: Note) {
        noteRepository.update(note)
    }

    fun countNotes(): Int {
        return noteRepository.count()
    }

    fun setAccountSearchQuery(query: String) {
        accountSearchQuery.value = query
    }

    fun setBankCardSearchQuery(query: String) {
        bankCardSearchQuery.value = query
    }

    fun setContactSearchQuery(query: String) {
        contactSearchQuery.value = query
    }

    fun setNoteSearchQuery(query: String) {
        noteSearchQuery.value = query
    }
}