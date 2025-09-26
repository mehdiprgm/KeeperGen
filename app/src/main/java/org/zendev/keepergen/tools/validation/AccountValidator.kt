package org.zendev.keepergen.tools.validation

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import org.zendev.keepergen.viewmodel.DatabaseViewModel

class AccountValidator(activity: AppCompatActivity) {
    private var databaseViewModel: DatabaseViewModel =
        ViewModelProvider(activity)[DatabaseViewModel::class.java]

    fun isNameValid(name: String): ValidationResult {
        val validationResult = ValidationResult(true, "OK")

        if (name.isEmpty()) {
            validationResult.isOk = false
            validationResult.message = "Name is empty, select a name for this account"
        } else {
            val account = databaseViewModel.getAccount(name)

            if (account != null) {
                validationResult.isOk = false
                validationResult.message = "Account with this name already exists"
            }
        }

        return validationResult
    }

    fun isUsernameValid(username: String): ValidationResult {
        val validationResult = ValidationResult(true, "OK")

        if (username.isEmpty()) {
            validationResult.isOk = false
            validationResult.message = "Username is empty."
        }

        return validationResult
    }

    fun isPasswordValid(password: String): ValidationResult {
        val validationResult = ValidationResult(true, "OK")

        if (password.isEmpty()) {
            validationResult.isOk = false
            validationResult.message = "Password is empty."
        }

        return validationResult
    }
}