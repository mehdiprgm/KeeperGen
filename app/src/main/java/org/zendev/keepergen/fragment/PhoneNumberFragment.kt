package org.zendev.keepergen.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.zendev.keepergen.R
import org.zendev.keepergen.databinding.FragmentPhoneNumberBinding

class PhoneNumberFragment : Fragment() {
    private lateinit var b: FragmentPhoneNumberBinding
    var phoneNumber = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentPhoneNumberBinding.inflate(layoutInflater)
        return b.root
    }
}