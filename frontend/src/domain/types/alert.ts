export enum PendingAlertValueType {
  FRENCH_EEZ_FISHING_ALERT = 'FRENCH_EEZ_FISHING_ALERT',
  MISSING_FAR_ALERT = 'MISSING_FAR_ALERT',
  THREE_MILES_TRAWLING_ALERT = 'THREE_MILES_TRAWLING_ALERT',
  TWELVE_MILES_FISHING_ALERT = 'TWELVE_MILES_FISHING_ALERT'
}

export type PendingAlert = {
  creationDate: string
  externalReferenceNumber: string
  id: string
  infraction: {
    infraction: string
    infractionCategory: string
    natinfCode: string
    regulation: string
  }
  internalReferenceNumber: string
  ircs: string
  tripNumber: string
  value: PendingAlertValue
  vesselIdentifier: string
  vesselName: string
}

export type PendingAlertValue = {
  flagState: string
  natinfCode: string | null
  riskFactor: number
  seaFront: string
  speed: number
  type: PendingAlertValueType
}

export type LEGACY_PendingAlert = PendingAlert & {
  isValidated: boolean
}

export type SilencedAlert = {
  externalReferenceNumber: string
  id: string
  internalReferenceNumber: string
  ircs: string
  isReactivated: boolean | null
  silencedAfterDate: Date
  silencedBeforeDate: Date
  value: PendingAlertValue
  vesselIdentifier: string
  vesselName: string
}

export type LEGACY_SilencedAlert = SilencedAlert & {
  silencedPeriod?: SilencedAlertPeriodRequest
}

export type SilencedAlertPeriodRequest = {
  afterDateTime: Date | null
  beforeDateTime: Date | null
  silencedAlertPeriod: string | null
}

export type SilenceAlertQueueItem = {
  pendingAlertId: string
  silencedAlertPeriodRequest: SilencedAlertPeriodRequest
}
