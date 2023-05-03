package com.nipunapps.classmanager

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.nipunapps.classmanager.databinding.ActivityMainBinding
import com.nipunapps.photo_picker_13.PhotoPicker

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show()
                binding.imageView.setImageURI(result.data?.data)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.camera.setOnClickListener {
            val photoPicker = PhotoPicker(this)
            photoPicker.pickImageFromCamera(launcher)
        }

        binding.gallery.setOnClickListener {
            val photoPicker = PhotoPicker(this)
            photoPicker.pickImageFromGallery(launcher)
        }
    }
}