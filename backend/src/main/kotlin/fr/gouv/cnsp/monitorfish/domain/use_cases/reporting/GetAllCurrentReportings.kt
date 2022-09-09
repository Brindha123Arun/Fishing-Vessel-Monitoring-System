package fr.gouv.cnsp.monitorfish.domain.use_cases.reporting

import fr.gouv.cnsp.monitorfish.config.UseCase
import fr.gouv.cnsp.monitorfish.domain.entities.reporting.Reporting
import fr.gouv.cnsp.monitorfish.domain.entities.vessel.VesselIdentifier
import fr.gouv.cnsp.monitorfish.domain.repositories.LastPositionRepository
import fr.gouv.cnsp.monitorfish.domain.repositories.ReportingRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@UseCase
class GetAllCurrentReportings(private val reportingRepository: ReportingRepository,
                              private val lastPositionRepository: LastPositionRepository) {
    private val logger: Logger = LoggerFactory.getLogger(GetAllCurrentReportings::class.java)

    fun execute(): List<Reporting> {
        val currents = reportingRepository.findAllCurrent()

        currents.forEach {
            it.underCharter = try {
                when (it.vesselIdentifier) {
                    VesselIdentifier.INTERNAL_REFERENCE_NUMBER -> {
                        require(it.internalReferenceNumber != null) {
                            "The fields 'internalReferenceNumber' must be not null when the vessel identifier is INTERNAL_REFERENCE_NUMBER."
                        }
                        lastPositionRepository.findUnderCharterForVessel(it.vesselIdentifier, it.internalReferenceNumber)
                    }
                    VesselIdentifier.IRCS -> {
                        require(it.ircs != null) {
                            "The fields 'ircs' must be not null when the vessel identifier is IRCS."
                        }
                        lastPositionRepository.findUnderCharterForVessel(it.vesselIdentifier, it.ircs)
                    }
                    VesselIdentifier.EXTERNAL_REFERENCE_NUMBER -> {
                        require(it.externalReferenceNumber != null) {
                            "The fields 'externalReferenceNumber' must be not null when the vessel identifier is EXTERNAL_REFERENCE_NUMBER."
                        }
                        lastPositionRepository.findUnderCharterForVessel(it.vesselIdentifier, it.externalReferenceNumber)
                    }
                }
            } catch (e: Throwable) {
                logger.error(e.message)

                null
            }
        }

        return currents
    }
}
