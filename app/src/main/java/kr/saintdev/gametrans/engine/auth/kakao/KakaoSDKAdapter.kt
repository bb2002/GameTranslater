package kr.saintdev.gametrans.engine.auth.kakao

import android.content.Context
import com.kakao.auth.*
import kr.saintdev.gametrans.engine.auth.GlobalApplication

class KakaoSDKAdapter : KakaoAdapter() {
    override fun getSessionConfig(): ISessionConfig {
        return object : ISessionConfig {
            override fun isSaveFormData() = true

            override fun getAuthTypes() = arrayOf(AuthType.KAKAO_TALK, AuthType.KAKAO_STORY, AuthType.KAKAO_ACCOUNT)

            override fun isSecureMode() = false

            override fun getApprovalType() = ApprovalType.INDIVIDUAL

            override fun isUsingWebviewTimer() = false
        }
    }

    override fun getPushConfig() = super.getPushConfig()

    override fun getApplicationConfig() = IApplicationConfig { GlobalApplication.getGlobalApplicationCtx() }


}