package com.anibalventura.anothernote.ui.shareapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.databinding.FragmentAboutBinding
import com.anibalventura.anothernote.utils.shareText

class ShareAppFragment : Fragment() {

    // DataBinding.
    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /**
         * Inflate the layout for this fragment.
         */
        // DataBinding.
        _binding = FragmentAboutBinding.inflate(inflater, container, false)

        // Share text.
        shareText(requireContext(), getString(R.string.share_app))

        // Go back to home.
        findNavController().navigate(R.id.action_shareAppFragment_to_noteFragment)

        return binding.root
    }
}