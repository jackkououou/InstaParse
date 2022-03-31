package com.example.instaparse.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.instaparse.LoginActivity
import com.example.instaparse.MainActivity
import com.example.instaparse.Post
import com.example.instaparse.R
import com.parse.ParseFile
import com.parse.ParseUser
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class ComposeFragment : Fragment() {

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    lateinit var ivPreview: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO set on click listenr and logic

        ivPreview = view.findViewById<ImageView>(R.id.imageView)

        view.findViewById<Button>(R.id.btSubmit).setOnClickListener {

            val description = view.findViewById<EditText>(R.id.etDescription).text.toString()
            val user = ParseUser.getCurrentUser()
            if(photoFile != null) {
                submitPost(description, user, photoFile!!)
                view.findViewById<EditText>(R.id.etDescription).editableText.clear()
                view.findViewById<ImageView>(R.id.imageView).setImageResource(android.R.drawable.ic_menu_camera)
                Toast.makeText(requireContext(), "Posted!", Toast.LENGTH_SHORT).show()
            } else {
                Log.e(MainActivity.TAG, "Error, the image is null")
            }
        }
        view.findViewById<Button>(R.id.btOpenCam).setOnClickListener {
            onLaunchCamera()
        }
        view.findViewById<Button>(R.id.btLogout).setOnClickListener {
            logOutUser()
        }
    }

    fun submitPost(description: String, user: ParseUser, file: File) {
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))
        post.saveInBackground { exception ->
            if (exception != null) {
                Log.e(MainActivity.TAG, "Error saving post")
                exception.printStackTrace()
                Toast.makeText(requireContext(), "Error submitting post", Toast.LENGTH_SHORT).show()
            } else {
                Log.i(MainActivity.TAG, "Successfully sent post")
                // reset edit field for later posts
                // reset image view
            }
        }
    }

    fun onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {
            val fileProvider: Uri = FileProvider.getUriForFile(requireContext(), "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), MainActivity.TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(MainActivity.TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // by this point we have the camera photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // RESIZE BITMAP, see section below
                val bytes = ByteArrayOutputStream()
                takenImage.compress(Bitmap.CompressFormat.JPEG, 40, bytes)
                val resizedFile = getPhotoFileUri(photoFileName+"_resized")
                resizedFile.createNewFile()
                val fos = FileOutputStream(resizedFile)
                fos.write(bytes.toByteArray())
                fos.close()
                // Load the taken image into a preview
                val resizedImage = BitmapFactory.decodeFile(resizedFile!!.absolutePath)
                ivPreview.setImageBitmap(resizedImage)
            } else { // Result was a failure
                Toast.makeText(requireContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun logOutUser() {
//        ParseUser.logOut()
//        Toast.makeText(requireContext() , "Logging out", Toast.LENGTH_SHORT).show()
//        val intent = Intent(requireContext(), LoginActivity::class.java)
//        startActivity(intent)
//        finish()

    }

    companion object {
        private const val TAG = "ComposeFragment"
    }
}