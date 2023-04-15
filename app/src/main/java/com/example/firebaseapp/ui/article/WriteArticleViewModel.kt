package com.example.firebaseapp.ui.article

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// 뷰 모델 클래스
// 안드로이드 아키텍쳐에서 사용하는 뷰모델
class WriteArticleViewModel : ViewModel() {

    //데이터가 바뀔 때마다 데이터를 저장하여 양방향으로 데이터를 주고 받는 역할
    private var _selectedUri = MutableLiveData<Uri?>()
    var selectedUri : LiveData<Uri?> = _selectedUri

    fun updateSelectedUri(uri : Uri?){
        _selectedUri.value = uri

    }
}