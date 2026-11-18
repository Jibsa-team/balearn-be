package com.jipsa.balearn.infra.oauth2

import com.jipsa.balearn.domain.user.*
import com.jipsa.balearn.domain.user.exception.CustomUserException
import com.jipsa.balearn.infra.oauth2.exception.CustomOAuth2Exception
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import java.security.MessageDigest
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomOAuth2UserService(
    private val userAppender: UserAppender,
    private val userReader: UserReader
) : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val registrationId = userRequest.clientRegistration.registrationId
        val attributes = oAuth2User.attributes

        val userInfo = when (registrationId) {
            "google" -> extractGoogleUserInfo(attributes)
            "kakao" -> extractKakaoUserInfo(attributes)
            "github" -> extractGithubUserInfo(attributes)
            else -> throw CustomOAuth2Exception.UnsupportedProviderException
        }

        // 이메일 중복 체크 및 회원 생성/로그인 처리 (추후 구현)
        handleLoginOrRegister(userInfo)

        return CustomOAuth2UserDetail(userInfo)
    }

    private fun extractGoogleUserInfo(attributes: Map<String, Any>): OAuth2Attributes {
        return OAuth2Attributes(
            name = attributes["name"] as String,
            email = attributes["email"] as String,
            provider = AuthProvider.GOOGLE,
            snsId = attributes["sub"] as String,
            profileImageUrl = attributes["picture"] as String,
            attributes = attributes
        )
    }

    private fun extractKakaoUserInfo(attributes: Map<String, Any>): OAuth2Attributes {
        val kakaoAccount = attributes["kakao_account"] as Map<*, *>
        val profile = kakaoAccount["profile"] as Map<*, *>
        return OAuth2Attributes(
            name = profile["nickname"] as String,
            email = kakaoAccount["email"] as String,
            provider = AuthProvider.KAKAO,
            snsId = attributes["id"].toString(),
            profileImageUrl = profile["profile_image_url"] as String,
            attributes = attributes
        )
    }

    private fun extractGithubUserInfo(attributes: Map<String, Any>): OAuth2Attributes {
        return OAuth2Attributes(
            name = attributes["name"] as? String ?: attributes["login"] as String,
            email = attributes["email"] as? String ?: UUID.randomUUID()
                .toString(), // Github은 이메일 제공이 선택사항이므로 없을 경우 랜덤값으로 임시로 대체
            provider = AuthProvider.GITHUB,
            snsId = attributes["id"].toString(),
            profileImageUrl = attributes["avatar_url"] as String,
            attributes = attributes
        )
    }

    private fun handleLoginOrRegister(userInfo: OAuth2Attributes) {
        val email = userInfo.email
        val provider = userInfo.provider

        try {
            val user = userReader.readByEmail(email)
            if (user.userProvider.provider != provider) {
                throw CustomOAuth2Exception.DuplicateEmailException
            }
        } catch (e: CustomUserException.UserNotFoundException) {
            createNewUser(userInfo)
        }
    }

    fun registerOidcUser(user: OidcUser): OidcUser {
        val email = user.email?.takeIf { it.isNotBlank() && user.emailVerified == true }
            ?: throw OAuth2AuthenticationException("verified_email_required")
        val identity = "${user.issuer}\u0000${user.subject}"
        val providerId = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(identity.toByteArray()))
        val attributes = OAuth2Attributes(
            name = user.fullName ?: user.preferredUsername ?: email,
            email = email,
            provider = AuthProvider.OIDC,
            snsId = providerId,
            profileImageUrl = UserProfile.DEFAULT_IMAGE_URL,
            attributes = user.attributes
        )
        try {
            val existing = userReader.readByEmail(email)
            if (existing.userProvider != UserProvider(AuthProvider.OIDC, providerId)) {
                throw OAuth2AuthenticationException("account_already_registered")
            }
        } catch (e: CustomUserException.UserNotFoundException) {
            createNewUser(attributes)
        }
        return user
    }

    private fun createNewUser(attributes: OAuth2Attributes) {
        val newUser = User(
            id = UserId(0),
            _userProfile = UserProfile(
                name = attributes.name,
                email = attributes.email,
                phoneNumber = null,
                profileImageUrl = attributes.profileImageUrl
            ),
            userProvider = UserProvider(
                attributes.provider,
                attributes.snsId
            ),
            createdAt = null,
            modifiedAt = null
        )
        userAppender.append(newUser)
    }

}
