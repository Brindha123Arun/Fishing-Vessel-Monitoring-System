import React, { useEffect, useRef, useState } from 'react'
import MapMenuOverlay from './overlays/MapMenuOverlay'
import { HIT_PIXEL_TO_TOLERANCE } from '../../constants/constants'
import LayersEnum from '../../domain/entities/layers'
import { useSelector } from 'react-redux'

/**
 * Handle map menu - Note that the map parameter is given from
 * the BaseMap component, event if it's not seen in the props passed to MapMenu
 */
const MapMenu = ({ map }) => {
  const {
    vessels
  } = useSelector(state => state.vessel)
  const [coordinates, setCoordinates] = useState([])
  const vessel = useRef(null)

  useEffect(() => {
    if (map) {
      function showMenu (event) {
        event.preventDefault()

        const pixel = map.getEventPixel(event)
        const feature = map.forEachFeatureAtPixel(pixel, feature => feature, { hitTolerance: HIT_PIXEL_TO_TOLERANCE })
        const clickedFeatureId = feature?.getId()

        if (clickedFeatureId?.toString()?.includes(LayersEnum.VESSELS.code)) {
          const clickedVessel = vessels.find(vessel => {
            return clickedFeatureId?.toString()?.includes(vessel.vesselId)
          })

          if (clickedVessel) {
            vessel.current = clickedVessel
            setCoordinates(feature.getGeometry().getCoordinates())
            return
          }

          return
        }

        vessel.current = null
        setCoordinates([])
      }

      map.getViewport().addEventListener('contextmenu', showMenu)

      return () => {
        map.getViewport().removeEventListener('contextmenu', showMenu)
      }
    }
  }, [map, vessels])

  return (
    <>
      <MapMenuOverlay map={map} coordinates={coordinates} vessel={vessel.current}/>
    </>
  )
}

export default MapMenu
