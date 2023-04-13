package com.example.firebaseapp.ui.article

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.fragment.app.Fragment
import com.example.firebaseapp.R
import com.example.firebaseapp.databinding.FragmentWriteBinding
import 	androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class WriteArticleFragment : Fragment(R.layout.fragment_write) {

    private lateinit var binding : FragmentWriteBinding

    private var selectedUri : Uri? = null

    // Registers a photo picker activity launcher in single-select mode.
    private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            selectedUri = uri
            binding.photoImageView.setImageURI(uri)
        } else {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWriteBinding.bind(view)

        // Launch the photo picker and allow the user to choose only images.
        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))

        binding.photoImageView.setOnClickListener {

        }

        binding.deleteButton.setOnClickListener {

        }

        binding.submitButton.setOnClickListener {
            if(selectedUri != null){
                val photoUril = selectedUri ?: return@setOnClickListener
                //  업로드 과정 작성
                val fileName = "${UUID.randomUUID()}.png"
                Firebase.storage.reference.child("articles/photo").child(fileName)
                    .putFile(photoUril)
                    .addOnCompleteListener{task ->
                        if(task.isSuccessful){
                            //다운로드 url 변경
                            Firebase.storage.reference.child("articles/photo/$fileName")
                                .downloadUrl
                                .addOnSuccessListener {
                                    Log.e("aa", it.toString())
                                }
                                .addOnFailureListener {

                                }
                        }else{
                            //에러
                            task.exception?.printStackTrace()
                        }
                    }
            }else
                Snackbar.make(view, "이미지가 업로드 되지 않았습니다.", Snackbar.LENGTH_SHORT).show()
        }


        //뒤로가기
        binding.backButton.setOnClickListener {
            findNavController().navigate(WriteArticleFragmentDirections.actionBack())
        }
    }


}