package com.leinaro.nookandpin.domain

import kotlinx.datetime.Instant

/**
 * A single note pinned to a [Pile].
 *
 * Notes are immutable once created — "editing" a note in Nook & Pin means
 * pinning a new one. That keeps the reveal animation (and the emotional
 * beat of "someone left you something new") meaningful.
 */
data class Note(
    val id: String,
    val pileId: String,
    val authorId: String,
    val text: String,
    val createdAt: Instant,
    val readAt: Instant? = null,
    val likedByUserIds: Set<String> = emptySet()
) {
    val isUnread: Boolean get() = readAt == null
    val likeCount: Int get() = likedByUserIds.size

    fun isLikedBy(userId: String): Boolean = userId in likedByUserIds
}
