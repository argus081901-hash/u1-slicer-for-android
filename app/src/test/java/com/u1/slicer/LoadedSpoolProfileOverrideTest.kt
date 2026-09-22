package com.u1.slicer

import com.u1.slicer.data.CanonicalFilamentList
import com.u1.slicer.data.ExtruderPreset
import com.u1.slicer.data.FilamentEntry
import com.u1.slicer.data.FilamentProfile
import com.u1.slicer.data.FilamentSource
import com.u1.slicer.data.resolvePerFilamentTypeAndTemp
import org.junit.Assert.assertEquals
import org.junit.Test

class LoadedSpoolProfileOverrideTest {

    private val genericPetg = FilamentProfile(
        id = 77,
        name = "Generic PETG",
        material = "PETG",
        nozzleTemp = 250,
        bedTemp = 70,
        retractLength = 0.8f,
        retractSpeed = 45f,
    )

    @Test
    fun explicitProfileBeatsPetgMaterialFallbackForCanonicalFile() {
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

        val (types, temps) = resolvePerFilamentTypeAndTemp(
            canonical = canonical,
            overrides = mapOf(0 to (null to "PETG")),
            colorMapping = listOf(0),
            presets = listOf(ExtruderPreset(index = 0, materialType = "PLA")),
            filamentLibrary = listOf(genericPetg),
            profileOverrides = mapOf(0 to genericPetg.id),
        )

        assertEquals(listOf("PETG"), types)
        assertEquals(listOf(250), temps)
    }

    @Test
    fun nonCanonicalProfileUsesSavedProfileTempInsteadOf235Fallback() {
        val (types, temps) = applyNonCanonicalOverride(
            slotTypes = listOf("PLA"),
            slotTemps = listOf(220),
            override = SlicerViewModel.FilamentOverride(
                materialType = "PETG",
                filamentProfileId = genericPetg.id,
            ),
            explicitProfile = genericPetg,
        )

        assertEquals(listOf("PETG"), types)
        assertEquals(listOf(250), temps)
    }

    @Test
    fun materialOnlyOverrideStillUsesLegacyFallback() {
        val (types, temps) = applyNonCanonicalOverride(
            slotTypes = listOf("PLA"),
            slotTemps = listOf(220),
            override = SlicerViewModel.FilamentOverride(materialType = "PETG"),
        )

        assertEquals(listOf("PETG"), types)
        assertEquals(listOf(235), temps)
    }
}
