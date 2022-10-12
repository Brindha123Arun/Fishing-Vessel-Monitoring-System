package fr.gouv.cnsp.monitorfish.infrastructure.api

import fr.gouv.cnsp.monitorfish.domain.entities.health.Health
import fr.gouv.cnsp.monitorfish.domain.use_cases.healthcheck.GetHealthcheck
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.ZonedDateTime

@ExtendWith(SpringExtension::class)
@WebMvcTest(value = [(HealthcheckController::class)])
class HealthcheckControllerITests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var getHealthcheck: GetHealthcheck

    @Test
    fun `Should get the health check`() {
        // Given
        given(this.getHealthcheck.execute()).willReturn(
            Health(
                ZonedDateTime.parse("2020-12-21T15:01:00Z"),
                ZonedDateTime.parse("2020-12-21T16:01:00Z"),
                ZonedDateTime.parse("2020-12-21T17:01:00Z")
            )
        )

        // When
        mockMvc.perform(get("/bff/v1/healthcheck"))
            // Then
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.datePositionReceived", equalTo("2020-12-21T15:01:00Z")))
            .andExpect(jsonPath("$.dateLastPosition", equalTo("2020-12-21T16:01:00Z")))
            .andExpect(jsonPath("$.dateLogbookMessageReceived", equalTo("2020-12-21T17:01:00Z")))
    }
}
