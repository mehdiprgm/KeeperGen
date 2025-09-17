package org.zendev.keepergen.api.repository

import org.zendev.keepergen.api.ApiService
import org.zendev.keepergen.api.model.User

class UserRepository(private val api: ApiService) {
    suspend fun addUser(user: User) = api.addUser(user)
    suspend fun getUser(username: String) = api.getUser(username)
    suspend fun getAllUsers() = api.getAllUsers()
    suspend fun updateUser(user: User) = api.updateUser(user)
}