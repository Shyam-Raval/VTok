package com.example.vtok

import com.google.firebase.Timestamp


data class SignInResult(
    val data: UserData?,
    val errorMessage: String?

)

data class UserData(
    val email: String?="",
    val userId: String="",
    val username: String?="",
    val ppurl: String?="",
    val bio : String =""

    )

data class AppState(
    val isSignedIn: Boolean = false,
    val userData: UserData? = null,
    val signInError: String? = null,
    val srEmail: String = "",
    val showDialog: Boolean = false,
    val User2: ChatUserData?= null,
    val chatId: String=""

)

data class ChatData(
    val chatId: String = "",
    val last: Message? = null,
    val user1: ChatUserData? = null,
    val user2: ChatUserData? = null

)

data class Message(
    val msgId: String = "",
    val senderId: String = "",
    val repliedMessage: Message? = null,
    val reaction: List<Reaction>? = emptyList(),
    val imageUrl: String = "",
    val fileUrl: String = "",
    val fileName: String = "",
    val fileSize: String = "",
    val vidUrl: String = "",
    val progress: String = "",
    val content: String = "",
    val time: Timestamp? = null,
    val read: Boolean = false,
    val forwarded: Boolean = false,


    )

data class Reaction(
    val ppurl : String = "",
    val username: String = "",
    val userId: String = "",
    val reaction : String = "",
)

data class ChatUserData(
    val userId: String = "",
    val typing : Boolean = false,
    val bio : String = "",
    val username : String? = "",
    val ppurl : String = "",

    val email : String = "",
    val status : Boolean = false,
    val unread : Int = 0

)

data class Image(
    val imgUrl :String = "",
    val time : Timestamp?= Timestamp.now(),
)
data class Story(
    val id:String="",
    val userId: String ="",
    var userName:String?="",
    val ppurl: String="",
    val images: List<Image> = emptyList()
)