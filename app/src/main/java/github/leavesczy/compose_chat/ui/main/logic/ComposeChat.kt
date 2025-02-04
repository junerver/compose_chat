package github.leavesczy.compose_chat.ui.main.logic

import github.leavesczy.compose_chat.base.provider.IAccountProvider
import github.leavesczy.compose_chat.base.provider.IConversationProvider
import github.leavesczy.compose_chat.base.provider.IFriendshipProvider
import github.leavesczy.compose_chat.base.provider.IGroupProvider
import github.leavesczy.compose_chat.base.provider.IMessageProvider
import github.leavesczy.compose_chat.proxy.logic.AccountProvider
import github.leavesczy.compose_chat.proxy.logic.ConversationProvider
import github.leavesczy.compose_chat.proxy.logic.FriendshipProvider
import github.leavesczy.compose_chat.proxy.logic.GroupProvider
import github.leavesczy.compose_chat.proxy.logic.MessageProvider

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object ComposeChat {

    const val groupId01 = "@TGS#3SSMB3WHI"

    const val groupId02 = "@TGS#3VOZA3WHT"

    const val groupId03 = "@TGS#3W42A3WHP"

    const val groupIdToUploadAvatar = "@TGS#aZRGY4WHQ"

    val accountProvider: IAccountProvider = AccountProvider()

    val conversationProvider: IConversationProvider = ConversationProvider()

    val messageProvider: IMessageProvider = MessageProvider()

    val friendshipProvider: IFriendshipProvider = FriendshipProvider()

    val groupProvider: IGroupProvider = GroupProvider()

}