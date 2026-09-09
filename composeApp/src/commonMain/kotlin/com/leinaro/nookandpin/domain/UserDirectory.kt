package com.leinaro.nookandpin.domain

/**
 * Looks up a person's Firebase uid by their email, so you can invite them
 * to a pile without knowing their uid — you never should, it's not
 * something anyone types in. Someone only shows up here after they've
 * signed into Nook & Pin at least once (whichever [AuthRepository]
 * implementation is wired up is expected to publish a lookup-able profile
 * on successful sign-in).
 */
interface UserDirectory {
    suspend fun findUidByEmail(email: String): String?
}
