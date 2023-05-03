package com.nipunapps.photo_picker_13

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhotoPickerActivity : AppCompatActivity() {

    companion object {
        const val PICK_TYPE = "PICK_TYPE"
    }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                captureImage()
                return@registerForActivityResult
            }
            setResult(RESULT_CANCELED)
            finish()
        }

    private val galleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                pickImage()
                return@registerForActivityResult
            }
            setResult(RESULT_CANCELED)
            finish()
        }

    private var pickType = PickType.GALLERY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.let { i ->
            pickType = i.getSerializableExtra(PICK_TYPE) as PickType
        }
        startPickingImage()
    }

    private fun startPickingImage() {
        when (pickType) {
            PickType.GALLERY -> {
                pickImageFromGallery()
            }

            PickType.CAMERA -> {
                pickImageFromCamera()
            }
        }
    }


    private fun pickImageFromGallery() {
        val permission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES
            else Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            pickImage()
            return
        }
        galleryPermissionLauncher.launch(permission)
    }

    private fun pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            android11OrHigherImagePickLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
            return
        }
        android10orBelowImagePickLauncher.launch("image/*")
    }

    private fun pickImageFromCamera() {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            captureImage()
            return
        }
        cameraPermissionLauncher.launch(permission)
    }

    private var photoUri: Uri? = null

    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(this, "${packageName}.provider", photoFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        takePictureLauncher.launch(intent)
    }

    private val android11OrHigherImagePickLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia(), this::onImageSelected)

    private val android10orBelowImagePickLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent(), this::onImageSelected)

    private fun onImageSelected(uri: Uri?, hasUriPermission: Boolean = false) {
        uri?.let { imageUri ->
            if (!hasUriPermission)
                contentResolver.takePersistableUriPermission(
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            val resIntent = Intent()
            resIntent.data = imageUri
            setResult(RESULT_OK,resIntent)
            finish()
            return
        }
        setResult(RESULT_CANCELED)
        finish()
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            onImageSelected(photoUri, hasUriPermission = true)
            return@registerForActivityResult
        }
        setResult(RESULT_CANCELED)
        finish()
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = File(cacheDir, "images")
        storageDir.mkdirs()
        return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
    }
}