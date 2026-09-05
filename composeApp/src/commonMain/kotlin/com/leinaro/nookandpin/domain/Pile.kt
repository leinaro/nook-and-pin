package com.leinaro.nookandpin.domain

/**
 * A shared corkboard between a fixed set of people — a couple, best
 * friends, a family. Anyone in [memberIds] can pin a note; everyone in
 * [memberIds] sees it appear (and gets pushed a notification) in real time.
 */
data class Pile(
    val id: String,
    val name: String,
    val memberIds: Set<String>,
    val createdBy: String
)
