package org.zendev.keepergen.viewmodel

import androidx.lifecycle.ViewModel
import org.zendev.keepergen.api.base.ApiResponse
import org.zendev.keepergen.api.base.RetrofitClient
import org.zendev.keepergen.api.model.User
import org.zendev.keepergen.api.repository.UserRepository
import retrofit2.Response

class ApiViewModel : ViewModel() {
    private var userRepository: UserRepository = UserRepository(RetrofitClient.apiService)

    suspend fun addUser(user: User): Response<ApiResponse> {
        return userRepository.addUser(user)
    }

    suspend fun updateUser(user: User): Response<ApiResponse> {
        return userRepository.updateUser(user)
    }

    suspend fun getUser(username: String): Response<User> {
        return userRepository.getUser(username)
    }

    suspend fun getAllUsers(): Response<List<User>> {
        return userRepository.getAllUsers()
    }
}