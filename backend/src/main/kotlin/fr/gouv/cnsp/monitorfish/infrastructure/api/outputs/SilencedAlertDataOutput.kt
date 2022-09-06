package fr.gouv.cnsp.monitorfish.infrastructure.api.outputs

import fr.gouv.cnsp.monitorfish.domain.entities.alerts.SilencedAlert
import fr.gouv.cnsp.monitorfish.domain.entities.alerts.type.AlertType
import fr.gouv.cnsp.monitorfish.domain.entities.vessel.VesselIdentifier
import java.time.ZonedDateTime

class SilencedAlertDataOutput(
  val id: Int? = null,
  val vesselName: String? = null,
  val internalReferenceNumber: String? = null,
  val externalReferenceNumber: String? = null,
  val ircs: String? = null,
  val vesselIdentifier: VesselIdentifier,
  val silencedBeforeDate: ZonedDateTime,
  val silencedAfterDate: ZonedDateTime? = null,
  val value: AlertType) {
  companion object {
    fun fromSilencedAlert(silencedAlert: SilencedAlert) = SilencedAlertDataOutput(
      id = silencedAlert.id,
      vesselName = silencedAlert.vesselName,
      internalReferenceNumber = silencedAlert.internalReferenceNumber,
      externalReferenceNumber = silencedAlert.externalReferenceNumber,
      ircs = silencedAlert.ircs,
      vesselIdentifier = silencedAlert.vesselIdentifier,
      silencedBeforeDate = silencedAlert.silencedBeforeDate,
      silencedAfterDate = silencedAlert.silencedAfterDate,
      value = silencedAlert.value)
  }
}
