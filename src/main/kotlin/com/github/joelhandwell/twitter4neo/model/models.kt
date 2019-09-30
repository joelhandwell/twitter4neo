package com.github.joelhandwell.twitter4neo.model

import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*


class Model

fun packageName(): String {
    return Model::class.java.`package`.name
}

fun localDateTimeOf(date: Date) = LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC)

@NodeEntity
data class StatusNode(
    @Id @GeneratedValue var id: Long? = null,
    val createdAt: LocalDateTime,
    @Id val statusId: Long,
    val text: String
)

@NodeEntity
data class UserNode(
    @Id @GeneratedValue var id: Long? = null,
    @Id val userId: Long,
    val name: String,
    val screenName: String,
    val description: String,
    val isProtected: Boolean,

    @Relationship var statuses: MutableList<StatusNode>? = mutableListOf()
) {
    fun addStatus(statusNode: StatusNode) {
        if (statuses == null) {
            statuses = mutableListOf()
        }
        statuses!!.add(statusNode)
    }
}