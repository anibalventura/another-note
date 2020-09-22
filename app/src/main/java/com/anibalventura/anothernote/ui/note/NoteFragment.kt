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
import com.anibalventura.anothernote.Constants.ARCHIVE_NOTE
import com.anibalventura.anothernote.Constants.DELETE_NOTE
import com.anibalventura.anothernote.Constants.NOTE_VIEW
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

    // ViewModels.
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

        when (sharedPref(requireContext()).getBoolean(NOTE_VIEW, false)) {
            true -> recyclerView.layoutManager = LinearLayoutManager(requireActivity())
            false -> recyclerView.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        recyclerView.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 300 // Milliseconds
        }

        // Swipe to delete.
        swipeToArchiveOrDelete(DELETE_NOTE, recyclerView)

        // Swipe to archive.
        swipeToArchiveOrDelete(ARCHIVE_NOTE, recyclerView)
    }

    private fun swipeToArchiveOrDelete(action: Int, recyclerView: RecyclerView) {
        lateinit var background: Drawable
        lateinit var icon: Drawable

        when (action) {
            DELETE_NOTE -> {
                background =
                    ResourcesCompat.getDrawable(resources, R.drawable.bg_swipe_delete, null)!!
                icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_trash, null)!!
            }
            ARCHIVE_NOTE -> {
                background =
                    ResourcesCompat.getDrawable(resources, R.drawable.bg_swipe_archive, null)!!
                icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_archive, null)!!
            }
        }

        val swipeToDeleteCallBack = object : SwipeItem(action, background, icon) {
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

                archiveOrDeleteNote(action, noteItem, archiveItem, trashItem)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    /*
     * Restore data when archive or delete.
     */
    private fun archiveOrDeleteNote(
        action: Int,
        deletedItem: NoteData,
        archiveItem: ArchiveData,
        trashItem: TrashData
    ) {
        when (action) {
            DELETE_NOTE -> {
                trashViewModel.insertData(trashItem)
                Snackbar.make(
                    requireView(),
                    getString(R.string.snack_moved_trash),
                    Snackbar.LENGTH_LONG
                )
                    .setAction(getString(R.string.snack_undo)) {
                        noteViewModel.insertData(deletedItem)
                        trashViewModel.deleteItem(trashItem)
                    }.show()
            }
            ARCHIVE_NOTE -> {
                archiveViewModel.insertData(archiveItem)
                Snackbar.make(
                    requireView(),
                    getString(R.string.snack_note_archived),
                    Snackbar.LENGTH_LONG
                )
                    .setAction(getString(R.string.snack_undo)) {
                        noteViewModel.insertData(deletedItem)
                        archiveViewModel.deleteItem(archiveItem)
                    }.show()
            }
        }

        // Delete note.
        noteViewModel.deleteItem(deletedItem)
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

    private fun confirmDeleteAll() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(getString(R.string.dialog_delete_all))
        dialogBuilder.setMessage(getString(R.string.dialog_delete_you_sure))
        dialogBuilder.setPositiveButton(getString(R.string.dialog_confirmation)) { _, _ ->
            noteViewModel.deleteAll()
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