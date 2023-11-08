import { openSideWindowMissionList } from './utils'

context('Side Window > Mission List > Export Activity Reports', () => {
  beforeEach(() => {
    openSideWindowMissionList()
  })

  it('Should download an activity report', () => {
    cy.cleanFiles()
    cy.clickButton('Exporter les ACT-REP')

    cy.fill('Début', [2020, 1, 17])
    cy.fill('Fin', [2021, 1, 12])
    cy.fill('JDP', 'JDP NS-01')

    cy.intercept(
      'GET',
      '/bff/v1/mission_actions/controls/activity_reports?beforeDateTime=2021-01-12T23:59:59.000Z&afterDateTime=2020-01-17T00:00:00.000Z&jdp=NORTH_SEA'
    ).as('getActivityReports')

    cy.clickButton('Exporter')
    cy.wait('@getActivityReports')

    // Then
    cy.wait(1000)
    cy.exec('cd cypress/downloads && ls').then(result => {
      const downloadedCSVFilename = result.stdout

      return cy
        .readFile(`cypress/downloads/${downloadedCSVFilename}`)
        .should(
          'contains',
          'PATROL_CODE,PATROL_TYPE,MEAN_ID,JDP_CODE,EVENT_TYPE,EVENT_DATE,EVENT_TIME,LS,PS1,PS2,PS3,NATIONAL_REFERENCE,OBJECT_TYPE,OBJECT_STATE,OBJECT_NATIONAL_ID'
        )
        .should(
          'contains',
          '"","L","Cross Etel","","INSPECTION","2020018","8:19","FRA","FRA","","","","Vessel","FRA","AYFAK000999999"'
        )
        .should(
          'contains',
          'RC,CFR,NA,ACTIVITY_CODE,GEAR_CODE,MESH_SIZE,FAO_AREA_CODE,FLEET_SEGMENT,LA,LO,PORT_CODE,COUNTRY_CODE,PORT_NAME,LOCATION,SPECIES1,WEIGHT1,NB_IND1,SPECIES2,WEIGHT2'
        )
        .should(
          'contains',
          '"CALLME","FAK000999999","PHENOMENE","LAN","OTB",58.9,"27.4.a","","","","AEFAT","FRA","Fateh Terminal","","JAX",450,"","CRF",40'
        )
    })
  })
})
