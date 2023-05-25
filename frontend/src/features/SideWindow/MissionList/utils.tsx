import { Tag, TagBullet } from '@mtes-mct/monitor-ui'
import { uniq } from 'lodash/fp'
import styled from 'styled-components'

import { Mission } from '../../../domain/entities/mission/types'
import { FrontendError } from '../../../libs/FrontendError'

import type { ControlUnit } from '../../../domain/types/controlUnit'

export function getControlUnitsNamesFromAdministrations(
  controlUnits: ControlUnit.ControlUnit[],
  administrations: string[]
): string[] {
  const names = controlUnits
    .filter(({ administration }) => administrations.includes(administration))
    .map(({ name }) => name)
  const uniqueNames = uniq(names)
  const uniqueSortedNames = uniqueNames.sort()

  return uniqueSortedNames
}

export const renderStatus = (missionStatus: Mission.MissionStatus): JSX.Element => {
  switch (missionStatus) {
    case Mission.MissionStatus.UPCOMING:
      return (
        <StyledTag bullet={TagBullet.DISK} bulletColor="#52B0FF" style={{ color: '#52B0FF' }}>
          {Mission.MissionStatusLabel.UPCOMING}
        </StyledTag>
      )

    case Mission.MissionStatus.IN_PROGRESS:
      return (
        <StyledTag bullet={TagBullet.DISK} bulletColor="#3660FA" style={{ color: '#3660FA' }}>
          {Mission.MissionStatusLabel.IN_PROGRESS}
        </StyledTag>
      )

    case Mission.MissionStatus.DONE:
      return (
        <StyledTag bullet={TagBullet.DISK} bulletColor="#1400AD" style={{ color: '#1400AD' }}>
          {Mission.MissionStatusLabel.DONE}
        </StyledTag>
      )

    case Mission.MissionStatus.CLOSED:
      return (
        <StyledTagWithCheck bulletColor="#463939" style={{ color: '#463939' }}>
          <span>✓</span>
          {Mission.MissionStatusLabel.CLOSED}
        </StyledTagWithCheck>
      )

    default:
      throw new FrontendError("`missionStatus` doesn't match `MissionStatus` enum.")
  }
}

// TODO Remove this hack once we get rid of local CSS.
const StyledTag = styled(Tag)`
  align-items: flex-end;
  display: flex;
  line-height: 1;

  > span {
    height: 10px;
    margin-right: 6px;
    width: 10px;
  }
`

// TODO Add check in icons and `TagBullet` in monitor-ui.
const StyledTagWithCheck = styled(StyledTag)`
  > span {
    font-size: 16px;
    height: auto;
    line-height: 13px;
    margin-right: 6px;
    width: 10px;
  }
`
