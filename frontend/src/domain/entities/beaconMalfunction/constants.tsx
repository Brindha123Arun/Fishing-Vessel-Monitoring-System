import styled from 'styled-components'

import { COLORS } from '../../../constants/constants'
import { ReactComponent as VesselStatusActivityDetectedSVG } from '../../../features/icons/Avarie_statut_activite_detectee.svg'
import { ReactComponent as VesselStatusAtPortSVG } from '../../../features/icons/Avarie_statut_navire_a_quai.svg'
import { ReactComponent as VesselStatusAtSeaSVG } from '../../../features/icons/Avarie_statut_navire_en_mer.svg'
import { ReactComponent as VesselStatusNoNewsSVG } from '../../../features/icons/Avarie_statut_sans_nouvelles.svg'
import { ReactComponent as VesselStatusNeverEmittedSVG } from '../../../features/icons/never_emitted.svg'

import type {
  BeaconMalfunctionStageColumnValue,
  BeaconMalfunctionStatusValue,
  EnfOfBeaconMalfunctionStatusValue
} from '../../types/beaconMalfunction'

const BeaconMalfunctionsTab = {
  DETAIL: 2,
  RESUME: 1
}

const UserType = {
  OPS: 'OPS',
  SIP: 'SIP'
}

const BeaconMalfunctionPropertyName = {
  STAGE: 'STAGE',
  VESSEL_STATUS: 'VESSEL_STATUS'
}

const BeaconMalfunctionVesselStatus = {
  ACTIVITY_DETECTED: 'ACTIVITY_DETECTED',
  AT_PORT: 'AT_PORT',
  AT_SEA: 'AT_SEA',
  NEVER_EMITTED: 'NEVER_EMITTED',
  NO_NEWS: 'NO_NEWS'
}

const VesselStatusAtPort = styled(VesselStatusAtPortSVG)``
const VesselStatusAtSea = styled(VesselStatusAtSeaSVG)``
const VesselStatusNoNews = styled(VesselStatusNoNewsSVG)``
const VesselStatusNeverEmitted = styled(VesselStatusNeverEmittedSVG)``
const VesselStatusActivityDetected = styled(VesselStatusActivityDetectedSVG)``

const iconStyle = {
  height: 17,
  verticalAlign: 'sub'
}

const vesselStatuses: BeaconMalfunctionStatusValue[] = [
  {
    color: '#F4DEAF',
    icon: <VesselStatusAtPort style={iconStyle} />,
    label: 'Navire à quai',
    textColor: COLORS.charcoal,
    value: 'AT_PORT'
  },
  {
    color: '#9ED7D9',
    icon: <VesselStatusAtSea style={iconStyle} />,
    label: 'Navire en mer',
    textColor: COLORS.charcoal,
    value: 'AT_SEA'
  },
  {
    color: '#E6BC51',
    icon: <VesselStatusNoNews style={iconStyle} />,
    label: 'Sans nouvelles',
    textColor: COLORS.charcoal,
    value: 'NO_NEWS'
  },
  {
    color: COLORS.charcoal,
    icon: <VesselStatusNeverEmitted style={iconStyle} />,
    label: "N'a jamais émis",
    textColor: COLORS.white,
    value: 'NEVER_EMITTED'
  },
  {
    color: '#C41812',
    icon: <VesselStatusActivityDetected style={iconStyle} />,
    label: 'Activité détectée',
    textColor: COLORS.white,
    value: 'ACTIVITY_DETECTED'
  }
]

export enum EndOfBeaconMalfunctionReason {
  PERMANENT_INTERRUPTION_OF_SUPERVISION = 'PERMANENT_INTERRUPTION_OF_SUPERVISION',
  RESUMED_TRANSMISSION = 'RESUMED_TRANSMISSION',
  TEMPORARY_INTERRUPTION_OF_SUPERVISION = 'TEMPORARY_INTERRUPTION_OF_SUPERVISION'
}

const endOfBeaconMalfunctionReasonRecord: Record<EndOfBeaconMalfunctionReason, EnfOfBeaconMalfunctionStatusValue> = {
  PERMANENT_INTERRUPTION_OF_SUPERVISION: {
    color: COLORS.opal,
    label: 'Arrêt définitif du suivi',
    textColor: COLORS.gunMetal,
    value: 'PERMANENT_INTERRUPTION_OF_SUPERVISION'
  },
  RESUMED_TRANSMISSION: {
    color: COLORS.mediumSeaGreen,
    label: 'Reprise des émissions',
    textColor: COLORS.white,
    value: 'RESUMED_TRANSMISSION'
  },
  TEMPORARY_INTERRUPTION_OF_SUPERVISION: {
    color: COLORS.opal,
    label: 'Arrêt temporaire du suivi',
    textColor: COLORS.gunMetal,
    value: 'TEMPORARY_INTERRUPTION_OF_SUPERVISION'
  }
}

export enum BeaconMalfunctionsStage {
  ARCHIVED = 'ARCHIVED',
  CROSS_CHECK = 'CROSS_CHECK',
  END_OF_MALFUNCTION = 'END_OF_MALFUNCTION',
  FOUR_HOUR_REPORT = 'FOUR_HOUR_REPORT',
  INITIAL_ENCOUNTER = 'INITIAL_ENCOUNTER',
  RELAUNCH_REQUEST = 'RELAUNCH_REQUEST',
  RESUMED_TRANSMISSION = 'RESUMED_TRANSMISSION',
  TARGETING_VESSEL = 'TARGETING_VESSEL'
}

/* eslint-disable sort-keys-fix/sort-keys-fix */
/**
 * Sort keys are disabled as keys order dictates Kanban columns ordering
 */
const beaconMalfunctionsStageColumnRecord: Record<BeaconMalfunctionsStage, BeaconMalfunctionStageColumnValue> = {
  INITIAL_ENCOUNTER: {
    code: 'INITIAL_ENCOUNTER',
    description: "Obtenir une réponse des navires qui ont cessé d'émettre.",
    isColumn: true,
    title: 'Premier contact'
  },
  FOUR_HOUR_REPORT: {
    code: 'FOUR_HOUR_REPORT',
    description: "Suivre les navires qui font leurs 4h report ou les relancer s'ils l'ont cessé.",
    isColumn: true,
    title: '4h report'
  },
  RELAUNCH_REQUEST: {
    code: 'RELAUNCH_REQUEST',
    description:
      "Relancer les navires qui sont à quai (ou supposés à quai) et qui n'ont pas encore repris leurs émissions.",
    isColumn: true,
    title: 'Relance pour reprise'
  },
  TARGETING_VESSEL: {
    code: 'TARGETING_VESSEL',
    description:
      "Mobiliser les unités sur les navires dont on n'a pas de nouvelles et/ou qui sont actifs en mer sans VMS.",
    isColumn: true,
    title: 'Ciblage du navire'
  },
  CROSS_CHECK: {
    code: 'CROSS_CHECK',
    description:
      "Mobiliser les unités sur les navires dont on n'a pas de nouvelles et/ou qui sont actifs en mer sans VMS.",
    isColumn: true,
    title: 'Contrôle croisé'
  },
  END_OF_MALFUNCTION: {
    code: 'END_OF_MALFUNCTION',
    description:
      "Envoyer un message de reprise aux unités dont les émissions ont repris et archiver les avaries qu'on ne suit plus.",
    isColumn: true,
    title: "Fin de l'avarie"
  },
  ARCHIVED: {
    code: 'ARCHIVED',
    description: 'Avaries clôturées.\n NB : Seules les 30 dernières avaries restent dans le kanban.',
    isColumn: true,
    title: 'Archivage'
  },
  /** Old stages - for backward compatibility * */
  RESUMED_TRANSMISSION: {
    code: 'RESUMED_TRANSMISSION',
    isColumn: false,
    title: 'Reprise des émissions'
  }
}
/* eslint-enable sort-keys-fix/sort-keys-fix */

const beaconMalfunctionNotificationType = {
  END_OF_MALFUNCTION: {
    followUpMessage: "Notification de fin d'avarie",
    preposition: 'de la'
  },
  MALFUNCTION_AT_PORT_INITIAL_NOTIFICATION: {
    followUpMessage: "Notification initiale d'avarie à quai",
    preposition: 'de la'
  },
  MALFUNCTION_AT_PORT_REMINDER: {
    followUpMessage: 'Relance pour avarie à quai',
    preposition: "d'une"
  },
  MALFUNCTION_AT_SEA_INITIAL_NOTIFICATION: {
    followUpMessage: "Notification initiale d'avarie en mer",
    preposition: 'de la'
  },
  MALFUNCTION_AT_SEA_REMINDER: {
    followUpMessage: 'Relance pour avarie en mer',
    preposition: "d'une"
  }
}

const communicationMeans = {
  EMAIL: {
    addresseePreposition: 'à',
    denomination: 'email',
    value: 'EMAIL'
  },
  FAX: {
    addresseePreposition: 'au',
    denomination: 'fax',
    value: 'FAX'
  },
  SMS: {
    addresseePreposition: 'au',
    denomination: 'SMS',
    value: 'SMS'
  }
}

const beaconMalfunctionNotificationRecipientFunction = {
  FMC: {
    addressee: 'CNSP',
    value: 'FMC'
  },
  SATELLITE_OPERATOR: {
    addressee: 'Opérateur sat.',
    value: 'SATELLITE_OPERATOR'
  },
  VESSEL_CAPTAIN: {
    addressee: 'Capitaine',
    value: 'VESSEL_CAPTAIN'
  },
  VESSEL_OPERATOR: {
    addressee: 'Armateur',
    value: 'VESSEL_OPERATOR'
  }
}

export {
  UserType,
  BeaconMalfunctionPropertyName,
  BeaconMalfunctionVesselStatus,
  vesselStatuses,
  BeaconMalfunctionsTab,
  endOfBeaconMalfunctionReasonRecord,
  beaconMalfunctionsStageColumnRecord,
  beaconMalfunctionNotificationType,
  communicationMeans,
  beaconMalfunctionNotificationRecipientFunction
}
