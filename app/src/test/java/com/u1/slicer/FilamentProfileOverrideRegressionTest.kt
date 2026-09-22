package com.u1.slicer

import com.u1.slicer.data.CanonicalFilamentList
import com.u1.slicer.data.ExtruderPreset
import com.u1.slicer.data.FilamentEntry
import com.u1.slicer.data.FilamentProfile
import com.u1.slicer.data.FilamentSource
import org.junit.Assert.assertEquals
import org.junit.Test

class FilamentProfileOverrideRegressionTest {

    private val petg250 = FilamentProfile(
        id = 77L,
        name = "Generic PETG tuned",
        material = "PETG",
        nozzleTemp = 250,
        bedTemp = 70,
        retractLength = 0.8f,
        retractSpeed = 45f,
    )

    @Test
    fun `non canonical loaded spool profile uses profile nozzle temperature`() {
        val override = SlicerViewModel.FilamentOverride(
            color = "#000000",
            materialType = "PETG",
            filamentProfileId = petg250.id,
        )

        val (types, temps) = applyNonCanonicalOverride(
            slotTypes = listOf("PLA"),
            slotTemps = listOf(220),
            override = override,
            filamentLibrary = listOf(petg250),
        )

        assertEquals(listOf("PETG"), types)
        assertEquals(listOf(250), temps)
    }

    @Test
    fun `canonical header patch keeps explicit profile temperature`() {
        val canonical = CanonicalFilamentList(
            filaments = listOf(
                FilamentEntry(
                    fileIndex = 0,
                    color = "#000000",
                    materialType = "PLA",
                    source = FilamentSource.FILE_COLOUR,
                )
            )
        )
        val presets = listOf(ExtruderPreset(index = 0, materialType = "PLA"))

        val temps = resolveNozzleTempsForHeaderPatch(
            canonical = canonical,
            overrides = mapOf(0 to ("#000000" to "PETG")),
            colorMapping = listOf(0),
            presets = presets,
            filamentLibrary = listOf(petg250),
            profileOverrides = mapOf(0 to petg250.id),
        )

        assertEquals(listOf(250), temps)
    }

    @Test
    fun `material only override still uses material default without profile`() {
        val override = SlicerViewModel.FilamentOverride(
            materialType = "PETG",
            filamentProfileId = null,
        )

        val (_, temps) = applyNonCanonicalOverride(
            slotTypes = listOf("PLA"),
            slotTemps = listOf(215),
            override = override,
            filamentLibrary = listOf(petg250),
        )

        assertEquals(listOf(235), temps)
    }
}
