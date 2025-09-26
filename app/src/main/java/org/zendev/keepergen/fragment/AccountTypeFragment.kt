package org.zendev.keepergen.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.zendev.keepergen.R
import org.zendev.keepergen.databinding.FragmentAccountTypeBinding


class AccountTypeFragment : Fragment() {
    private lateinit var b: FragmentAccountTypeBinding

    var accountType = "Social media"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentAccountTypeBinding.inflate(layoutInflater)
        return b.root
    }
}