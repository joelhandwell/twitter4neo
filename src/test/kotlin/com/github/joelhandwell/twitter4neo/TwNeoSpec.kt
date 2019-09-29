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
            val id = tw.id
            logger.debug(id.toString())
            assertNotNull(id)
        }
    }
})