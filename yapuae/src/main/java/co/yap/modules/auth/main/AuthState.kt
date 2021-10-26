package co.yap.modules.auth.main

import co.yap.yapcore.BaseState

class AuthState : BaseState(), IAuth.State {
    override var isAccountBlocked: Boolean = false
}