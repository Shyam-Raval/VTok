package com.example.vtok

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()
    fun resetState(){

    }

    fun onSignInResult(signInResult: SignInResult) {
        _state.update {
             it.copy(
                 isSignedIn = signInResult.data != null,
                 signInError= signInResult.errorMessage

             )
        }
    }
}