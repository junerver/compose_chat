package github.leavesczy.compose_chat.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.atLeast
import github.leavesczy.compose_chat.base.model.C2CConversation
import github.leavesczy.compose_chat.base.model.Chat
import github.leavesczy.compose_chat.base.model.Conversation
import github.leavesczy.compose_chat.base.model.GroupConversation
import github.leavesczy.compose_chat.extend.scrim
import github.leavesczy.compose_chat.ui.chat.ChatActivity
import github.leavesczy.compose_chat.ui.main.logic.ConversationViewModel
import github.leavesczy.compose_chat.ui.theme.WindowInsetsEmpty
import github.leavesczy.compose_chat.ui.widgets.CoilImage

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun ConversationPage(conversationViewModel: ConversationViewModel) {
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsetsEmpty
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues)
        ) {
            val conversationPageViewState = conversationViewModel.conversationPageViewState
            val conversationList = conversationPageViewState.conversationList
            if (conversationList.isEmpty()) {
                Text(
                    text = "Empty",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(
                            fraction = 0.4f
                        )
                        .wrapContentSize(align = Alignment.BottomCenter),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 70.sp
                )
            } else {
                val onClickConversation: (Conversation) -> Unit = remember {
                    { conversation ->
                        when (conversation) {
                            is C2CConversation -> {
                                ChatActivity.navTo(
                                    context = context,
                                    chat = Chat.PrivateChat(id = conversation.id)
                                )
                            }

                            is GroupConversation -> {
                                ChatActivity.navTo(
                                    context = context,
                                    chat = Chat.GroupChat(id = conversation.id)
                                )
                            }
                        }
                    }
                }
                val onDeleteConversation: (Conversation) -> Unit = remember {
                    { conversation ->
                        conversationViewModel.deleteConversation(conversation = conversation)
                    }
                }
                val onPinnedConversation: (Conversation, Boolean) -> Unit = remember {
                    { conversation, pin ->
                        conversationViewModel.pinConversation(
                            conversation = conversation, pin = pin
                        )
                    }
                }
                LazyColumn(
                    state = conversationPageViewState.listState,
                    contentPadding = PaddingValues(bottom = 60.dp),
                ) {
                    items(
                        items = conversationList,
                        key = {
                            it.id
                        },
                        contentType = {
                            "Conversation"
                        }
                    ) {
                        ConversationItem(
                            conversation = it,
                            onClickConversation = onClickConversation,
                            onDeleteConversation = onDeleteConversation,
                            onPinnedConversation = onPinnedConversation
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LazyItemScope.ConversationItem(
    conversation: Conversation,
    onClickConversation: (Conversation) -> Unit,
    onDeleteConversation: (Conversation) -> Unit,
    onPinnedConversation: (Conversation, Boolean) -> Unit
) {
    var menuExpanded by remember {
        mutableStateOf(value = false)
    }
    ConstraintLayout(
        modifier = Modifier
            .animateItemPlacement()
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .then(
                other = if (conversation.isPinned) {
                    Modifier.scrim(color = Color(0x26CCCCCC))
                } else {
                    Modifier
                }
            )
            .fillMaxWidth()
            .combinedClickable(onClick = {
                onClickConversation(conversation)
            }, onLongClick = {
                menuExpanded = true
            }),
    ) {
        val (avatarRef, unreadMessageCountRef, nicknameRef, lastMsgRef, timeRef, dividerRef, dropdownMenuRef) = createRefs()
        val verticalChain =
            createVerticalChain(nicknameRef, lastMsgRef, chainStyle = ChainStyle.Packed)
        constrain(ref = verticalChain) {
            top.linkTo(anchor = parent.top)
            bottom.linkTo(anchor = parent.bottom)
        }
        CoilImage(
            modifier = Modifier
                .constrainAs(ref = avatarRef) {
                    start.linkTo(anchor = parent.start)
                    linkTo(
                        top = parent.top, bottom = parent.bottom
                    )
                }
                .padding(
                    start = 14.dp, top = 8.dp, bottom = 8.dp
                )
                .size(size = 52.dp)
                .clip(shape = RoundedCornerShape(size = 6.dp)),
            data = conversation.faceUrl
        )
        if (conversation.unreadMessageCount > 0) {
            val count = if (conversation.unreadMessageCount > 99) {
                "99+"
            } else {
                conversation.unreadMessageCount.toString()
            }
            Text(
                modifier = Modifier
                    .constrainAs(ref = unreadMessageCountRef) {
                        start.linkTo(anchor = avatarRef.end)
                        top.linkTo(
                            anchor = avatarRef.top, margin = 2.dp
                        )
                        end.linkTo(anchor = avatarRef.end)
                        width = Dimension.preferredWrapContent.atLeast(dp = 20.dp)
                        height = Dimension.preferredWrapContent.atLeast(dp = 20.dp)
                    }
                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                    .wrapContentSize(align = Alignment.Center),
                text = count,
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
        Text(
            modifier = Modifier
                .constrainAs(ref = nicknameRef) {
                    linkTo(
                        start = avatarRef.end,
                        end = timeRef.start,
                        startMargin = 12.dp,
                        endMargin = 12.dp
                    )
                    width = Dimension.fillToConstraints
                }
                .padding(bottom = 1.dp),
            text = conversation.name,
            fontSize = 17.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(
            modifier = Modifier
                .constrainAs(ref = lastMsgRef) {
                    linkTo(start = nicknameRef.start, end = parent.end, endMargin = 12.dp)
                    width = Dimension.fillToConstraints
                }
                .padding(top = 1.dp),
            text = conversation.formatMsg,
            fontSize = 14.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(
            modifier = Modifier.constrainAs(ref = timeRef) {
                centerVerticallyTo(other = nicknameRef)
                end.linkTo(anchor = parent.end, margin = 12.dp)
                height = Dimension.wrapContent
            },
            text = conversation.lastMessage.messageDetail.conversationTime,
            fontSize = 12.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Divider(
            modifier = Modifier.constrainAs(ref = dividerRef) {
                linkTo(start = avatarRef.end, end = parent.end)
                bottom.linkTo(anchor = parent.bottom)
                width = Dimension.fillToConstraints
            },
            thickness = 0.2.dp
        )
        MoreActionDropdownMenu(
            modifier = Modifier.constrainAs(ref = dropdownMenuRef) {
                linkTo(
                    start = parent.start, end = parent.end, bias = 0.3f
                )
                linkTo(
                    top = parent.top, bottom = parent.bottom
                )
            },
            expanded = menuExpanded,
            onDismissRequest = {
                menuExpanded = false
            },
            conversation = conversation,
            onDeleteConversation = onDeleteConversation,
            onPinnedConversation = onPinnedConversation
        )
    }
}

@Composable
private fun MoreActionDropdownMenu(
    modifier: Modifier,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    conversation: Conversation,
    onDeleteConversation: (Conversation) -> Unit,
    onPinnedConversation: (Conversation, Boolean) -> Unit
) {
    Box(
        modifier = modifier
    ) {
        DropdownMenu(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
            expanded = expanded,
            onDismissRequest = onDismissRequest
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = if (conversation.isPinned) {
                            "取消置顶"
                        } else {
                            "置顶会话"
                        },
                        fontSize = 18.sp
                    )
                },
                onClick = {
                    onDismissRequest()
                    onPinnedConversation(
                        conversation, !conversation.isPinned
                    )
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = "删除会话", fontSize = 18.sp
                    )
                },
                onClick = {
                    onDismissRequest()
                    onDeleteConversation(conversation)
                }
            )
        }
    }
}