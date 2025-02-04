package github.leavesczy.compose_chat.ui.chat

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.material3.MaterialTheme
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.base.model.Chat
import github.leavesczy.compose_chat.base.model.ImageMessage
import github.leavesczy.compose_chat.base.model.TextMessage
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.chat.logic.ChatPageAction
import github.leavesczy.compose_chat.ui.chat.logic.ChatViewModel
import github.leavesczy.compose_chat.ui.friendship.FriendProfileActivity
import github.leavesczy.compose_chat.ui.preview.PreviewImageActivity
import github.leavesczy.compose_chat.ui.widgets.SystemBarTheme
import github.leavesczy.compose_chat.utils.showToast

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class ChatActivity : BaseActivity() {

    companion object {

        private const val keyChat = "keyChat"

        fun navTo(context: Context, chat: Chat) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(keyChat, chat)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

    @Suppress("DEPRECATION")
    private val chat: Chat by lazy {
        intent.getParcelableExtra(keyChat)!!
    }

    private val chatViewModel by viewModelsInstance {
        ChatViewModel(chat = chat)
    }

    private val chatPageAction = ChatPageAction(
        onClickAvatar = {
            val messageSenderId = it.messageDetail.sender.id
            if (messageSenderId.isNotBlank()) {
                FriendProfileActivity.navTo(context = this, friendId = messageSenderId)
            }
        },
        onClickMessage = {
            when (it) {
                is ImageMessage -> {
                    val imagePath = it.previewUrl
                    if (imagePath.isBlank()) {
                        showToast(msg = "图片路径为空")
                    } else {
                        PreviewImageActivity.navTo(
                            context = this, imagePath = imagePath
                        )
                    }
                }

                else -> {

                }
            }
        },
        onLongClickMessage = {
            when (it) {
                is TextMessage -> {
                    val msg = it.formatMessage
                    if (msg.isNotEmpty()) {
                        copyText(
                            context = this, text = msg
                        )
                        showToast(msg = "已复制")
                    }
                }

                else -> {

                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent(systemBarTheme = {
            SystemBarTheme(navigationBarColor = MaterialTheme.colorScheme.onSecondaryContainer)
        }) {
            ChatPage(
                chatViewModel = chatViewModel, chatPageAction = chatPageAction
            )
        }
    }

    private fun copyText(
        context: Context, text: String
    ) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        if (clipboardManager != null) {
            val clipData = ClipData.newPlainText(context.getString(R.string.app_name), text)
            clipboardManager.setPrimaryClip(clipData)
        }
    }

}