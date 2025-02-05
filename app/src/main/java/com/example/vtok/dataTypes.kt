package com.example.vtok




data class SignInResult(
    val data : UserData?,
            val errorMessage:String?

    )

data class UserData(
    val email: String?,
    val userId: String,
    val username: String?,
    val ppurl: String?,

)

data class AppState(
    val isSignedIn: Boolean = false,
    val userData : UserData?= null,

    )


