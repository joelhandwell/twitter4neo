package com.github.joelhandwell.twitter4neo

import org.slf4j.LoggerFactory
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertNotNull

object TwNeoSpec : Spek({

    val logger = LoggerFactory.getLogger(TwNeoSpec::class.java)

    describe("TwNeo") {
        it("has Twitter4j client") {
            val twNeo = TwNeo()
            val tw = twNeo.twitter
            val neo4jTimeline = tw.getUserTimeline("neo4j")
            val text = neo4jTimeline.first().text
            logger.debug(text)
            assertNotNull(text)
        }
    }
})