import { useEffect, useRef } from 'react'
import styled from 'styled-components'

import { contractRightMenu, expandRightMenu } from '../../../domain/shared_slices/Global'
import { useAppDispatch } from '../../../hooks/useAppDispatch'
import { useAppSelector } from '../../../hooks/useAppSelector'
import { useClickOutsideWhenOpened } from '../../../hooks/useClickOutsideWhenOpened'

export function RightMenuOnHoverArea() {
  const dispatch = useAppDispatch()
  const selectedVessel = useAppSelector(state => state.vessel.selectedVessel)
  const mapToolOpened = useAppSelector(state => state.global.mapToolOpened)

  const areaRef = useRef(null)
  const clickedOutsideComponent = useClickOutsideWhenOpened(areaRef, selectedVessel)

  useEffect(() => {
    if (clickedOutsideComponent && selectedVessel && mapToolOpened === undefined) {
      dispatch(contractRightMenu())
    } else {
      dispatch(expandRightMenu())
    }
  }, [dispatch, clickedOutsideComponent, mapToolOpened, selectedVessel])

  return selectedVessel && <Area ref={areaRef} onMouseEnter={() => dispatch(expandRightMenu())} />
}

const Area = styled.div`
  height: 500px;
  right: 0;
  width: 60px;
  opacity: 0;
  position: absolute;
  top: 0;
`
