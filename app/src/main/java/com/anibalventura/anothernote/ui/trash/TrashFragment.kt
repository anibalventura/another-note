package com.anibalventura.anothernote.ui.trash

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.anibalventura.anothernote.Constants.TRASH_TO_EMPTY
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.adapters.TrashAdapter
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.data.viewmodel.TrashViewModel
import com.anibalventura.anothernote.databinding.FragmentTrashBinding
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class TrashFragment : Fragment() {

    // ViewModels
    private val trashViewModel: TrashViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    // DataBinding.
    private var _binding: FragmentTrashBinding? = null
    private val binding get() = _binding!!

    // Adapter.
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

        // Get notify every time the database change.
        trashViewModel.getAllData.observe(viewLifecycleOwner, { data ->
            sharedViewModel.checkIfTrashIsEmpty(data)
            adapter.setData(data)
        })

        // Setup RecyclerView
        setupRecyclerView()

        // Set menu.
        setHasOptionsMenu(true)

        return binding.root
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.trashRecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 300 // Milliseconds
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        // Enable required options.
        menu.findItem(R.id.menu_main_empty_trash).setEnabled(true).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_main_empty_trash -> sharedViewModel.emptyData(
                requireContext(),
                TRASH_TO_EMPTY
            )
        }
        return super.onOptionsItemSelected(item)
    }

    // Destroy all references of the fragment to avoid memory leak.
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}