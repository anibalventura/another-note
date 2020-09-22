package com.anibalventura.anothernote.ui.trash

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.data.viewmodel.TrashViewModel
import com.anibalventura.anothernote.databinding.FragmentTrashBinding
import com.anibalventura.anothernote.ui.trash.adapter.TrashRecyclerViewAdapter
import com.anibalventura.anothernote.utils.showToast
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class TrashFragment : Fragment() {

    // ViewModels
    private val trashViewModel: TrashViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    // DataBinding.
    private var _binding: FragmentTrashBinding? = null
    private val binding get() = _binding!!

    // Adapter.
    private val adapter: TrashRecyclerViewAdapter by lazy { TrashRecyclerViewAdapter() }

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
            R.id.menu_main_empty_trash -> emptyTrash()
        }
        return super.onOptionsItemSelected(item)
    }

    /*
     * Dialog for confirmation of delete all notes.
     */
    private fun emptyTrash() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(getString(R.string.dialog_empty_trash))
        dialogBuilder.setMessage(getString(R.string.dialog_empty_trash_you_sure))
        dialogBuilder.setPositiveButton(getString(R.string.dialog_empty_trash)) { _, _ ->
            trashViewModel.deleteAll()
            showToast(requireContext(), getString(R.string.empty_trash_successful))
        }
        dialogBuilder.setNegativeButton(getString(R.string.dialog_negative)) { _, _ -> }
        dialogBuilder.show()
    }

    // Destroy all references of the fragment to avoid memory leak.
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}