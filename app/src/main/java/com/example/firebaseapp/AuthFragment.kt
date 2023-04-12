package com.example.firebaseapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.firebaseapp.databinding.FragmentAuthBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class AuthFragment : Fragment(R.layout.fragment_auth) {

    private lateinit var binding : FragmentAuthBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAuthBinding.bind(view)
        setupSignUpButton()
        setupSignInOutButton()
    }
    override fun onStart() {
        super.onStart()
        if(Firebase.auth.currentUser == null ){
            initViewsForSignOutState()
        }else{
            initViewsForSignInState()
        }
    }

    private fun setupSignUpButton(){
        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if(email.isEmpty() || password.isEmpty()){
                Snackbar.make(binding.root, "이메일 또는 패스워드를 입력해주세요", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Firebase.auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Snackbar.make(binding.root, "회원가입에 성공했습니다.", Snackbar.LENGTH_SHORT).show()

                    //로그인 된 상태
                    initViewsForSignInState()
                } else{
                    Snackbar.make(binding.root, "회원가입에 실패했습니다.", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    //로그인 로그아웃 버튼
    private fun setupSignInOutButton(){
        binding.signInOutButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if(Firebase.auth.currentUser == null){
                //로그인
                if(email.isEmpty() || password.isEmpty()){
                    Snackbar.make(binding.root, "이메일 또는 패스워드를 입력해주세요", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                Firebase.auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener {task ->
                        if(task.isSuccessful){
//                            val action = AuthFragmentDirections.actionAuthFragmentToHomeFragment()
//                            findNavController().navigate(action)
                            //로그아웃으로 바꾸기
                            initViewsForSignInState()
                        }else{
                            Snackbar.make(binding.root, "로그인에 실패했습니다. 이메일 또는 패스워드를 입력해주세요", Snackbar.LENGTH_SHORT).show()
                        }
                    }

            }else{
                //로그아웃

                Firebase.auth.signOut()
                //todo 로그인으로 바꾸기
                initViewsForSignOutState()
            }
        }
    }

    private fun initViewsForSignOutState(){
        binding.emailEditText.text.clear()
        binding.emailEditText.isEnabled = true
        binding.passwordEditText.text.clear()
        binding.passwordEditText.isVisible = true
        binding.signInOutButton.text = getString(R.string.SignIn)
        binding.signUpButton.isEnabled = true
    }

    private fun initViewsForSignInState(){
        binding.emailEditText.setText(Firebase.auth.currentUser?.email)
        binding.emailEditText.isEnabled = false
        binding.passwordEditText.isVisible = false
        binding.signInOutButton.text = getString(R.string.SignOut)
        binding.signUpButton.isEnabled = false

    }
}