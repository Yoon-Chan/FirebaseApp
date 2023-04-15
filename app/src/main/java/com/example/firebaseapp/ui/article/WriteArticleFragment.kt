package com.example.firebaseapp.ui.article

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.fragment.app.Fragment
import com.example.firebaseapp.R
import com.example.firebaseapp.databinding.FragmentWriteBinding
import    androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.example.firebaseapp.data.ArticleModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class WriteArticleFragment : Fragment(R.layout.fragment_write) {

    private lateinit var binding: FragmentWriteBinding
    private lateinit var viewModel : WriteArticleViewModel

    // Registers a photo picker activity launcher in single-select mode.
    private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            viewModel.updateSelectedUri(uri)
        } else {
            Toast.makeText(context, "무슨 상태??", Toast.LENGTH_SHORT).show()
            //hideProgress()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWriteBinding.bind(view)
        setupViewModel()

        // Launch the photo picker and allow the user to choose only images.

        if(viewModel.selectedUri.value == null){
            startPicker()
        }
        setupPhotoImageView()
        setupDeleteButton()
        setupSubmitButton(view)


        //뒤로가기
        setupBackButton()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity()).get<WriteArticleViewModel>()

        viewModel.selectedUri.observe(viewLifecycleOwner) {
            binding.photoImageView.setImageURI(it)

            if (it != null) {
                binding.addButton.isVisible = false
                binding.deleteButton.isVisible = true
            } else {
                binding.deleteButton.isVisible = false
                binding.addButton.isVisible = true
            }


        }
    }


    private fun setupDeleteButton() {
        binding.deleteButton.setOnClickListener {
            viewModel.updateSelectedUri(null)
        }
    }

    private fun setupPhotoImageView() {
        binding.photoImageView.setOnClickListener {
            if (viewModel.selectedUri.value == null) {
                startPicker()
            }
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigate(WriteArticleFragmentDirections.actionBack())
        }
    }

    private fun setupSubmitButton(view: View) {
        binding.submitButton.setOnClickListener {
            showProgress()
            if (viewModel.selectedUri.value != null) {
                val photoUrl = viewModel.selectedUri.value ?: return@setOnClickListener
                //  업로드 과정 작성
                uploadImage(photoUrl, successHandler = {
                    uploadArticle(it, binding.descriptionEditText.text.toString())
                },
                    errorHandler = {
                        Snackbar.make(view, "이미지 업로드에 실패했습니다.", Snackbar.LENGTH_SHORT).show()
                        hideProgress()
                    })
            } else
                Snackbar.make(view, "이미지가 업로드 되지 않았습니다.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun startPicker() {
        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
    }

    private fun showProgress(){
        binding.progressBarLayout.isVisible = true

    }

    private fun hideProgress(){
        binding.progressBarLayout.isVisible = false
    }

    private fun uploadImage(
        uri: Uri,
        successHandler: (String) -> Unit,
        errorHandler: (Throwable?) -> Unit
    ) {
        val fileName = "${UUID.randomUUID()}.png"
        Firebase.storage.reference.child("articles/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //다운로드 url 변경
                    Firebase.storage.reference.child("articles/photo/$fileName")
                        .downloadUrl
                        .addOnSuccessListener {
                            successHandler(it.toString())
                        }
                        .addOnFailureListener {
                            errorHandler(it)
                        }
                } else {
                    //에러
                    errorHandler(task.exception)
                }
            }
    }

    private fun uploadArticle(photoUrl: String, description: String) {
        //새로운 문서 Id
        val articleId = UUID.randomUUID().toString()
        val articleModel = ArticleModel(
            articleId = articleId,
            createdAt = System.currentTimeMillis(),
            description = description,
            imageUrl = photoUrl,
        )
        Firebase.firestore.collection("articles").document(articleId)
            .set(articleModel)
            .addOnSuccessListener {
                findNavController().navigate(WriteArticleFragmentDirections.actionWriteArticleFragmentToHomeFragment())
                hideProgress()
            }
            .addOnFailureListener {
                it.printStackTrace()
                view?.let { view -> Snackbar.make(view, "글 작성에 실패했습니다.", Snackbar.LENGTH_SHORT).show() }
                hideProgress()
            }


        //업로드 끝나면 프로세스바 사라지기
        hideProgress()
    }

}