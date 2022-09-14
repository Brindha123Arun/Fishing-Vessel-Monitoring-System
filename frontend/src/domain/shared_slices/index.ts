import { combineReducers } from '@reduxjs/toolkit'

import regulation from '../../features/backoffice/Regulation.slice'
import regulatoryLayerSearch from '../../features/layers/regulatory/search/RegulatoryLayerSearch.slice'
import vesselList from '../../features/vessel_list/VesselList.slice'
import { alertReducer } from './Alert'
import { beaconMalfunctionReducer } from './BeaconMalfunction'
import { controlReducer } from './Control'
import { favoriteVesselReducer } from './FavoriteVessel'
import { filterReducer } from './Filter'
import { fishingActivitiesReducer } from './FishingActivities'
import fleetSegment from './FleetSegment'
import gear from './Gear'
import { globalSliceReducer } from './Global'
import infraction from './Infraction'
import { interestPointReducer } from './InterestPoint'
import layer from './Layer'
import { mapReducer } from './Map'
import measurement from './Measurement'
import { regulatoryReducer } from './Regulatory'
import reporting from './Reporting'
import species from './Species'
import { vesselSliceReducer } from './Vessel'

const commonReducerList = {
  gear,
  global: globalSliceReducer,
  map: mapReducer,
  regulatory: regulatoryReducer,
  species
}

const homeReducers = combineReducers({
  ...commonReducerList,
  alert: alertReducer,
  beaconMalfunction: beaconMalfunctionReducer,
  //  TODO Pass that to singular.
  controls: controlReducer,
  favoriteVessel: favoriteVesselReducer,
  filter: filterReducer,
  fishingActivities: fishingActivitiesReducer,
  fleetSegment,
  infraction,
  interestPoint: interestPointReducer,
  layer: layer.homepage.reducer,
  measurement,
  regulatoryLayerSearch,
  reporting,
  vessel: vesselSliceReducer,
  vesselList
})

const backofficeReducers = combineReducers({
  ...commonReducerList,
  fleetSegment,
  layer: layer.backoffice.reducer,
  regulation
})

export { homeReducers, backofficeReducers }
