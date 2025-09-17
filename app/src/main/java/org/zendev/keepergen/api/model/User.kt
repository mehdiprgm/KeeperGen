package org.zendev.keepergen.api.model

import java.io.Serializable

data class User(
    var id: Int,
    var username: String,
    var password: String,
    var securityCode: String,
    var phoneNumber: String,
    var imagePath: String,
    var loginDateTime: String,
    var isLocked: Boolean,
    var createDate: String
) : Serializable