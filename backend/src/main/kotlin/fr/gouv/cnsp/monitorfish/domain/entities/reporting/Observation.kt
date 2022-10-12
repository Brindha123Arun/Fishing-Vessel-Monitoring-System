package fr.gouv.cnsp.monitorfish.domain.entities.reporting

import fr.gouv.cnsp.monitorfish.domain.use_cases.reporting.UpdatedInfractionSuspicionOrObservation

class Observation(
    override val reportingActor: ReportingActor,
    override val unit: String? = null,
    override val authorTrigram: String? = null,
    override val authorContact: String? = null,
    override val title: String,
    override val description: String? = null,
    override var seaFront: String? = null,
    override val flagState: String? = null
) : InfractionSuspicionOrObservationType(
    reportingActor = reportingActor,
    natinfCode = null,
    title = title,
    type = ReportingTypeMapping.OBSERVATION,
    flagState = flagState
) {
    companion object {
        fun fromUpdatedReporting(updatedInfractionSuspicionOrObservation: UpdatedInfractionSuspicionOrObservation): Observation = Observation(
            reportingActor = updatedInfractionSuspicionOrObservation.reportingActor,
            unit = updatedInfractionSuspicionOrObservation.unit,
            authorTrigram = updatedInfractionSuspicionOrObservation.authorTrigram,
            authorContact = updatedInfractionSuspicionOrObservation.authorContact,
            title = updatedInfractionSuspicionOrObservation.title,
            flagState = updatedInfractionSuspicionOrObservation.flagState,
            seaFront = updatedInfractionSuspicionOrObservation.seaFront,
            description = updatedInfractionSuspicionOrObservation.description
        )
    }
}
