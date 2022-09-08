package fr.gouv.cnsp.monitorfish.infrastructure.api.bff

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import fr.gouv.cnsp.monitorfish.config.MapperConfiguration
import fr.gouv.cnsp.monitorfish.domain.entities.reporting.*
import fr.gouv.cnsp.monitorfish.domain.entities.vessel.VesselIdentifier
import fr.gouv.cnsp.monitorfish.domain.use_cases.reporting.*
import fr.gouv.cnsp.monitorfish.infrastructure.api.input.CreateReportingDataInput
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.ZonedDateTime

@Import(MapperConfiguration::class)
@ExtendWith(SpringExtension::class)
@WebMvcTest(value = [(ReportingController::class)])
class ReportingControllerITests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var archiveReporting: ArchiveReporting

    @MockBean
    private lateinit var deleteReporting: DeleteReporting

    @MockBean
    private lateinit var archiveReportings: ArchiveReportings

    @MockBean
    private lateinit var deleteReportings: DeleteReportings

    @MockBean
    private lateinit var addReporting: AddReporting

    @MockBean
    private lateinit var getAllCurrentReportings: GetAllCurrentReportings

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `Should archive a reporting`() {
        // When
        mockMvc.perform(put("/bff/v1/reportings/123/archive"))
            // Then
            .andExpect(status().isOk)

        Mockito.verify(archiveReporting).execute(123)
    }

    @Test
    fun `Should archive multiple reportings`() {
        // When
        mockMvc.perform(put("/bff/v1/reportings/archive")
            .content(objectMapper.writeValueAsString(listOf(1, 2, 3)))
            .contentType(MediaType.APPLICATION_JSON))
            // Then
            .andExpect(status().isOk)

        Mockito.verify(archiveReportings).execute(listOf(1, 2, 3))
    }

    @Test
    fun `Should delete a reporting`() {
        // When
        mockMvc.perform(put("/bff/v1/reportings/123/delete"))
            // Then
            .andExpect(status().isOk)

        Mockito.verify(deleteReporting).execute(123)
    }

    @Test
    fun `Should delete multiple reportings`() {
        // When
        mockMvc.perform(put("/bff/v1/reportings/delete")
            .content(objectMapper.writeValueAsString(listOf(1, 2, 3)))
            .contentType(MediaType.APPLICATION_JSON))
            // Then
            .andExpect(status().isOk)

        Mockito.verify(deleteReportings).execute(listOf(1, 2, 3))
    }

    @Test
    fun `Should create a reporting`() {
        // Given
        given(addReporting.execute(any())).willReturn(Reporting(
            internalReferenceNumber = "FRFGRGR",
            externalReferenceNumber = "RGD",
            ircs = "6554fEE",
            vesselIdentifier = VesselIdentifier.INTERNAL_REFERENCE_NUMBER,
            creationDate = ZonedDateTime.now(),
            value = InfractionSuspicion(ReportingActor.OPS, natinfCode = "123456", title = "A title"),
            type = ReportingType.INFRACTION_SUSPICION,
            isDeleted = false,
            isArchived = false))

        // When
        mockMvc.perform(post("/bff/v1/reportings")
            .content(objectMapper.writeValueAsString(CreateReportingDataInput(
                internalReferenceNumber = "FRFGRGR",
                externalReferenceNumber = "RGD",
                ircs = "6554fEE",
                vesselIdentifier = VesselIdentifier.INTERNAL_REFERENCE_NUMBER,
                creationDate = ZonedDateTime.now(),
                value = InfractionSuspicion(ReportingActor.OPS, natinfCode = "123456", title = "A title"),
                type = ReportingType.INFRACTION_SUSPICION
            )))
            .contentType(MediaType.APPLICATION_JSON))
            // Then
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.internalReferenceNumber", equalTo("FRFGRGR")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.value.reportingActor", equalTo("OPS")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.value.natinfCode", equalTo("123456")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.value.title", equalTo("A title")))
    }

  @Test
  fun `Should get all current reportings`() {
    // Given
    given(getAllCurrentReportings.execute()).willReturn(listOf(
      Reporting(
        internalReferenceNumber = "FRFGRGR",
        externalReferenceNumber = "RGD",
        ircs = "6554fEE",
        vesselIdentifier = VesselIdentifier.INTERNAL_REFERENCE_NUMBER,
        creationDate = ZonedDateTime.now(),
        value = InfractionSuspicion(ReportingActor.OPS, natinfCode = "123456", title = "A title"),
        type = ReportingType.INFRACTION_SUSPICION,
        isDeleted = false,
        isArchived = false,
        underCharter = true)
    ))

    // When
    mockMvc.perform(get("/bff/v1/reportings"))
      // Then
      .andExpect(status().isOk)
      .andExpect(MockMvcResultMatchers.jsonPath("$.length()", equalTo(1)))
      .andExpect(MockMvcResultMatchers.jsonPath("$[0].internalReferenceNumber", equalTo("FRFGRGR")))
      .andExpect(MockMvcResultMatchers.jsonPath("$[0].isArchived", equalTo(false)))
      .andExpect(MockMvcResultMatchers.jsonPath("$[0].isDeleted", equalTo(false)))
      .andExpect(MockMvcResultMatchers.jsonPath("$[0].underCharter", equalTo(true)))
  }

}
