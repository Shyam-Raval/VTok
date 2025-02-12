package com.example.vtok.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import com.example.vtok.AppState
import com.example.vtok.ChatData
import com.example.vtok.ChatUserData
import com.example.vtok.ChatViewModel
import com.example.vtok.R
import com.example.vtok.dialogs.CustomDialogBox
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.logging.SimpleFormatter


@Composable
fun ChatsScreenUI(
    viewModel: ChatViewModel = ChatViewModel(),
    state: AppState = AppState(),
    showSingleChat: (ChatUserData, String) -> Unit ={_ , _ ,->}
) {
    val padding by animateDpAsState(targetValue = 10.dp)
    val chats = viewModel.chats
    val filterChats = chats
    val selectedItem = remember { mutableStateListOf<String>() }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showDialog() },
                shape = RoundedCornerShape(50.dp),
                containerColor = colorScheme.inversePrimary
            ) {
                Icon(
                    imageVector = Icons.Filled.AddComment,
                    contentDescription = "",
                    tint = Color.White
                )
            }
        }
    ) {
        it
        Image(
            painter = painterResource(id = R.drawable.blck_blurry),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop


        )

        AnimatedVisibility(visible = state.showDialog) {
            CustomDialogBox(
                state = state,
                hideDialog = { viewModel.hideDialog() },
                addChat = {
                    viewModel.addChat(state.srEmail)
                    viewModel.hideDialog()
                    viewModel.setSrEmail("")

                },
                setEmail = {
                    viewModel.setSrEmail(it)
                }
            )
        }
        Column(
            modifier = Modifier.padding(top = 36.dp)
        ) {
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 10.dp , top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ){
                item{
                //    Box(modifier = Modifier.padding(bottom = 20.dp , start = 5.dp , end = 5.dp ).size(70.dp).drawWithCache {  })
                }
            }
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(0.98f)
                ) {
                    Column {

                        Text(
                            text = "VTok",
                            modifier = Modifier.padding(start = 16.dp),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )


                        }
                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = {}, modifier = Modifier
                        .background(
                            colorScheme.background.copy(alpha = 0.2f),
                            CircleShape
                        )
                        .border(
                            0.05.dp,
                            color = Color(0xFF35567A),
                            CircleShape
                        )) {
                        Icon(
                            painter = painterResource(id = R.drawable._666693_search_icon),
                            contentDescription = "",
                            modifier = Modifier.scale(0.7f)
                        )
                        }
                    Spacer(modifier = Modifier.weight(0.1f))

                        Column {
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .background(
                                        colorScheme.background.copy(alpha = .2f),
                                        CircleShape
                                    ).border(0.05.dp , Color(0xFF35567A) , CircleShape )

                            ) {
                                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "")

                            }
                        }


                }
            }
            LazyColumn(
                modifier = Modifier
                    .padding(top = padding)
                    .fillMaxSize()
                    .background(
                        colorScheme.background.copy(
                            alpha = .2f,
                        ),
                        shape = RoundedCornerShape(30.dp, 30.dp)

                    )
                    .border(
                        0.05.dp,
                        color = Color(0xFF35567A),
                        shape = RoundedCornerShape(30.dp, 30.dp)
                    )
            ) {
                item {
                    Text(
                        text = "Chats",
                        modifier = Modifier.padding(16.dp, 16.dp, 16.dp),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Normal
                    )

                }
                items(filterChats) {
                    val chatUser = if (it.user1?.userId != state.userData?.userId) {
                        it.user1
                    } else {
                        it.user2
                    }
                    ChatItem(
                        state = state,
                        chatUser!!,
                        chat = it,
                        isSelected = selectedItem.contains(it.chatId),
                        showSingleChat = { user, id -> showSingleChat(user, id) }


                    )
                }


            }
        }


    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatItem(
    state: AppState,
    userData: ChatUserData,
    chat: ChatData,
    isSelected: Boolean,
    showSingleChat: (ChatUserData, String) -> Unit
) {
    val formatter = remember {
        SimpleDateFormat(("hh:mm a"), Locale.getDefault())
    }
    val color = if (!isSelected) Color.Transparent else colorScheme.onPrimary
    Row(
        modifier = Modifier
            .background(
                color = color
            )
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable {
                showSingleChat(
                    userData, chat.chatId
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(userData.ppurl)
                .crossfade(true)
                .allowHardware(false)
                .build(),
            placeholder = painterResource(id = R.drawable.person_placeholder_4),
            error = painterResource(id = R.drawable.person_placeholder_4),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(.95f)
            ) {
                Text(
                    text = if (userData.userId == state.userData?.userId)
                        userData.username.orEmpty() + "(You)" else userData.username.orEmpty(),
                    modifier = Modifier.width(150.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (chat.last?.time != null) formatter.format(chat.last.time.toDate()) else "",
                    color = Color.Gray,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Light
                    )
                )

            }
            AnimatedVisibility(chat.last?.time != null && userData.typing) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (chat.last?.senderId == state.userData?.userId) {
                        Icon(
                            painter = painterResource(id = R.drawable.check_mark),
                            contentDescription = "",
                            modifier = Modifier
                                .padding(end = 5.dp)
                                .size(10.dp),
                            tint = if (chat.last?.read ?: false) Color(0xFF13C70D) else Color.White
                        )
                    }
                }

            }
        }

    }

}
