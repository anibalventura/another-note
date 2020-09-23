package com.anibalventura.anothernote.ui.note

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.anibalventura.anothernote.Constants.NOTE_TO_ARCHIVE
import com.anibalventura.anothernote.Constants.NOTE_TO_EMPTY
import com.anibalventura.anothernote.Constants.NOTE_TO_TRASH
import com.anibalventura.anothernote.Constants.NOTE_VIEW
import com.anibalventura.anothernote.Constants.SWIPE_ARCHIVE
import com.anibalventura.anothernote.Constants.SWIPE_DELETE
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.adapters.NoteAdapter
import com.anibalventura.anothernote.data.models.ArchiveData
import com.anibalventura.anothernote.data.models.TrashData
import com.anibalventura.anothernote.data.viewmodel.NoteViewModel
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.databinding.FragmentNoteBinding
import com.anibalventura.anothernote.utils.SwipeItem
import com.anibalventura.anothernote.utils.hideKeyboard
import com.anibalventura.anothernote.utils.sharedPref
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class NoteFragment : Fragment(), SearchView.OnQueryTextListener {

    // ViewModels.
    private val noteViewModel: NoteViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    // DataBinding.
    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!

    // Adapter.
    private val adapter: NoteAdapter by lazy { NoteAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // DataBinding.
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel

        // Setup RecyclerView
        setupRecyclerView()

        // Get notify every time the database change.
        noteViewModel.getAllData.observe(viewLifecycleOwner, { data ->
            sharedViewModel.checkIfNoteIsEmpty(data)
            adapter.setData(data)
        })

        // Set menu.
        setHasOptionsMenu(true)

        // Hide soft keyboard.
        hideKeyboard(requireActivity())

        return binding.root
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.noteRecyclerView
        recyclerView.adapter = adapter

        when (sharedPref(requireContext()).getBoolean(NOTE_VIEW, false)) {
            true -> recyclerView.layoutManager = LinearLayoutManager(requireActivity())
            false -> recyclerView.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        recyclerView.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 300 // Milliseconds
        }

        // Swipe to delete.
        swipeToArchiveOrDelete(SWIPE_DELETE, recyclerView)

        // Swipe to archive.
        swipeToArchiveOrDelete(SWIPE_ARCHIVE, recyclerView)
    }

    private fun swipeToArchiveOrDelete(action: Int, recyclerView: RecyclerView) {
        lateinit var background: Drawable
        lateinit var icon: Drawable

        when (action) {
            SWIPE_DELETE -> {
                background =
                    ResourcesCompat.getDrawable(resources, R.drawable.bg_swipe_delete, null)!!
                icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_trash, null)!!
            }
            SWIPE_ARCHIVE -> {
                background =
                    ResourcesCompat.getDrawable(resources, R.drawable.bg_swipe_archive, null)!!
                icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_archive, null)!!
            }
        }

        val swipeToDeleteCallBack = object : SwipeItem(action, background, icon, requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, action: Int) {

                val noteItem = adapter.dataList[viewHolder.adapterPosition]
                val archiveItem = ArchiveData(
                    adapter.dataList[viewHolder.adapterPosition].id,
                    adapter.dataList[viewHolder.adapterPosition].title,
                    adapter.dataList[viewHolder.adapterPosition].description
                )
                val trashItem = TrashData(
                    adapter.dataList[viewHolder.adapterPosition].id,
                    adapter.dataList[viewHolder.adapterPosition].title,
                    adapter.dataList[viewHolder.adapterPosition].description
                )

                when (action) {
                    SWIPE_ARCHIVE -> sharedViewModel.moveItem(
                        NOTE_TO_ARCHIVE,
                        noteItem,
                        archiveItem,
                        trashItem,
                        requireView()
                    )
                    SWIPE_DELETE -> sharedViewModel.moveItem(
                        NOTE_TO_TRASH,
                        noteItem,
                        archiveItem,
                        trashItem,
                        requireView()
                    )
                }
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        // Enable required options.
        menu.findItem(R.id.menu_main_search).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_main_list).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_main_grid).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_main_sort_by).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_main_sort_title).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_main_delete_all).setEnabled(true).isVisible = true
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val search = menu.findItem(R.id.menu_main_search).actionView as? SearchView
        val list = menu.findItem(R.id.menu_main_list)
        val grid = menu.findItem(R.id.menu_main_grid)

        search?.isSubmitButtonEnabled = true
        search?.setOnQueryTextListener(this)

        when (sharedPref(requireContext()).getBoolean(NOTE_VIEW, false)) {
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
            R.id.menu_main_list -> changeNoteView(true)
            R.id.menu_main_grid -> changeNoteView(false)
            R.id.menu_main_delete_all -> sharedViewModel.emptyData(requireContext(), NOTE_TO_EMPTY)
            R.id.menu_main_sort_title -> noteViewModel.sortByTitle.observe(this, {
                adapter.setData(it)
            })
        }
        return super.onOptionsItemSelected(item)
    }

    @Suppress("DEPRECATION")
    private fun changeNoteView(change: Boolean) {
        sharedPref(requireContext()).edit().putBoolean(NOTE_VIEW, change).apply()
        adapter.notifyDataSetChanged()
        setupRecyclerView()
        invalidateOptionsMenu(requireActivity())
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
        noteViewModel.searchDatabase("%$query%").observe(this, { list ->
            list?.let {
                adapter.setData(it)
            }
        })
    }

    // Destroy all references of the fragment to avoid memory leak.
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}