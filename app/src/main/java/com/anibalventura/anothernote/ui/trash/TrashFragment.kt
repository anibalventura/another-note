package com.anibalventura.anothernote.ui.trash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.data.viewmodel.TrashViewModel
import com.anibalventura.anothernote.databinding.FragmentTrashBinding
import com.anibalventura.anothernote.ui.trash.adapter.TrashAdapter
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class TrashFragment : Fragment() {

    // ViewModels
    private val trashViewModel: TrashViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    // DataBinding.
    private var _binding: FragmentTrashBinding? = null
    private val binding get() = _binding!!

    private val adapter: TrashAdapter by lazy { TrashAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /**
         * Inflate the layout for this fragment.
         */
        // DataBinding.
        _binding = FragmentTrashBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel

        // Setup RecyclerView
        setupRecyclerView()

        // Get notify every time the database change.
        trashViewModel.getAllData.observe(viewLifecycleOwner, { data ->
            sharedViewModel.checkIfTrashIsEmpty(data)
            adapter.setData(data)
        })


        return binding.root
    }

    private fun setupRecyclerView() {
        val trashRecyclerView = binding.trashRecyclerView
        trashRecyclerView.adapter = adapter
        trashRecyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        trashRecyclerView.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 300 // Milliseconds
        }
    }

    // Destroy all references of the fragment to avoid memory leak.
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}