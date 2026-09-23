package com.u1.slicer.bambu

import com.u1.slicer.slice.SlicerTarget
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BambuA1FastStartupTest {

    @Test
    fun `a1 fast startup removes optional vibration and trims purge while keeping safety passes`() {
        val start = BambuSingleNozzleMachineGcode.forTarget(SlicerTarget.BambuA1).start

        assertFalse(start.contains(";=====start printer sound"))
        assertFalse(start.contains(";===== mech mode fast check start"))
        assertFalse(start.contains(";===== mech mode fast check end"))

        assertFalse(start.contains("G1 E50 F200"))
        assertTrue(start.contains("G1 E25 F200"))
        assertTrue(start.contains("G1 E3 F200"))
        // Keep the important reliability/safety steps.
        assertTrue(start.contains(";===== wipe nozzle"))
        assertTrue(start.contains(";===== bed leveling"))
        assertTrue(start.contains("G28"))
        assertTrue(start.contains("build_plate_detect_flag"))
    }
}
