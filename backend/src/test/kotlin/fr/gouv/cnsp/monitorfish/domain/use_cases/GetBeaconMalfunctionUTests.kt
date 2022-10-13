package fr.gouv.cnsp.monitorfish.domain.use_cases

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import fr.gouv.cnsp.monitorfish.domain.entities.CommunicationMeans
import fr.gouv.cnsp.monitorfish.domain.entities.beacon_malfunctions.*
import fr.gouv.cnsp.monitorfish.domain.entities.vessel.VesselIdentifier
import fr.gouv.cnsp.monitorfish.domain.repositories.*
import fr.gouv.cnsp.monitorfish.domain.use_cases.beacon_malfunction.GetBeaconMalfunction
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.ZonedDateTime

@ExtendWith(SpringExtension::class)
class GetBeaconMalfunctionUTests {

    @MockBean
    private lateinit var beaconMalfunctionsRepository: BeaconMalfunctionsRepository

    @MockBean
    private lateinit var beaconMalfunctionCommentsRepository: BeaconMalfunctionCommentsRepository

    @MockBean
    private lateinit var beaconMalfunctionActionsRepository: BeaconMalfunctionActionsRepository

    @MockBean
    private lateinit var beaconMalfunctionNotificationsRepository: BeaconMalfunctionNotificationsRepository

    @MockBean
    private lateinit var lastPositionRepository: LastPositionRepository

    @Test
    fun `execute Should return the detailed beacon malfunction`() {
        // Given
        val now = ZonedDateTime.now().minusDays(1)
        given(beaconMalfunctionsRepository.find(1))
            .willReturn(BeaconMalfunction(1, "FR224226850", "1236514", "IRCS",
                null, VesselIdentifier.INTERNAL_REFERENCE_NUMBER, "BIDUBULE", VesselStatus.AT_SEA, Stage.END_OF_MALFUNCTION,
                 ZonedDateTime.now(), null, ZonedDateTime.now(),
                beaconNumber = "123465", beaconStatusAtMalfunctionCreation = BeaconStatus.ACTIVATED))
        given(beaconMalfunctionsRepository.findAllByVesselIdentifierEquals(eq(VesselIdentifier.INTERNAL_REFERENCE_NUMBER), eq("FR224226850"), any()))
            .willReturn(listOf(
                BeaconMalfunction(1, "FR224226850", "1236514", "IRCS",
                    null, VesselIdentifier.INTERNAL_REFERENCE_NUMBER, "BIDUBULE", VesselStatus.AT_SEA, Stage.END_OF_MALFUNCTION,
                     ZonedDateTime.now(), null, ZonedDateTime.now(),
                    beaconNumber = "123465", beaconStatusAtMalfunctionCreation = BeaconStatus.ACTIVATED),
                BeaconMalfunction(2, "FR224226850", "1236514", "IRCS",
                    null, VesselIdentifier.INTERNAL_REFERENCE_NUMBER, "BIDUBULE", VesselStatus.AT_SEA, Stage.INITIAL_ENCOUNTER,
                     ZonedDateTime.now(), null, ZonedDateTime.now(),
                    beaconNumber = "123465", beaconStatusAtMalfunctionCreation = BeaconStatus.ACTIVATED)))
        given(beaconMalfunctionCommentsRepository.findAllByBeaconMalfunctionId(1)).willReturn(listOf(BeaconMalfunctionComment(
            beaconMalfunctionId = 1, comment = "A comment", userType = BeaconMalfunctionCommentUserType.SIP, dateTime = now)))
        given(beaconMalfunctionActionsRepository.findAllByBeaconMalfunctionId(1)).willReturn(listOf(BeaconMalfunctionAction(
            beaconMalfunctionId = 1, propertyName = BeaconMalfunctionActionPropertyName.VESSEL_STATUS, nextValue = VesselStatus.ACTIVITY_DETECTED.toString(), previousValue = VesselStatus.AT_PORT.toString(), dateTime = now)))
        given(beaconMalfunctionNotificationsRepository.findAllByBeaconMalfunctionId(1)).willReturn(
            listOf(
                BeaconMalfunctionNotification(
                    id = 1, beaconMalfunctionId = 1, dateTimeUtc = now,
                    notificationType = BeaconMalfunctionNotificationType.MALFUNCTION_AT_PORT_INITIAL_NOTIFICATION,
                    communicationMeans = CommunicationMeans.SMS,
                    recipientFunction = BeaconMalfunctionNotificationRecipientFunction.VESSEL_CAPTAIN,
                    recipientName = "Jack Sparrow", recipientAddressOrNumber = "0000000000", success = true
                )
            )
        )

        // When
        val beaconMalfunctions = GetBeaconMalfunction(
            beaconMalfunctionsRepository,
            beaconMalfunctionCommentsRepository,
            beaconMalfunctionActionsRepository,
            lastPositionRepository,
            beaconMalfunctionNotificationsRepository
        )
            .execute(1)

        // Then
        assertThat(beaconMalfunctions.resume?.numberOfBeaconsAtSea).isEqualTo(1)
        assertThat(beaconMalfunctions.resume?.numberOfBeaconsAtPort).isEqualTo(1)
        assertThat(beaconMalfunctions.resume?.lastBeaconMalfunctionVesselStatus).isEqualTo(VesselStatus.AT_SEA)
        assertThat(beaconMalfunctions.actions).hasSize(1)
        assertThat(beaconMalfunctions.comments).hasSize(1)
        assertThat(beaconMalfunctions.notifications).hasSize(1)
        assertThat(beaconMalfunctions.notifications[0].beaconMalfunctionId).isEqualTo(1)
        assertThat(beaconMalfunctions.notifications[0].notificationType).isEqualTo(
            BeaconMalfunctionNotificationType.MALFUNCTION_AT_PORT_INITIAL_NOTIFICATION
        )
        assertThat(beaconMalfunctions.notifications[0].dateTimeUtc).isEqualTo(now)
        assertThat(beaconMalfunctions.notifications[0].notifications[0].recipientName).isEqualTo("Jack Sparrow")
        assertThat(beaconMalfunctions.beaconMalfunction.internalReferenceNumber).isEqualTo("FR224226850")
    }
}
