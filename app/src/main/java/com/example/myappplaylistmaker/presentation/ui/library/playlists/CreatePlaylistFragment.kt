package com.example.myappplaylistmaker.presentation.ui.library.playlists

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.databinding.FragmentCreationBinding
import com.example.myappplaylistmaker.presentation.view_models.create_playlist.CreatePlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistFragment() : Fragment() {

    private var _binding: FragmentCreationBinding? = null
    private val binding get() = _binding!!
    private val createPlaylistViewModel by viewModel<CreatePlaylistViewModel>()
    private var nameInput: String = ""
    private var descriptionInput: String = ""
    private var uploadedCover: Uri? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                uploadedCover = uri
                binding.addPhoto.setImageURI(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateButtonState()
        editName()
        editDescription()

        binding.arrow.setOnClickListener {
            confirmDialog()
        }

        binding.addPhoto.setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.buttonCreate.setOnClickListener {
            showSnackbar()
            savePlaylist()
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    confirmDialog()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun editName() {
        binding.editName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                nameInput = s.toString()
                updateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun editDescription() {
        binding.editDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                descriptionInput = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateButtonState() {
        binding.buttonCreate.isEnabled = !(nameInput.isBlank() || nameInput.isEmpty())
    }

    private fun confirmDialog() {
        if (nameInput.isNotEmpty() || descriptionInput.isNotEmpty() ||
            nameInput.isNotBlank() || descriptionInput.isNotBlank() ||
            uploadedCover != null
        ) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.saveConfirmTitle)
                .setMessage(R.string.saveConfirmMessage)
                .setNeutralButton(R.string.cancel) { dialog, witch ->
                }.setNegativeButton(R.string.finish) { dialog, witch ->
                    findNavController().navigateUp()
                }.show()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri): String {
        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            getString(R.string.myAlbum)
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        outputStream.close()
        inputStream?.close()
        return file.absolutePath
    }

    private fun showSnackbar() {
        val playlistName = binding.editName.text.toString().trim()
        if (playlistName.isNotEmpty()) {
            val snackbar =
                Snackbar.make(binding.root, getString(R.string.playlistCreated, playlistName), Snackbar.LENGTH_LONG)
            val textView =
                snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            val typeface = ResourcesCompat.getFont(requireContext(), R.font.ys_display_regular)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            textView.setTypeface(typeface)
            snackbar.show()
        }
    }

    private fun savePlaylist() {
        val name = binding.editName.text.toString().trim()
        val descriptor = binding.editDescription.text.toString().trim()
        val playlistCover = uploadedCover?.let { saveImageToPrivateStorage(it) } ?: ""
        createPlaylistViewModel.addPlaylist(name, descriptor, playlistCover)
        parentFragmentManager.popBackStack()
    }
}