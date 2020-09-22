package com.anibalventura.anothernote.ui.archive

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.data.viewmodel.ArchiveViewModel
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.databinding.FragmentArchiveBinding
import com.anibalventura.anothernote.ui.archive.adapter.ArchiveAdapter
import com.anibalventura.anothernote.utils.hideKeyboard
import com.anibalventura.anothernote.utils.showToast

class ArchiveFragment : Fragment(), SearchView.OnQueryTextListener {

    // DataBinding.
    private var _binding: FragmentArchiveBinding? = null
    private val binding get() = _binding!!

    // ViewModels
    private val archiveViewModel: ArchiveViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    // Adapter.
    private val adapter: ArchiveAdapter by lazy { ArchiveAdapter() }
    private lateinit var recyclerView: RecyclerView

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
        recyclerView = binding.archiveRecyclerView
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
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.itemAnimator = null
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
            R.id.archive_menu_delete_all -> confirmDeleteAll()
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
        dialogBuilder.setTitle(getString(R.string.dialog_delete_all))
        dialogBuilder.setMessage(getString(R.string.dialog_delete_you_sure))
        dialogBuilder.setPositiveButton(getString(R.string.dialog_confirmation)) { _, _ ->
            archiveViewModel.deleteAll()
            showToast(requireContext(), getString(R.string.delete_all_successful))
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