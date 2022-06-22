package fr.gouv.cnsp.monitorfish.domain.repositories

import fr.gouv.cnsp.monitorfish.domain.entities.controls.Infraction

interface InfractionRepository {
    fun findInfractions(ids: List<Int>): List<Infraction>
    fun findInfractionByNatinfCode(natinfCode: String): Infraction
    fun findFishingInfractions(): List<Infraction>
}
