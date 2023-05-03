package com.nipunapps.photo_picker_13

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity

class PhotoPicker(
    private val context: Context
) {

    fun pickImageFromGallery(
        launcher: ActivityResultLauncher<Intent>
    ) {
        val intent = Intent(context, PhotoPickerActivity::class.java).apply {
            putExtra(PhotoPickerActivity.PICK_TYPE, PickType.GALLERY)
        }
        launcher.launch(intent)
    }

    fun pickImageFromCamera(
        launcher: ActivityResultLauncher<Intent>
    ) {
        val intent = Intent(context, PhotoPickerActivity::class.java).apply {
            putExtra(PhotoPickerActivity.PICK_TYPE, PickType.CAMERA)
        }
        launcher.launch(intent)
    }
}