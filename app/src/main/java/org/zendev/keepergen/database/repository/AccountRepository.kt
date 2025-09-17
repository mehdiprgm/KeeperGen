package org.zendev.keepergen.database.repository

import androidx.lifecycle.LiveData
import org.zendev.keepergen.database.dao.AccountDAO
import org.zendev.keepergen.database.entity.Account

class AccountRepository(private val accountDAO: AccountDAO) {
    val allAccounts : LiveData<List<Account>> = accountDAO.getAll()

    fun add(account: Account) {
        accountDAO.add(account)
    }

    fun delete(account: Account) {
        accountDAO.delete(account)
    }

    fun get(name: String) : Account? {
        return accountDAO.get(name)
    }

    fun update(account: Account) {
        accountDAO.update(account)
    }

    fun count() : Int {
        return accountDAO.count()
    }

    fun search(query: String): LiveData<List<Account>> {
        return accountDAO.search(query)
    }
}