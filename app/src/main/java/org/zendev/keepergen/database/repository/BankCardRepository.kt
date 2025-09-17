package org.zendev.keepergen.database.repository

import androidx.lifecycle.LiveData
import org.zendev.keepergen.database.dao.AccountDAO
import org.zendev.keepergen.database.dao.BankCardDAO
import org.zendev.keepergen.database.entity.Account
import org.zendev.keepergen.database.entity.BankCard

class BankCardRepository(private val bankCardDAO: BankCardDAO) {
    val allBankCards : LiveData<List<BankCard>> = bankCardDAO.getAll()

    fun add(bankCard: BankCard) {
        bankCardDAO.add(bankCard)
    }

    fun delete(bankCard: BankCard) {
        bankCardDAO.delete(bankCard)
    }

    fun get(cardName: String) : BankCard? {
        return bankCardDAO.get(cardName)
    }

    fun update(bankCard: BankCard) {
        bankCardDAO.update(bankCard)
    }

    fun count() : Int {
        return bankCardDAO.count()
    }

    fun search(query: String): LiveData<List<BankCard>> {
        return bankCardDAO.search(query)
    }
}