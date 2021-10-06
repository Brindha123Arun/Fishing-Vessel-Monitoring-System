/// <reference types="cypress" />

const port = Cypress.env('PORT') ? Cypress.env('PORT') : 3000

context('LayersSidebar', () => {
  beforeEach(() => {
    cy.viewport(1280, 1024)
    cy.visit(`http://localhost:${port}/#@-224002.65,6302673.54,8.70`)

    cy.request('GET', 'http://localhost:8081/geoserver/wfs?service=WFS&version=1.1.0&request=GetFeature&typename=monitorfish:regulatory_areas&outputFormat=application/json&propertyName=law_type,layer_name,engins,engins_interdits,especes,especes_interdites,references_reglementaires,zones,facade,region').then(
      (response) => {
        cy.log(response.body)
      }
    )

    cy.get('*[data-cy^="first-loader"]', { timeout: 20000 }).should('not.exist')
    cy.url().should('include', '@-22')
  })

  it('A regulation Should be searched, added to My Zones and showed on the map with the Zone button', () => {
    // When
    cy.get('*[data-cy^="layers-sidebar"]').click({ timeout: 20000 })

    // Add the layer to My Zones
    cy.get('*[data-cy^="regulatory-search-input"]').type('Cotentin')
    cy.get('*[data-cy^="regulatory-layer-topic"]').click({ timeout: 20000 })
    cy.get('*[data-cy^="regulatory-zone-check"]').click({ timeout: 20000 })
    cy.get('*[data-cy^="regulatory-search-add-zones-button"]').contains('Ajouter 1 zone')
    cy.get('*[data-cy^="regulatory-search-add-zones-button"]').click()

    // Then it is in "My Zones"
    cy.get('*[data-cy="regulatory-layers-my-zones"]').click()
    cy.get('*[data-cy="regulatory-layers-my-zones-topic"]').contains('Ouest Cotentin Bivalves')
    cy.get('*[data-cy="regulatory-layers-my-zones-topic"]').click()
    cy.get('*[data-cy="regulatory-layers-my-zones-zone"]').contains('Praires Ouest cotentin')
                                                                                                                        
    // Show a zone with the zone button
    cy.log('Show a zone with the zone button')
    cy.get('*[data-cy="regulatory-layers-my-zones-zone-show"]').eq(0).click({ timeout: 20000 })
    cy.wait(1000)

    cy.get('canvas', { timeout: 20000 }).eq(0).click(490, 580, { timeout: 20000, force: true })
    cy.get('*[data-cy="regulatory-layers-metadata-seafront"]').contains('MEMN')

    // Close the metadata modal and hide the zone
    cy.get('*[data-cy="regulatory-layers-metadata-close"]').click()
    cy.get('*[data-cy="regulatory-layers-my-zones-zone-hide"]').eq(0).click({ timeout: 20000 })

    // The layer is hidden, the metadata modal should not be opened
    cy.get('canvas', { timeout: 20000 }).eq(0).click(490, 580, { timeout: 20000, force: true })
    cy.get('*[data-cy="regulatory-layers-metadata-seafront"]', { timeout: 20000 }).should('not.exist')
  })

  it('A regulation Should be searched, added to My Zones and showed on the map with the Topic button', () => {
    // When
    cy.get('*[data-cy^="layers-sidebar"]').click({ timeout: 20000 })

    // Add the layer to My Zones
    cy.get('*[data-cy^="regulatory-search-input"]').type('Cotentin')
    cy.get('*[data-cy^="regulatory-layer-topic"]').click({ timeout: 20000 })
    cy.get('*[data-cy^="regulatory-zone-check"]').click({ timeout: 20000 })
    cy.get('*[data-cy^="regulatory-search-add-zones-button"]').contains('Ajouter 1 zone')
    cy.get('*[data-cy^="regulatory-search-add-zones-button"]').click()

    // Then it is in "My Zones"
    cy.get('*[data-cy="regulatory-layers-my-zones"]').click()
    cy.get('*[data-cy="regulatory-layers-my-zones-topic"]').contains('Ouest Cotentin Bivalves')
    cy.get('*[data-cy="regulatory-layers-my-zones-topic"]').click()
    cy.get('*[data-cy="regulatory-layers-my-zones-zone"]').contains('Praires Ouest cotentin')

    // Show a zone with the topic button
    cy.log('Show a zone with the topic button')
    cy.get('*[data-cy="regulatory-layers-my-zones-topic-show"]').eq(0).click({ timeout: 20000 })
    cy.wait(1000)

    cy.get('canvas').eq(0).click(490, 580, { timeout: 20000, force: true })
    cy.get('*[data-cy="regulatory-layers-metadata-seafront"]').contains('MEMN')
    cy.get('*[data-cy="regulatory-layers-metadata-close"]').click()

    // Delete the zone
    cy.get('*[data-cy="regulatory-layers-my-zones-zone-delete"]').click()
    cy.get('*[data-cy="regulatory-layers-my-zones-topic"]', { timeout: 20000 }).should('not.exist');

    // The layer is hidden, the metadata modal should not be opened
    cy.get('canvas').eq(0).click(490, 580, { timeout: 20000, force: true })
    cy.get('*[data-cy="regulatory-layers-metadata-seafront"]', { timeout: 20000 }).should('not.exist')

    // Close the layers sidebar
    cy.get('*[data-cy^="layers-sidebar"]', { timeout: 20000 }).click({ timeout: 20000 })
  })

  it('A regulation metadata Should be opened', () => {
    // When
    cy.get('*[data-cy^="layers-sidebar"]').click({ timeout: 20000 })

    cy.get('*[data-cy^="regulatory-search-input"]').type('Cotentin')
    cy.get('*[data-cy^="regulatory-layer-topic"]').click({ timeout: 20000 })
    cy.get('*[data-cy^="regulatory-zone-check"]').click({ timeout: 20000 })
    cy.get('*[data-cy^="regulatory-search-add-zones-button"]').click()
    cy.get('*[data-cy="regulatory-layers-my-zones"]').click()
    cy.get('*[data-cy="regulatory-layers-my-zones-topic"]').click()

    // Then show the metadata
    cy.get('*[data-cy="regulatory-layers-show-metadata"]').click()
    cy.get('*[data-cy="regulatory-layers-metadata-seafront"]').contains('MEMN')
    cy.get('*[data-cy="regulatory-layers-metadata-gears"]').contains('Dragues remorquées par bateau (DRB)')
  })

  it('An advanced search Should filter the search result', () => {
    // When
    cy.get('*[data-cy^="layers-sidebar"]').click({ timeout: 20000 })

    cy.get('*[data-cy^="regulatory-search-input"]').type('Cotentin')
    cy.get('*[data-cy="regulatory-layers-advanced-search"]').click()

    cy.get('*[data-cy^="regulatory-layers-advanced-search-zone"]').type('MEMN')
    cy.get('*[data-cy^="regulatory-layers-advanced-search-gears"]').type('DRB')
    cy.get('*[data-cy^="regulatory-layers-advanced-search-species"]').type('VEV')
    cy.get('*[data-cy^="regulatory-layer-topic"]').contains('Ouest Cotentin Bivalves')
  })

  it('An administrative zone Should be showed and hidden', () => {
    // When
    cy.get('*[data-cy^="layers-sidebar"]').click({ timeout: 20000 })
    cy.get('*[data-cy^="administrative-zones-open"]').click({ timeout: 20000 })
    cy.get('*[data-cy^="administrative-layer-toggle"]').eq(0).click({ timeout: 20000 })
    cy.wait(500)

    // Then
    cy.get('.ol-layer').toMatchImageSnapshot({
      clip: { x: 400, y: 0, width: 200, height: 200 }
    })

    cy.get('*[data-cy^="administrative-layer-toggle"]').eq(0).click({ timeout: 20000 })
  })
})
