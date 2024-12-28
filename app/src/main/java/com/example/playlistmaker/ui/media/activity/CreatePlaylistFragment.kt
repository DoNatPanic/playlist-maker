package com.example.playlistmaker.ui.media.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.domain.media.entity.Playlist
import com.example.playlistmaker.presentation.utils.OnSwipeTouchListener
import com.example.playlistmaker.ui.media.view_model.CreatePlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.UUID


class CreatePlaylistFragment : Fragment() {

    private val viewModel: CreatePlaylistViewModel by viewModel()

    private lateinit var binding: FragmentCreatePlaylistBinding

    private var isPictureSelected: Boolean = false

    private var filePath: String? = null

    // Описали callback для обработки нажатия на Back
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            closeFragment()
            disableCallback()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Добавляем созданный callback к Dispatcher
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // если поле Название не заполнено, кнопка Создать неактивна
        binding.createPlaylistBtn.isEnabled = false

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.createPlaylistBtn.isEnabled =
                    binding.playlistNameInput.text.toString().isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        binding.playlistNameInput.addTextChangedListener(simpleTextWatcher)

        // назад
        binding.toolbar.setNavigationOnClickListener {
            closeFragment()
        }

        // нажали на кнопку Создать
        binding.createPlaylistBtn.setOnClickListener { _ -> createPlaylist() }

        // регистрируем событие, которое вызывает photo picker
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                //обрабатываем событие выбора пользователем фотографии
                if (uri != null) {
                    binding.pickerImage.setImageURI(uri)
                    saveImageToPrivateStorage(uri)
                    isPictureSelected = true
                } else {
                    // empty
                }
            }

        // по нажатию на кнопку pickImage запускаем photo picker
        binding.pickerImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.main.setOnTouchListener(object : OnSwipeTouchListener(requireActivity()) {
            override fun onSwipeLeft() {
                closeFragment()
            }
        })

    }

    private fun disableCallback() {
        // Отключаем callback
        callback.isEnabled = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun closeFragment() {
        if (binding.playlistNameInput.text.toString().isNotEmpty() || isPictureSelected) {
            showDialog()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        //создаём экземпляр класса File, который указывает на нужный каталог
        val path =
            File(activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        //создаем каталог, если он не создан
        if (!path.exists()) {
            path.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val newUUID = randomUUID()
        filePath = "$path/$newUUID"
        val file = File(path, newUUID)
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = activity?.contentResolver?.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    fun randomUUID() = UUID.randomUUID().toString()

    private fun showDialog() {
        activity?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("Завершить создание плейлиста?")
                .setMessage("Все несохраненные данные будут потеряны» и двумя кнопками:")
                .setNeutralButton("Отмена") { dialog, which ->
                    // empty
                }
                .setPositiveButton("Завершить") { dialog, which ->
                    findNavController().popBackStack()
                }
                .show()
        }
    }

    private fun createPlaylist() {
        val playlist = Playlist(
            playlistName = binding.playlistNameInput.text.toString(),
            playlistInfo = binding.infoInput.text.toString(),
            playlistImgPath = filePath,
            trackIds = "",
            tracksCount = 0
        )
        viewModel.onCreatePlaylistClicked(playlist)
        findNavController().popBackStack()
        Toast.makeText(
            activity,
            "Плейлист ${binding.playlistNameInput.text} создан",
            Toast.LENGTH_SHORT
        ).show()
    }
}