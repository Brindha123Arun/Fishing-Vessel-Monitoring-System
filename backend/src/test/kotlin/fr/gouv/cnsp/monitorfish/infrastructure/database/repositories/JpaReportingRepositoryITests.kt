package fr.gouv.cnsp.monitorfish.infrastructure.database.repositories

import fr.gouv.cnsp.monitorfish.domain.entities.alerts.PendingAlert
import fr.gouv.cnsp.monitorfish.domain.entities.alerts.type.ThreeMilesTrawlingAlert
import fr.gouv.cnsp.monitorfish.domain.entities.reporting.*
import fr.gouv.cnsp.monitorfish.domain.entities.vessel.VesselIdentifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

class JpaReportingRepositoryITests : AbstractDBTests() {

    @Autowired
    private lateinit var jpaReportingRepository: JpaReportingRepository

    @Test
    @Transactional
    fun `save Should save a reporting from the pending alert`() {
        // Given
        val creationDate = ZonedDateTime.now()
        val now = ZonedDateTime.now()
        val alertOne = PendingAlert(
            internalReferenceNumber = "FRFGRGR",
            externalReferenceNumber = "RGD",
            ircs = "6554fEE",
            vesselIdentifier = VesselIdentifier.INTERNAL_REFERENCE_NUMBER,
            tripNumber = "123456",
            creationDate = creationDate,
            value = ThreeMilesTrawlingAlert("NAMO"))

        // When
        jpaReportingRepository.save(alertOne, now)
        val reporting = jpaReportingRepository.findAll()

        // Then
        assertThat(reporting).hasSize(9)
        assertThat(reporting.last().internalReferenceNumber).isEqualTo("FRFGRGR")
        assertThat(reporting.last().externalReferenceNumber).isEqualTo("RGD")
        val alert = reporting.last().value as ThreeMilesTrawlingAlert
        assertThat(alert.seaFront).isEqualTo("NAMO")
        assertThat(reporting.last().creationDate).isEqualTo(creationDate)
        assertThat(reporting.last().validationDate).isEqualTo(now)
    }

    @Test
    @Transactional
    fun `save Should save a reporting`() {
        // Given
        val creationDate = ZonedDateTime.now()
        val reporting = Reporting(
            internalReferenceNumber = "FRFGRGR",
            externalReferenceNumber = "RGD",
            ircs = "6554fEE",
            vesselIdentifier = VesselIdentifier.INTERNAL_REFERENCE_NUMBER,
            creationDate = creationDate,
            value = InfractionSuspicion(ReportingActor.OPS, natinfCode = "123456", title = "A title"),
            type = ReportingType.INFRACTION_SUSPICION,
            isDeleted = false,
            isArchived = false)

        // When
        jpaReportingRepository.save(reporting)
        val reportings = jpaReportingRepository.findAll()

        // Then
        assertThat(reportings).hasSize(9)
        assertThat(reportings.last().internalReferenceNumber).isEqualTo("FRFGRGR")
        assertThat(reportings.last().externalReferenceNumber).isEqualTo("RGD")
        assertThat(reportings.last().type).isEqualTo(ReportingType.INFRACTION_SUSPICION)
        val infraction = reportings.last().value as InfractionSuspicion
        assertThat(infraction.reportingActor).isEqualTo(ReportingActor.OPS)
        assertThat(infraction.natinfCode).isEqualTo("123456")
        assertThat(infraction.title).isEqualTo("A title")
        assertThat(reportings.last().creationDate).isEqualTo(creationDate)
        assertThat(reportings.last().validationDate).isNull()
    }

    @Test
    @Transactional
    fun `findCurrentAndArchivedByVesselIdentifierEquals Should return current and archived reporting`() {
        // When
        val reporting = jpaReportingRepository.findCurrentAndArchivedByVesselIdentifierEquals(
            VesselIdentifier.INTERNAL_REFERENCE_NUMBER, "ABC000180832", ZonedDateTime.now().minusYears(1))

        // Then
        assertThat(reporting).hasSize(2)
        assertThat(reporting.last().internalReferenceNumber).isEqualTo("ABC000180832")
        assertThat(reporting.last().isArchived).isEqualTo(true)
        assertThat(reporting.last().isDeleted).isEqualTo(false)
        val alertOne = reporting.last().value as ThreeMilesTrawlingAlert
        assertThat(alertOne.seaFront).isEqualTo("NAMO")

        assertThat(reporting.first().internalReferenceNumber).isEqualTo("ABC000180832")
        assertThat(reporting.first().isArchived).isEqualTo(false)
        assertThat(reporting.first().isDeleted).isEqualTo(false)
        val alertTwo = reporting.first().value as ThreeMilesTrawlingAlert
        assertThat(alertTwo.seaFront).isEqualTo("NAMO")
    }

    @Test
    @Transactional
    fun `findCurrentAndArchivedByVesselIdentifierEquals Should return current and archived reporting When filtering with date`() {
        // When
        val reporting = jpaReportingRepository.findCurrentAndArchivedByVesselIdentifierEquals(
            VesselIdentifier.INTERNAL_REFERENCE_NUMBER, "ABC000180832", ZonedDateTime.now())

        // Then
        assertThat(reporting).hasSize(1)
        assertThat(reporting.first().internalReferenceNumber).isEqualTo("ABC000180832")
        assertThat(reporting.first().isArchived).isEqualTo(false)
        assertThat(reporting.first().isDeleted).isEqualTo(false)
        val alertTwo = reporting.first().value as ThreeMilesTrawlingAlert
        assertThat(alertTwo.seaFront).isEqualTo("NAMO")
    }

    @Test
    @Transactional
    fun `archive Should set the archived flag as true`() {
        // Given
        val reportingToArchive = jpaReportingRepository.findCurrentAndArchivedByVesselIdentifierEquals(
            VesselIdentifier.INTERNAL_REFERENCE_NUMBER, "ABC000180832", ZonedDateTime.now().minusYears(1)).first()
        assertThat(reportingToArchive.isArchived).isEqualTo(false)

        // When
        jpaReportingRepository.archive(reportingToArchive.id!!)

        // Then
        val archivedReporting = jpaReportingRepository.findCurrentAndArchivedByVesselIdentifierEquals(
            VesselIdentifier.INTERNAL_REFERENCE_NUMBER, "ABC000180832", ZonedDateTime.now().minusYears(1)).first()
        assertThat(archivedReporting.isArchived).isEqualTo(true)
    }

    @Test
    @Transactional
    fun `delete Should set the deleted flag as true`() {
        // Given
        val reportingList = jpaReportingRepository.findCurrentAndArchivedByVesselIdentifierEquals(
            VesselIdentifier.INTERNAL_REFERENCE_NUMBER, "ABC000180832", ZonedDateTime.now().minusYears(1))
        assertThat(reportingList).hasSize(2)

        // When
        jpaReportingRepository.delete(reportingList.first().id!!)

        // Then
        val nextReportingList = jpaReportingRepository.findCurrentAndArchivedByVesselIdentifierEquals(
            VesselIdentifier.INTERNAL_REFERENCE_NUMBER, "ABC000180832", ZonedDateTime.now().minusYears(1))
        assertThat(nextReportingList).hasSize(1)
    }

  @Test
  @Transactional
  fun `findAllCurrent Should return current reportings`() {
    // When
    val reporting = jpaReportingRepository.findAllCurrent()

    // Then
    assertThat(reporting).hasSize(5)
    assertThat(reporting.first().internalReferenceNumber).isEqualTo("ABC000180832")
    assertThat(reporting.first().isArchived).isEqualTo(false)
    assertThat(reporting.first().isDeleted).isEqualTo(false)
  }

    @Test
    @Transactional
    fun `update Should update a given InfractionSuspicion`() {
        // Given
        val updatedReporting = InfractionSuspicion(
            ReportingActor.UNIT,
            "An unit",
            "",
            "Jean Bon",
            "Une observation",
            "Une description",
            "1236",
            "MEMN",
            "FR",
            "DML 56")

        // When
        val reporting = jpaReportingRepository.update(6, updatedReporting)

        // Then
        assertThat(reporting.internalReferenceNumber).isEqualTo("ABC000042310")
        assertThat((reporting.value as InfractionSuspicion).reportingActor).isEqualTo(updatedReporting.reportingActor)
        assertThat((reporting.value as InfractionSuspicion).unit).isEqualTo(updatedReporting.unit)
        assertThat((reporting.value as InfractionSuspicion).authorTrigram).isEqualTo(updatedReporting.authorTrigram)
        assertThat((reporting.value as InfractionSuspicion).authorContact).isEqualTo(updatedReporting.authorContact)
        assertThat((reporting.value as InfractionSuspicion).title).isEqualTo(updatedReporting.title)
        assertThat((reporting.value as InfractionSuspicion).description).isEqualTo(updatedReporting.description)
        assertThat((reporting.value as InfractionSuspicion).natinfCode).isEqualTo(updatedReporting.natinfCode)
        assertThat((reporting.value as InfractionSuspicion).seaFront).isEqualTo(updatedReporting.seaFront)
        assertThat((reporting.value as InfractionSuspicion).flagState).isEqualTo(updatedReporting.flagState)
        assertThat((reporting.value as InfractionSuspicion).dml).isEqualTo(updatedReporting.dml)
    }

    @Test
    @Transactional
    fun `update Should update a given Observation`() {
        // Given
        val updatedReporting = Observation(
            ReportingActor.UNIT,
            "An unit",
            "",
            "Jean Bon",
            "Une observation",
            "Une description",
            "MEMN",
            "FR")

        // When
        val reporting = jpaReportingRepository.update(8, updatedReporting)

        // Then
        assertThat(reporting.internalReferenceNumber).isEqualTo("ABC000597493")
        assertThat((reporting.value as Observation).reportingActor).isEqualTo(updatedReporting.reportingActor)
        assertThat((reporting.value as Observation).unit).isEqualTo(updatedReporting.unit)
        assertThat((reporting.value as Observation).authorTrigram).isEqualTo(updatedReporting.authorTrigram)
        assertThat((reporting.value as Observation).authorContact).isEqualTo(updatedReporting.authorContact)
        assertThat((reporting.value as Observation).title).isEqualTo(updatedReporting.title)
        assertThat((reporting.value as Observation).description).isEqualTo(updatedReporting.description)
        assertThat((reporting.value as Observation).seaFront).isEqualTo(updatedReporting.seaFront)
        assertThat((reporting.value as Observation).flagState).isEqualTo(updatedReporting.flagState)
    }
}
