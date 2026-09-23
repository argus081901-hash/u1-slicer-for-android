package com.u1.slicer.printer

import com.u1.slicer.data.BambuModel
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class BambuA1FastStartPayloadTest {

    @Test
    fun `modern a1 payload skips optional vibration and extrusion calibration`() {
        val print = JSONObject(
            DefaultBambuLanClient.projectFileCommandPayload(
                sequenceId = 17,
                submissionId = "1700",
                remoteName = "a1-fast.gcode.3mf",
                plateId = 1,
                amsMapping = listOf(-1),
                useAms = false,
                subtaskName = "a1-fast",
                model = BambuModel.A1,
                firmwareVersion = "01.05.00.00",
            ),
        ).getJSONObject("print")

        assertFalse(print.getBoolean("vibration_cali"))
        assertFalse(print.getBoolean("flow_cali"))
        assertEquals(0, print.getInt("extrude_cali_flag"))
        assertFalse(print.getBoolean("bed_leveling"))
        assertEquals(2, print.getInt("auto_bed_leveling"))
    }
}
