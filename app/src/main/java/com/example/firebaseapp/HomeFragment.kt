package com.example.firebaseapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.firebaseapp.data.ArticleModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Firebase.firestore

        db.collection("articles").document("5DJdP0UlelFCoyQxRtus")
            .get()
            .addOnSuccessListener {result ->
                val article =  result.toObject<ArticleModel>()

                Log.e("homeFragment", article.toString())
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

}