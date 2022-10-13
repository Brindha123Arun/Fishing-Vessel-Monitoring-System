package fr.gouv.cnsp.monitorfish.infrastructure.database.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager
import org.springframework.transaction.annotation.Transactional

class JpaBeaconRepositoryITests : AbstractDBTests() {

    @Autowired
    private lateinit var jpaBeaconRepository: JpaBeaconRepository

    @Autowired
    lateinit var cacheManager: CacheManager

    @BeforeEach
    fun setup() {
        cacheManager.getCache("search_beacons")?.clear()
    }

    @Test
    @Transactional
    fun `search Should return a vessel When part of the beacon number is given`() {
        // When
        val vessels = jpaBeaconRepository.search("FGE")

        assertThat(vessels).hasSize(1)
        assertThat(vessels.first().beaconNumber).isEqualTo("FGEDX85")
    }

    @Test
    @Transactional
    fun `findActivatedBeaconNumbers Should return beacon numbers of activated beacons`() {
        // When
        val beaconNumbers = jpaBeaconRepository.findActivatedBeaconNumbers()

        assertThat(beaconNumbers).hasSize(6)
        assertThat(beaconNumbers).isEqualTo(listOf("FGEDX85", "123456", "ETETE4", "A56CZ2", "NB56FR8", "PO8U9U4"))
    }
}
