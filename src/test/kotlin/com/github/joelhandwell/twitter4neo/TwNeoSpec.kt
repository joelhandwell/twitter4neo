package com.github.joelhandwell.twitter4neo

import com.github.joelhandwell.twitter4neo.model.StatusNode
import com.github.joelhandwell.twitter4neo.model.packageName
import org.slf4j.LoggerFactory
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.session.SessionFactory
import java.nio.file.Paths
import kotlin.test.assertEquals

fun testDb(dbName: String?): SessionFactory {
    val file = Paths.get("$userHome\\.Neo4jEmbedded\\$dbName").toFile().toURI().toURL().toExternalForm()
    return SessionFactory(Configuration.Builder().uri(file).build(), packageName())
}

object TwNeoSpec : Spek({

    val logger = LoggerFactory.getLogger(TwNeoSpec::class.java)
    val db = testDb(TwNeoSpec::class.java.simpleName)
    val twNeo = TwNeo(db)

    describe("TwNeo") {

        beforeEachTest { db.openSession().purgeDatabase() }

        it("saves a user's timeline into db", timeout = 30000) {
            twNeo.saveUserTimeline(LocalProperty.rootUserId)
            val statusCount = db.openSession().countEntitiesOfType(StatusNode::class.java)
            logger.debug(statusCount.toString())
            assertEquals(20, statusCount)
        }
    }

    afterGroup { db.close() }
})
