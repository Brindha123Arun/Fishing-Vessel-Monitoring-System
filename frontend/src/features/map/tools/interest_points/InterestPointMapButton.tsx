import { useCallback, useEffect, useMemo, useRef } from 'react'
import styled from 'styled-components'

import { MapTool } from '../../../../domain/entities/map'
import { setMapToolOpened } from '../../../../domain/shared_slices/Global'
import {
  deleteInterestPointBeingDrawed,
  drawInterestPoint,
  endInterestPointDraw
} from '../../../../domain/shared_slices/InterestPoint'
import { useAppDispatch } from '../../../../hooks/useAppDispatch'
import { useAppSelector } from '../../../../hooks/useAppSelector'
import { useEscapeFromKeyboard } from '../../../../hooks/useEscapeFromKeyboard'
import { ReactComponent as InterestPointSVG } from '../../../icons/standardized/Landmark.svg'
import { MapToolButton } from '../MapToolButton'
import EditInterestPoint from './EditInterestPoint'

export function InterestPointMapButton() {
  const dispatch = useAppDispatch()
  const { healthcheckTextWarning, mapToolOpened, rightMenuIsOpen } = useAppSelector(state => state.global)
  const isRightMenuShrinked = !rightMenuIsOpen
  const isOpen = useMemo(() => mapToolOpened === MapTool.INTEREST_POINT, [mapToolOpened])
  const wrapperRef = useRef(null)
  const escapeFromKeyboard = useEscapeFromKeyboard()

  const close = useCallback(() => {
    dispatch(endInterestPointDraw())
    dispatch(setMapToolOpened(undefined))
    dispatch(deleteInterestPointBeingDrawed())
  }, [dispatch])

  useEffect(() => {
    if (escapeFromKeyboard && isOpen) {
      close()
    }
  }, [escapeFromKeyboard, isOpen, close])

  useEffect(() => {
    if (!isOpen) {
      dispatch(endInterestPointDraw())
      dispatch(deleteInterestPointBeingDrawed())
    }
  }, [dispatch, isOpen])

  const openOrCloseInterestPoint = useCallback(() => {
    if (!isOpen) {
      dispatch(drawInterestPoint())
      dispatch(setMapToolOpened(MapTool.INTEREST_POINT))
    } else {
      close()
    }
  }, [dispatch, isOpen, close])

  return (
    <Wrapper ref={wrapperRef}>
      <InterestPointButton
        dataCy="interest-point"
        isOpen={isOpen}
        onClick={openOrCloseInterestPoint}
        style={{ top: 291 }}
        title={"Créer un point d'intérêt"}
      >
        <InterestPointIcon $isRightMenuShrinked={isRightMenuShrinked} />
      </InterestPointButton>
      <EditInterestPoint close={close} healthcheckTextWarning={healthcheckTextWarning} isOpen={isOpen} />
    </Wrapper>
  )
}

const Wrapper = styled.div`
  transition: all 0.2s;
  z-index: 1000;
`

const InterestPointButton = styled(MapToolButton)``

const InterestPointIcon = styled(InterestPointSVG)<{
  $isRightMenuShrinked: boolean
}>`
  width: 25px;
  height: 25px;
  opacity: ${p => (p.$isRightMenuShrinked ? '0' : '1')};
  transition: all 0.2s;
  rect:first-of-type {
    fill: ${p => p.theme.color.gainsboro};
  }
  path:first-of-type {
    fill: ${p => p.theme.color.gainsboro};
  }
`
