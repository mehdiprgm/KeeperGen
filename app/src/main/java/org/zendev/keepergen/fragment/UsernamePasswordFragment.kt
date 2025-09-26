package org.zendev.keepergen.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import org.zendev.keepergen.R
import org.zendev.keepergen.databinding.FragmentUsernamePasswordBinding
import org.zendev.keepergen.tools.validation.AccountValidator

class UsernamePasswordFragment : Fragment() {
    private lateinit var b: FragmentUsernamePasswordBinding

    var username: String? = null
    var password: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentUsernamePasswordBinding.inflate(layoutInflater)
        return b.root
    }

    private fun isUsernameValid(): Boolean {
        val result =
            AccountValidator(requireActivity() as AppCompatActivity).isUsernameValid(b.txtUsername.text.toString())

        if (result.isOk) {
            b.tvUsernameErrorMessage.text = ""
            return true
        }

        b.tvUsernameErrorMessage.text = result.message
        return false
    }

    private fun isPasswordValid(): Boolean {
        val result =
            AccountValidator(requireActivity() as AppCompatActivity).isPasswordValid(b.txtPassword.text.toString())

        if (result.isOk) {
            b.tvPasswordErrorMessage.text = ""
            return true
        }

        b.tvPasswordErrorMessage.text = result.message
        return false
    }

    fun readUsername(): String? {
        if (isUsernameValid()) {
            username = b.txtUsername.text.toString()
        }

        return username
    }

    fun readPassword(): String? {
        if (isPasswordValid()) {
            password = b.txtPassword.text.toString()
        }

        return password
    }

    fun clearUsernameAndPassword() {
        username = null
        password = null
    }
}