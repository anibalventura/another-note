package com.anibalventura.anothernote.ui.archive

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.data.viewmodel.ArchiveViewModel
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.databinding.FragmentArchiveBinding
import com.anibalventura.anothernote.ui.archive.adapter.ArchiveAdapter
import com.anibalventura.anothernote.utils.hideKeyboard
import com.anibalventura.anothernote.utils.showToast
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class ArchiveFragment : Fragment(), SearchView.OnQueryTextListener {

    // ViewModels
    private val archiveViewModel: ArchiveViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    // DataBinding.
    private var _binding: FragmentArchiveBinding? = null
    private val binding get() = _binding!!

    private val adapter: ArchiveAdapter by lazy { ArchiveAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /**
         * Inflate the layout for this fragment.
         */
        // DataBinding.
        _binding = FragmentArchiveBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel

        // Setup RecyclerView
        setupRecyclerView()

        // Get notify every time the database change.
        archiveViewModel.getAllData.observe(viewLifecycleOwner, { data ->
            sharedViewModel.checkIfArchiveIsEmpty(data)
            adapter.setData(data)
        })

        // Set menu.
        setHasOptionsMenu(true)

        // Hide soft keyboard.
        hideKeyboard(requireActivity())

        return binding.root
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.archiveRecyclerView
        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 300 // Milliseconds
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_archive, menu)

        val search = menu.findItem(R.id.archive_menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.note_menu_delete_all -> confirmDeleteAll()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchTroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchTroughDatabase(query)
        }
        return true
    }

    private fun searchTroughDatabase(query: String) {
        archiveViewModel.searchDatabase("%$query%").observe(this, { list ->
            list?.let {
                adapter.setData(it)
            }
        })
    }

    private fun confirmDeleteAll() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Delete All")
        dialogBuilder.setTitle("Delete All")
        dialogBuilder.setMessage("Are you sure you want to delete everything?")
        dialogBuilder.setPositiveButton("Yes") { _, _ ->
            archiveViewModel.deleteAll()
            showToast(requireContext(), "Successfully Deleted Everything.")
        }
        dialogBuilder.setNegativeButton("No") { _, _ -> }
        dialogBuilder.show()
    }

    // Destroy all references of the fragment to avoid memory leak.
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}