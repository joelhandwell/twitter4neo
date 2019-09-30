package com.github.joelhandwell.twitter4neo

import com.github.joelhandwell.twitter4neo.model.packageName
import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.session.SessionFactory
import twitter4j.TwitterFactory
import java.nio.file.Paths
import java.util.*
import ch.qos.logback.classic.Level
import com.github.joelhandwell.twitter4neo.model.StatusNode
import com.github.joelhandwell.twitter4neo.model.UserNode
import com.github.joelhandwell.twitter4neo.model.localDateTimeOf
import org.neo4j.ogm.cypher.ComparisonOperator
import org.neo4j.ogm.cypher.Filter
import org.slf4j.LoggerFactory

val userDir = System.getProperty("user.dir")
val userHome = System.getProperty("user.home")

object LocalProperty {

    private val p = Properties()

    init {
        val input = Paths.get("$userDir\\local.properties")
            .toFile()
            .inputStream()

        p.load(input)
    }

    val neo4juri = p.getProperty("neo4j.uri")
    val rootScreenName = p.getProperty("root.screen.name")
    val rootUserId = p.getProperty("root.user.id").toLong()
}

fun prodDb(): SessionFactory {
    (LoggerFactory.getLogger("org.neo4j.ogm") as ch.qos.logback.classic.Logger).level = Level.ERROR
    return SessionFactory(Configuration.Builder().uri(LocalProperty.neo4juri).build(), packageName())
}

class TwNeo(private val db: SessionFactory = prodDb()) {

    private val logger = LoggerFactory.getLogger(TwNeo::class.java)
    private val twitter = TwitterFactory.getSingleton()!!

    fun saveUser(userId: Long): UserNode {
        logger.debug("saveUser called for id $userId")
        val user = twitter.showUser(userId)
        val userNode = UserNode(
            userId = user.id,
            name = user.name,
            screenName = user.screenName,
            description = user.description,
            isProtected = user.isProtected
        )
        db.openSession().save(userNode)
        return userNode
    }

    fun saveUserTimeline(userId: Long) {

        val fromDb = db.openSession().loadAll(UserNode::class.java, Filter("userId", ComparisonOperator.EQUALS, userId))
        val user = when(fromDb.size){
            0 -> saveUser(userId)
            1 -> fromDb.first()
            else -> throw IllegalStateException("user count should be either 0 or 1")
        }

        logger.debug(user.toString())

        val fromTwitter = twitter.getUserTimeline(userId)

        fromTwitter.forEach {
            val statusNode = StatusNode(
                createdAt = localDateTimeOf(it.createdAt),
                statusId = it.id,
                text = it.text
            )
            user.addStatus(statusNode)
        }
        db.openSession().save(user)
    }
}

fun main() {
    val twNeo = TwNeo()
    val userId = LocalProperty.rootUserId
    twNeo.saveUserTimeline(userId)
}
