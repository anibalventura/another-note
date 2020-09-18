package com.anibalventura.anothernote.ui.add

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.data.models.NoteData
import com.anibalventura.anothernote.data.viewmodel.NoteViewModel
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.utils.showToast
import kotlinx.android.synthetic.main.fragment_add.*

class AddFragment : Fragment() {

    private val noteViewModel: NoteViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        // Set menu.
        setHasOptionsMenu(true)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_menu_check -> insertDataToDb()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertDataToDb() {
        val title = tvAddTitle.text.toString()
        val description = etAddNote.text.toString()

        when (sharedViewModel.verifyData(title, description)) {
            true -> {
                // Insert data to database.
                val newData =
                    NoteData(0, title, description)
                noteViewModel.insertData(newData)
                showToast(requireContext(), "Note added.")
                // Navigate back.
                findNavController().navigate(R.id.action_addFragment_to_listFragment)
            }
            else -> showToast(requireContext(), "Please fill out all fields.")
        }
    }
}