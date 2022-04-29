package github.leavesczy.compose_chat.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.cache.AccountCache
import github.leavesczy.compose_chat.model.LoginPageState
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2021/7/9 15:10
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class LoginViewModel : ViewModel() {

    val loginPageState = MutableStateFlow(
        LoginPageState(
            showLogo = false,
            showInput = false,
            showLoading = false,
            loginSuccess = false,
            lastLoginUserId = ""
        )
    )

    fun autoLogin() {
        val lastLoginUserId = AccountCache.lastLoginUserId
        if (lastLoginUserId.isBlank() || !AccountCache.canAutoLogin) {
            dispatchViewState(
                LoginPageState(
                    showLogo = true,
                    showInput = true,
                    showLoading = false,
                    loginSuccess = false,
                    lastLoginUserId = lastLoginUserId
                )
            )
        } else {
            dispatchViewState(
                LoginPageState(
                    showLogo = false,
                    showInput = false,
                    showLoading = true,
                    loginSuccess = false,
                    lastLoginUserId = lastLoginUserId
                )
            )
            login(userId = lastLoginUserId)
        }
    }

    fun goToLogin(userId: String) {
        dispatchViewState(
            LoginPageState(
                showLogo = true,
                showInput = true,
                showLoading = true,
                loginSuccess = false,
                lastLoginUserId = userId
            )
        )
        login(userId = userId)
    }

    private fun login(userId: String) {
        val formatUserId = userId.lowercase()
        viewModelScope.launch {
            when (val loginResult = ComposeChat.accountProvider.login(formatUserId)) {
                is ActionResult.Failed -> {
                    showToast(loginResult.reason)
                    dispatchViewState(
                        LoginPageState(
                            showLogo = true,
                            showInput = true,
                            showLoading = false,
                            loginSuccess = false,
                            lastLoginUserId = formatUserId
                        )
                    )
                }
                is ActionResult.Success -> {
                    AccountCache.onUserLogin(userId = formatUserId)
                    dispatchViewState(
                        LoginPageState(
                            showLogo = false,
                            showInput = false,
                            showLoading = false,
                            loginSuccess = true,
                            lastLoginUserId = formatUserId
                        )
                    )
                }
            }
        }
    }

    private fun dispatchViewState(loginPageState: LoginPageState) {
        this.loginPageState.value = loginPageState
    }

}