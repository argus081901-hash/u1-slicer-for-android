package com.u1.slicer.printer

import com.u1.slicer.data.ExtruderPreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BambuExtruderPresetUpsertTest {

    @Test
    fun sparseExternalSpoolPresetIsInsertedAndProfileIsPreserved() {
        val external = ExtruderPreset(
            index = 254,
            color = "#000000",
            materialType = "PETG",
            filamentProfileId = 77,
            displayLabel = "External spool",
        )

        val result = upsertExtruderPreset(emptyList(), external)

        assertEquals(1, result.size)
        assertEquals(254, result.single().index)
        assertEquals("PETG", result.single().materialType)
        assertEquals(77L, result.single().filamentProfileId)
    }

    @Test
    fun existingSparsePresetIsReplacedWithoutDuplicates() {
        val old = ExtruderPreset(
            index = 254,
            materialType = "PETG",
            filamentProfileId = null,
        )
        val updated = old.copy(filamentProfileId = 77)

        val result = upsertExtruderPreset(listOf(old), updated)

        assertEquals(1, result.size)
        assertEquals(77L, result.single().filamentProfileId)
    }

    @Test
    fun unrelatedPresetIsLeftUntouched() {
        val slot0 = ExtruderPreset(index = 0, materialType = "PLA")
        val external = ExtruderPreset(index = 254, materialType = "PETG", filamentProfileId = 77)

        val result = upsertExtruderPreset(listOf(slot0), external)

        assertEquals(2, result.size)
        assertEquals("PLA", result.first { it.index == 0 }.materialType)
        assertNull(result.first { it.index == 0 }.filamentProfileId)
    }
}
