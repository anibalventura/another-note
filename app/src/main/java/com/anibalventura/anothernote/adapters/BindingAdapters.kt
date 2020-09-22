package com.anibalventura.anothernote.adapters

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.data.models.ArchiveData
import com.anibalventura.anothernote.data.models.NoteData
import com.anibalventura.anothernote.data.models.TrashData
import com.anibalventura.anothernote.ui.archive.ArchiveFragmentDirections
import com.anibalventura.anothernote.ui.note.NoteFragmentDirections
import com.anibalventura.anothernote.ui.trash.TrashFragmentDirections
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BindingAdapters {

    companion object {

        @JvmStatic
        @BindingAdapter("android:emptyDatabase")
        fun emptyDatabase(view: View, emptyDatabase: MutableLiveData<Boolean>) {
            when (emptyDatabase.value) {
                true -> view.visibility = View.VISIBLE
                false -> view.visibility = View.INVISIBLE
            }
        }

        /** ============================= NoteFragment ============================= **/
        @JvmStatic
        @BindingAdapter("android:navigateToNoteAddFragment")
        fun navigateToNoteAddFragment(view: FloatingActionButton, navigate: Boolean) {
            view.setOnClickListener {
                if (navigate) {
                    view.findNavController().navigate(R.id.action_noteFragment_to_noteAddFragment)
                }
            }
        }

        @JvmStatic
        @BindingAdapter("android:sendDataToNoteUpdateFragment")
        fun sendDataToNoteUpdateFragment(view: ConstraintLayout, currentItem: NoteData) {
            view.setOnClickListener {
                val action =
                    NoteFragmentDirections.actionNoteFragmentToNoteUpdateFragment(currentItem)
                view.findNavController().navigate(action)
            }
        }

        /** ============================= TrashFragment ============================= **/
        @JvmStatic
        @BindingAdapter("android:sendDataToTrashUpdateFragment")
        fun sendDataToTrashUpdateFragment(view: ConstraintLayout, currentItem: TrashData) {
            view.setOnClickListener {
                val action =
                    TrashFragmentDirections.actionTrashFragmentToTrashUpdateFragment(currentItem)
                view.findNavController().navigate(action)
            }
        }

        /** ============================= ArchiveFragment ============================= **/
        @JvmStatic
        @BindingAdapter("android:sendDataToArchiveUpdateFragment")
        fun sendDataToArchiveUpdateFragment(view: ConstraintLayout, currentItem: ArchiveData) {
            view.setOnClickListener {
                val action = ArchiveFragmentDirections.actionArchiveFragmentToArchiveUpdateFragment(
                    currentItem
                )
                view.findNavController().navigate(action)
            }
        }
    }
}