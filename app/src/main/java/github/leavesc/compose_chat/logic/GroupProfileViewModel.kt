package github.leavesc.compose_chat.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesc.compose_chat.base.model.GroupProfile
import github.leavesc.compose_chat.model.GroupProfileScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2021/10/27 18:17
 * @Desc:
 * @Github：https://github.com/leavesC
 */
class GroupProfileViewModel(private val groupId: String) : ViewModel() {

    val groupProfileScreenState = MutableStateFlow(
        GroupProfileScreenState(
            groupProfile = GroupProfile.Empty,
            memberList = emptyList()
        )
    )

    init {
        getGroupProfile()
        getGroupMemberList()
    }

    private fun getGroupProfile() {
        viewModelScope.launch(Dispatchers.Main) {
            ComposeChat.groupProvider.getGroupInfo(groupId = groupId)?.let {
                groupProfileScreenState.emit(value = groupProfileScreenState.value.copy(groupProfile = it))
            }
        }
    }

    private fun getGroupMemberList() {
        viewModelScope.launch(Dispatchers.Main) {
            val memberList = ComposeChat.groupProvider.getGroupMemberList(groupId = groupId)
            groupProfileScreenState.emit(value = groupProfileScreenState.value.copy(memberList = memberList))
        }
    }

}