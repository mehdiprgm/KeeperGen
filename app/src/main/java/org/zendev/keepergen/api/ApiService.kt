package org.zendev.keepergen.api

import org.zendev.keepergen.api.base.ApiResponse
import org.zendev.keepergen.api.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("users/add")
    suspend fun addUser(@Body user: User): Response<ApiResponse>

    @GET("users/get/{username}")
    suspend fun getUser(@Path("username") username: String): Response<User>

    @GET("users")
    suspend fun getAllUsers(): Response<List<User>>

    @PUT("users/update")
    suspend fun updateUser(@Body user: User): Response<ApiResponse>
}