package com.anibalventura.anothernote.ui.note

import android.app.AlertDialog
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
import com.anibalventura.anothernote.CONST
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.data.models.ArchiveData
import com.anibalventura.anothernote.data.models.NoteData
import com.anibalventura.anothernote.data.models.TrashData
import com.anibalventura.anothernote.data.viewmodel.ArchiveViewModel
import com.anibalventura.anothernote.data.viewmodel.NoteViewModel
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.data.viewmodel.TrashViewModel
import com.anibalventura.anothernote.databinding.FragmentNoteBinding
import com.anibalventura.anothernote.ui.note.adapter.NoteAdapter
import com.anibalventura.anothernote.utils.SwipeItem
import com.anibalventura.anothernote.utils.hideKeyboard
import com.anibalventura.anothernote.utils.sharedPref
import com.anibalventura.anothernote.utils.showToast
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class NoteFragment : Fragment(), SearchView.OnQueryTextListener {

    // ViewModels
    private val noteViewModel: NoteViewModel by viewModels()
    private val archiveViewModel: ArchiveViewModel by viewModels()
    private val trashViewModel: TrashViewModel by viewModels()
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

        when (sharedPref(requireContext()).getBoolean(CONST.NOTE_VIEW, false)) {
            true -> recyclerView.layoutManager = LinearLayoutManager(requireActivity())
            false -> recyclerView.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        recyclerView.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 300 // Milliseconds
        }

        // Swipe to delete.
        swipeToArchiveOrDelete(CONST.DELETE_ITEM, recyclerView)

        // Swipe to archive.
        swipeToArchiveOrDelete(CONST.ARCHIVE_ITEM, recyclerView)
    }

    private fun swipeToArchiveOrDelete(direction: Int, recyclerView: RecyclerView) {
        lateinit var background: Drawable
        lateinit var icon: Drawable

        when (direction) {
            CONST.DELETE_ITEM -> {
                background =
                    ResourcesCompat.getDrawable(resources, R.drawable.bg_swipe_delete, null)!!
                icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_trash, null)!!
            }
            CONST.ARCHIVE_ITEM -> {
                background =
                    ResourcesCompat.getDrawable(resources, R.drawable.bg_swipe_archive, null)!!
                icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_archive, null)!!
            }
        }

        val swipeToDeleteCallBack = object : SwipeItem(direction, background, icon) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val deletedItem = adapter.dataList[viewHolder.adapterPosition]
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

                when (direction) {
                    // Send note to trash.
                    CONST.DELETE_ITEM -> trashViewModel.insertData(trashItem)
                    // Send note to archive.
                    CONST.ARCHIVE_ITEM -> archiveViewModel.insertData(archiveItem)
                }

                // Delete note.
                noteViewModel.deleteItem(deletedItem)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)

                // Restore data.
                restoreItem(direction, viewHolder.itemView, deletedItem, archiveItem, trashItem)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    /*
     * Restore data when archive or delete.
     */
    private fun restoreItem(
        direction: Int,
        view: View,
        deletedItem: NoteData,
        archiveItem: ArchiveData,
        trashItem: TrashData
    ) {
        when (direction) {
            CONST.DELETE_ITEM ->
                Snackbar.make(view, "Archive \"${deletedItem.title}\"", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        noteViewModel.insertData(deletedItem)
                        trashViewModel.deleteItem(trashItem)
                    }.show()
            CONST.ARCHIVE_ITEM ->
                Snackbar.make(view, "Archive \"${deletedItem.title}\"", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        noteViewModel.insertData(deletedItem)
                        archiveViewModel.deleteItem(archiveItem)
                    }.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_note, menu)

        val search = menu.findItem(R.id.note_menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val list = menu.findItem(R.id.note_menu_view_list)
        val grid = menu.findItem(R.id.note_menu_view_grid)

        when (sharedPref(requireContext()).getBoolean(CONST.NOTE_VIEW, false)) {
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
            R.id.note_menu_delete_all -> confirmDeleteAll()
            R.id.note_menu_view_list -> changeNoteView(true)
            R.id.note_menu_view_grid -> changeNoteView(false)
            R.id.note_menu_title -> noteViewModel.sortByTitle.observe(this, {
                adapter.setData(it)
            })
        }
        return super.onOptionsItemSelected(item)
    }

    @Suppress("DEPRECATION")
    private fun changeNoteView(change: Boolean) {
        sharedPref(requireContext()).edit().putBoolean(CONST.NOTE_VIEW, change).apply()
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

    private fun confirmDeleteAll() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Delete All")
        dialogBuilder.setTitle("Delete All")
        dialogBuilder.setMessage("Are you sure you want to delete everything?")
        dialogBuilder.setPositiveButton("Yes") { _, _ ->
            noteViewModel.deleteAll()
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