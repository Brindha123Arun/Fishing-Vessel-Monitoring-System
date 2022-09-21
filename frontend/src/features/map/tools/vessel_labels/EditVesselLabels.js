import { useMemo } from 'react'
import styled from 'styled-components'
import { useDispatch, useSelector } from 'react-redux'

import { ReactComponent as LabelSVG } from '../../../icons/Menu_etiquettes_navires.svg'
import { ReactComponent as RiskFactorSVG } from '../../../icons/Bouton_afficher_note_de_risque.svg'
import { COLORS } from '../../../../constants/constants'
import {
  setRiskFactorShowedOnMap,
  setVesselLabel,
  setVesselLabelsShowedOnMap
} from '../../../../domain/shared_slices/Map'
import VesselLabelSelection from './VesselLabelSelection'
import MapPropertyTrigger from '../../../commonComponents/MapPropertyTrigger'
import { MapTool } from '../../../../domain/entities/map'
import { MapToolBox } from '../MapToolBox'

const EditVesselLabels = () => {
  const dispatch = useDispatch()
  const vesselLabel = useSelector(state => state.map.vesselLabel)
  const {
    vesselLabelsShowedOnMap,
    riskFactorShowedOnMap
  } = useSelector(state => state.map)
  const {
    healthcheckTextWarning,
    isAdmin,
    mapToolOpened
  } = useSelector(state => state.global)

  const isOpen = useMemo(() => mapToolOpened === MapTool.VESSEL_LABELS, [mapToolOpened])

  return (
    <Wrapper
      healthcheckTextWarning={healthcheckTextWarning}
      isOpen={isOpen}>
      <Header isFirst={false}>
        Affichage des étiquettes { isAdmin ? 'et notes des navires' : ''}
      </Header>
      <VesselLabel>
        Choisir le libellé des étiquettes des navires
      </VesselLabel>
      <VesselLabelSelection
        updateVesselLabel={label => dispatch(setVesselLabel(label))}
        vesselLabel={vesselLabel}
        isAdmin={isAdmin}
      />
      <MapPropertyTrigger
        booleanProperty={vesselLabelsShowedOnMap}
        updateBooleanProperty={isShowed => dispatch(setVesselLabelsShowedOnMap(isShowed))}
        text={'les étiquettes des navires'}
        Icon={LabelSVG}
      />
      {
        isAdmin && <MapPropertyTrigger
          booleanProperty={riskFactorShowedOnMap}
          updateBooleanProperty={isShowed => dispatch(setRiskFactorShowedOnMap(isShowed))}
          text={'la note de risque des navires'}
          Icon={RiskFactorSVG}
        />
      }

    </Wrapper>
  )
}

const VesselLabel = styled.div`
  margin: 15px 5px 3px 20px;
  font-size: 13px;
  color: ${COLORS.slateGray};
  text-align: left;
`

const Header = styled.div`
  background: ${COLORS.charcoal};
  color: ${COLORS.gainsboro};
  padding: 9px 0 7px 15px;
  font-size: 16px;
  text-align: left;
  border-top-left-radius: ${props => props.isFirst ? '2px' : '0'};
  border-top-right-radius: ${props => props.isFirst ? '2px' : '0'};
`

const Wrapper = styled(MapToolBox)`
  width: 406px;
  top: 194px;
`

export default EditVesselLabels
