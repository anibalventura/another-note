package com.anibalventura.anothernote.ui.archive

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.anibalventura.anothernote.utils.Constants.ARCHIVE_EMPTY
import com.anibalventura.anothernote.utils.Constants.ARCHIVE_LAYOUT
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.adapters.ArchiveAdapter
import com.anibalventura.anothernote.data.viewmodel.ArchiveViewModel
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.databinding.FragmentArchiveBinding
import com.anibalventura.anothernote.utils.hideSoftKeyboard
import com.anibalventura.anothernote.utils.sharedPref

class ArchiveFragment : Fragment(), SearchView.OnQueryTextListener {

    // DataBinding.
    private var _binding: FragmentArchiveBinding? = null
    private val binding get() = _binding!!

    // ViewModels
    private val archiveViewModel: ArchiveViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    // RecyclerView Adapter.
    private val adapter: ArchiveAdapter by lazy { ArchiveAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment.
        _binding = FragmentArchiveBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel

        // Setup RecyclerView.
        setupRecyclerView()

        // Get notify every time the database change.
        archiveViewModel.getDatabase.observe(viewLifecycleOwner, { data ->
            sharedViewModel.checkIfArchiveIsEmpty(data)
            adapter.setData(data)
        })

        // Set menu.
        setHasOptionsMenu(true)

        // Hide Soft Keyboard.
        hideSoftKeyboard(requireActivity())

        return binding.root
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.archiveRecyclerView
        recyclerView.adapter = adapter

        // Set layout view.
        when (sharedPref(requireContext()).getBoolean(ARCHIVE_LAYOUT, false)) {
            true -> recyclerView.layoutManager = LinearLayoutManager(requireActivity())
            false -> recyclerView.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        // Set to null because a bug in RecyclerView library.
        recyclerView.itemAnimator = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        // Enable required options.
        menu.findItem(R.id.menu_main_search).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_main_list).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_main_grid).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_main_delete_all).setEnabled(true).isVisible = true
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val search = menu.findItem(R.id.menu_main_search).actionView as? SearchView
        val list = menu.findItem(R.id.menu_main_list)
        val grid = menu.findItem(R.id.menu_main_grid)

        // Set search function.
        search?.isSubmitButtonEnabled = true
        search?.setOnQueryTextListener(this)

        // Change option when change recyclerview layout.
        when (sharedPref(requireContext()).getBoolean(ARCHIVE_LAYOUT, false)) {
            true -> {
                list.setEnabled(false).isVisible = false
                grid.setEnabled(true).isVisible = true
            }
            false -> {
                list.setEnabled(true).isVisible = true
                grid.setEnabled(false).isVisible = false
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_main_list -> changeLayoutView(true)
            R.id.menu_main_grid -> changeLayoutView(false)
            R.id.menu_main_delete_all ->
                sharedViewModel.emptyDatabase(requireContext(), ARCHIVE_EMPTY)
        }

        return super.onOptionsItemSelected(item)
    }

    @Suppress("DEPRECATION")
    private fun changeLayoutView(change: Boolean) {
        sharedPref(requireContext()).edit().putBoolean(ARCHIVE_LAYOUT, change).apply()
        adapter.notifyDataSetChanged()
        setupRecyclerView()
        ActivityCompat.invalidateOptionsMenu(requireActivity())
    }

    // Methods for search option.
    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) searchTroughDatabase(query)
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) searchTroughDatabase(query)
        return true
    }

    // Set search option.
    private fun searchTroughDatabase(query: String) {
        archiveViewModel.searchDatabase("%$query%").observe(this, { list ->
            list?.let { adapter.setData(it) }
        })
    }

    // Destroy all references of the fragment to avoid memory leak.
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}