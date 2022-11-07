import ky from 'ky'

import { ApiError } from '../libs/ApiError'

import type {
  Reporting,
  UpdateReporting,
  InfractionSuspicionReporting,
  PendingAlertReporting
} from '../domain/types/reporting'

export const ARCHIVE_REPORTING_ERROR_MESSAGE = "Nous n'avons pas pu archiver le signalement"
export const ARCHIVE_REPORTINGS_ERROR_MESSAGE = "Nous n'avons pas pu archiver les signalements"
export const DELETE_REPORTING_ERROR_MESSAGE = "Nous n'avons pas pu supprimer le signalement"
export const DELETE_REPORTINGS_ERROR_MESSAGE = "Nous n'avons pas pu supprimer les signalements"
export const UPDATE_REPORTING_ERROR_MESSAGE = "Nous n'avons pas pu éditer le signalement"
export const ADD_REPORTING_ERROR_MESSAGE = "Nous n'avons pas pu créer le signalement"
export const GET_REPORTINGS_ERROR_MESSAGE = "Nous n'avons pas pu créer les signalements"

/**
 * Archive a reporting
 *
 * @throws {@link ApiError}
 */
async function archiveReportingFromAPI(id: number) {
  try {
    await ky.put(`/bff/v1/reportings/${id}/archive`)
  } catch (err) {
    throw new ApiError(ARCHIVE_REPORTING_ERROR_MESSAGE, err)
  }
}

/**
 * Archive multiple reportings
 *
 * @throws {@link ApiError}
 */
async function archiveReportingsFromAPI(ids: number[]) {
  try {
    await ky.put(`/bff/v1/reportings/archive`, {
      headers: {
        Accept: 'application/json, text/plain',
        'Content-Type': 'application/json;charset=UTF-8'
      },
      json: JSON.stringify(ids)
    })
  } catch (err) {
    throw new ApiError(ARCHIVE_REPORTINGS_ERROR_MESSAGE, err)
  }
}

/**
 * Delete a reporting
 *
 * @throws {@link ApiError}
 */
async function deleteReportingFromAPI(id: number) {
  try {
    await ky.put(`/bff/v1/reportings/${id}/delete`)
  } catch (err) {
    throw new ApiError(DELETE_REPORTING_ERROR_MESSAGE, err)
  }
}

/**
 * Delete multiple reportings
 *
 * @throws {@link ApiError}
 */
async function deleteReportingsFromAPI(ids: number[]) {
  try {
    await ky.put(`/bff/v1/reportings/delete`, {
      headers: {
        Accept: 'application/json, text/plain',
        'Content-Type': 'application/json;charset=UTF-8'
      },
      json: JSON.stringify(ids)
    })
  } catch (err) {
    throw new ApiError(DELETE_REPORTINGS_ERROR_MESSAGE, err)
  }
}

/**
 * Add a reporting
 *
 * @throws {@link ApiError}
 */
async function addReportingFromAPI(newReporting: Reporting): Promise<Reporting> {
  try {
    return await ky
      .post(`/bff/v1/reportings`, {
        headers: {
          Accept: 'application/json, text/plain',
          'Content-Type': 'application/json;charset=UTF-8'
        },
        json: newReporting
      })
      .json<Reporting>()
  } catch (err) {
    throw new ApiError(ADD_REPORTING_ERROR_MESSAGE, err)
  }
}

/**
 * Update a reporting
 *
 * @throws {@link ApiError}
 */
async function updateReportingFromAPI(id: number, nextReporting: UpdateReporting): Promise<Reporting> {
  try {
    return await ky
      .put(`/bff/v1/reportings/${id}/update`, {
        headers: {
          Accept: 'application/json, text/plain',
          'Content-Type': 'application/json;charset=UTF-8'
        },
        json: nextReporting
      })
      .json<Reporting>()
  } catch (err) {
    throw new ApiError(UPDATE_REPORTING_ERROR_MESSAGE, err)
  }
}

/**
 * Get all current reportings
 *
 * @throws {@link ApiError}
 */
async function getAllCurrentReportingsFromAPI(): Promise<Array<InfractionSuspicionReporting | PendingAlertReporting>> {
  try {
    return await ky.get(`/bff/v1/reportings`).json<Array<InfractionSuspicionReporting | PendingAlertReporting>>()
  } catch (err) {
    throw new ApiError(GET_REPORTINGS_ERROR_MESSAGE, err)
  }
}

export {
  archiveReportingFromAPI,
  archiveReportingsFromAPI,
  deleteReportingFromAPI,
  deleteReportingsFromAPI,
  addReportingFromAPI,
  updateReportingFromAPI,
  getAllCurrentReportingsFromAPI
}
