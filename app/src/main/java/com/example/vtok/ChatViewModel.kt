package com.example.vtok

import android.content.ContentValues
import android.icu.util.Calendar
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChatViewModel : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()
    private val userCollection = Firebase.firestore.collection(USERS_COLLECTION)
    var userDataListener: ListenerRegistration? = null
    var chats by mutableStateOf<List<ChatData>>(emptyList())
    var chatListener: ListenerRegistration? = null
    var tp by mutableStateOf(ChatData())
    var tpListener: ListenerRegistration? = null
    var reply by mutableStateOf("")
    private val firestore = FirebaseFirestore.getInstance()
    var msgListener: ListenerRegistration? = null
    var messages by mutableStateOf<List<Message>>(listOf())


    fun resetState() {

    }

    fun onSignInResult(signInResult: SignInResult) {
        _state.update {
            it.copy(
                isSignedIn = signInResult.data != null, signInError = signInResult.errorMessage

            )
        }
    }

    fun adduserToFirestore(userData: UserData) {
        val userDataMap = mapOf(
            "userId" to userData?.userId,
            "username" to userData?.username,
            "ppurl" to userData?.ppurl,
            "email" to userData?.email

        )
        val userDocument = userCollection.document(userData.userId)
        userDocument.get().addOnSuccessListener {
            if (it.exists()) {
                userDocument.update(userDataMap).addOnSuccessListener {
                    Log.d(ContentValues.TAG, "user Data added to Firebase successfully ")
                }.addOnFailureListener {
                    Log.d(ContentValues.TAG, "user Data added to Firebase Failed ")
                }
            } else {
                userDocument.set(userDataMap).addOnSuccessListener {
                    Log.d(ContentValues.TAG, "user Data added to Firebase successfully ")
                }.addOnFailureListener {
                    Log.d(ContentValues.TAG, "user Data added to Firebase Failed ")

                }

            }
        }

    }

    fun getUserData(userId: String) {
        userDataListener = userCollection.document(userId).addSnapshotListener { value, error ->
            if (value != null) {
                _state.update {
                    it.copy(userData = value.toObject(UserData::class.java))
                }
            }

        }

    }

    fun hideDialog() {
        _state.update {
            it.copy(
                showDialog = false
            )
        }
    }

    fun showDialog() {
        _state.update {
            it.copy(
                showDialog = true
            )
        }

    }

    fun setSrEmail(email: String) {
        _state.update {
            it.copy(
                srEmail = email
            )
        }

    }

    fun addChat(email: String) {
        Firebase.firestore.collection(CHAT_COLLECTION).where(
            Filter.or(
                Filter.and(
                    Filter.equalTo("user1.email", email),
                    Filter.equalTo("user2.email", state.value.userData?.email)
                ),
                Filter.and(
                    Filter.equalTo("user1.email", state.value.userData?.email),
                    Filter.equalTo("user2.email", email)

                )

            )
        ).get().addOnSuccessListener {
            if (it.isEmpty) {
                userCollection.whereEqualTo("email", email).get().addOnSuccessListener {
                    if (it.isEmpty) {
                        println("Failed")
                    } else {

                        val chatPartner = it.toObjects(UserData::class.java).firstOrNull()
                        val id = Firebase.firestore.collection(CHAT_COLLECTION).document().id
                        val chat = ChatData(
                            chatId = id,
                            last = Message(
                                senderId = "", content = "", time = null
                            ),
                            user1 = ChatUserData(
                                userId = state.value.userData?.userId.toString(),
                                typing = false,
                                bio = state.value.userData?.bio.toString(),
                                username = state.value.userData?.username.toString(),
                                ppurl = state.value.userData?.ppurl.toString(),
                                email = state.value.userData?.email.toString(),


                                ),
                            user2 = ChatUserData(

                                typing = false,
                                bio = chatPartner?.bio.toString(),
                                username = chatPartner?.username.toString(),
                                ppurl = chatPartner?.ppurl.toString(),
                                userId = chatPartner?.userId.toString(),
                                email = chatPartner?.email.toString(),


                                ),
                        )
                        Firebase.firestore.collection(CHAT_COLLECTION).document().set(chat)


                    }
                }
            }
        }
    }

    fun showChats(userId: String) {
        chatListener = Firebase.firestore.collection(CHAT_COLLECTION).where(
            Filter.or(
                Filter.equalTo("user1.userId", userId),
                Filter.equalTo("user2.userId", userId)

            )

        ).addSnapshotListener { value, error ->
            if (value != null) {
                chats = value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }.sortedBy {
                    it.last?.time
                }.reversed()
            }
        }
    }

    fun getTp(chatId: String) {
        tpListener?.remove()
        tpListener = Firebase.firestore.collection(CHAT_COLLECTION).document(chatId)
            .addSnapshotListener { snp, err ->
                if (snp != null) {
                    tp = snp.toObject(ChatData::class.java) ?: ChatData()
                }
            }
    }

    fun setChatUser(usr: ChatUserData, id: String) {
        _state.update {
            it.copy(
                User2 = usr, chatId = id
            )
        }

    }

    fun sendReply(
        chatId: String,
        replyMessage: Message = Message(),
        msg: String,
        senderId: String = state.value.userData?.userId.toString(),


        ) {
        val id = Firebase.firestore.collection(CHAT_COLLECTION).document().collection(
            MESSAGES_COLLECTION
        ).document().id
        val time = Calendar.getInstance().time
        val message = Message(
            msgId = id,
            repliedMessage = replyMessage,
            senderId = senderId,
            content = msg,
            time = Timestamp(date = time)
        )
        Firebase.firestore.collection(CHAT_COLLECTION).document(chatId).collection(
            MESSAGES_COLLECTION
        ).document(id).set(message)
        firestore.collection(CHAT_COLLECTION).document(chatId).update("last", message)
    }
    fun popMessage(chatId: String){
        msgListener?.remove()
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                if(chatId != ""){
                    msgListener = firestore.collection(CHAT_COLLECTION).document(chatId).collection(
                        MESSAGES_COLLECTION
                    ).addSnapshotListener{value , error ->
                        if(value != null){
                            messages = value.documents.mapNotNull {
                                it.toObject(Message::class.java)
                            }.sortedBy {
                                it.time
                            }.reversed()

                        }
                        Log.d("TAG1","popMessage:$messages")
                    }
                }
            }
        }

    }

}