package fr.gouv.cnsp.monitorfish.infrastructure.api.input

import fr.gouv.cnsp.monitorfish.domain.entities.reporting.ReportingActor
import fr.gouv.cnsp.monitorfish.domain.entities.reporting.ReportingType
import fr.gouv.cnsp.monitorfish.domain.use_cases.reporting.UpdatedInfractionSuspicionOrObservation

class UpdateReportingDataInput(
    val reportingActor: ReportingActor,
    val reportingType: ReportingType,
    val unit: String? = null,
    val authorTrigram: String? = null,
    val authorContact: String? = null,
    val title: String,
    val description: String? = null,
    val natinfCode: String? = null
) {
    fun toUpdatedReportingValues() = UpdatedInfractionSuspicionOrObservation(
        reportingActor = this.reportingActor,
        reportingType = this.reportingType,
        unit = this.unit,
        authorTrigram = this.authorTrigram,
        authorContact = this.authorContact,
        title = this.title,
        description = this.description,
        natinfCode = this.natinfCode
    )
}
