package com.smofs.wsfs.postgres

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class WSFSDataSourceTest {
    @Test
    fun setupTest() {
        assertDoesNotThrow {
            WSFSDataSource().getDataSource()
            WSFSDataSource().getTestDataSource()
        }
    }
}
