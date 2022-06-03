package fr.gouv.cnsp.monitorfish.domain.use_cases

import fr.gouv.cnsp.monitorfish.config.UseCase
import fr.gouv.cnsp.monitorfish.domain.entities.vessel.VesselIdentifier
import fr.gouv.cnsp.monitorfish.domain.entities.vessel.VesselTrackDepth
import fr.gouv.cnsp.monitorfish.domain.entities.vessel.VesselWithData
import fr.gouv.cnsp.monitorfish.domain.entities.risk_factor.VesselRiskFactor
import fr.gouv.cnsp.monitorfish.domain.repositories.LogbookReportRepository
import fr.gouv.cnsp.monitorfish.domain.repositories.PositionRepository
import fr.gouv.cnsp.monitorfish.domain.repositories.RiskFactorsRepository
import fr.gouv.cnsp.monitorfish.domain.repositories.VesselRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime

@UseCase
class GetVessel(private val vesselRepository: VesselRepository,
                private val positionRepository: PositionRepository,
                private val logbookReportRepository: LogbookReportRepository,
                private val riskFactorsRepository: RiskFactorsRepository) {
    private val logger: Logger = LoggerFactory.getLogger(GetVessel::class.java)

    suspend fun execute(internalReferenceNumber: String,
                        externalReferenceNumber: String,
                        ircs: String,
                        trackDepth: VesselTrackDepth,
                        vesselIdentifier: VesselIdentifier?,
                        fromDateTime: ZonedDateTime? = null,
                        toDateTime: ZonedDateTime? = null): Pair<Boolean, VesselWithData> {

        return coroutineScope {
            val (vesselTrackHasBeenModified, positions) = GetVesselPositions(positionRepository, logbookReportRepository).execute(
                    internalReferenceNumber = internalReferenceNumber,
                    externalReferenceNumber = externalReferenceNumber,
                    ircs = ircs,
                    trackDepth = trackDepth,
                    vesselIdentifier = vesselIdentifier,
                    fromDateTime = fromDateTime,
                    toDateTime = toDateTime)

            val vesselFuture = async { vesselRepository.findVessel(internalReferenceNumber, externalReferenceNumber, ircs) }

            val vesselRiskFactorsFuture = async { riskFactorsRepository.findVesselRiskFactors(internalReferenceNumber) }

            Pair(
                    vesselTrackHasBeenModified,
                    VesselWithData(
                            vesselFuture.await(),
                            positions.await(),
                            vesselRiskFactorsFuture.await() ?: VesselRiskFactor()
                    )
            )
        }
    }
}
