package com.anibalventura.anothernote.ui.archive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anibalventura.anothernote.databinding.FragmentArchiveBinding

class ArchiveFragment : Fragment() {

    // DataBinding.
    private var _binding: FragmentArchiveBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /**
         * Inflate the layout for this fragment.
         */
        // DataBinding.
        _binding = FragmentArchiveBinding.inflate(inflater, container, false)


        return binding.root
    }

}