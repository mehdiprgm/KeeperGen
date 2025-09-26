package org.zendev.keepergen.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import org.zendev.keepergen.R
import org.zendev.keepergen.database.Table
import org.zendev.keepergen.databinding.FragmentNameBinding
import org.zendev.keepergen.tools.validation.AccountValidator

class NameFragment(private val table: Table) : Fragment() {
    private lateinit var b: FragmentNameBinding
    private var name: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        b = FragmentNameBinding.inflate(layoutInflater)
        return b.root
    }

    private fun isNameValid(): Boolean {
        val result =
            AccountValidator(requireActivity() as AppCompatActivity).isNameValid(b.txtName.text.toString())

        if (result.isOk) {
            b.tvErrorMessage.text = ""
            return true
        }

        b.tvErrorMessage.text = result.message
        return false
    }

    fun readName(): String? {
        if (isNameValid()) {
            name = b.txtName.text.toString()
        }

        return name
    }

    fun clearName() {
        name = null
    }
}