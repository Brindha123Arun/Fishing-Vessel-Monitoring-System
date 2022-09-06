package fr.gouv.cnsp.monitorfish.infrastructure.api

import fr.gouv.cnsp.monitorfish.domain.exceptions.CouldNotUpdateControlObjectiveException
import fr.gouv.cnsp.monitorfish.domain.exceptions.CouldNotUpdateFleetSegmentException
import fr.gouv.cnsp.monitorfish.domain.exceptions.NAFMessageParsingException
import fr.gouv.cnsp.monitorfish.domain.exceptions.NoLogbookFishingTripFound
import fr.gouv.cnsp.monitorfish.infrastructure.api.outputs.ApiError
import fr.gouv.cnsp.monitorfish.infrastructure.api.outputs.MissingParameterApiError
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(HIGHEST_PRECEDENCE)
class ControllersExceptionHandler {
  private val logger: Logger = LoggerFactory.getLogger(ControllersExceptionHandler::class.java)

  @ResponseStatus(HttpStatus.OK)
  @ExceptionHandler(NAFMessageParsingException::class)
  fun handleNAFMessageParsingException(e: Exception): ApiError {
    logger.error(e.message, e.cause)
    return ApiError(e)
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NoLogbookFishingTripFound::class)
  fun handleNoLogbookLastDepartureDateFound(e: Exception): ApiError {
    logger.error(e.message, e.cause)
    return ApiError(e)
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(CouldNotUpdateControlObjectiveException::class)
  fun handleCouldNotUpdateControlObjectiveException(e: Exception): ApiError {
    logger.error(e.message, e.cause)
    return ApiError(CouldNotUpdateControlObjectiveException(e.message.toString(), e))
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalArgumentException::class)
  fun handleIllegalArgumentException(e: Exception): ApiError {
    logger.error(e.message, e.cause)
    return ApiError(IllegalArgumentException(e.message.toString(), e))
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(CouldNotUpdateFleetSegmentException::class)
  fun handleCouldNotUpdateFleetSegmentException(e: Exception): ApiError {
    logger.error(e.message, e.cause)
    return ApiError(CouldNotUpdateFleetSegmentException(e.message.toString(), e))
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MissingServletRequestParameterException::class)
  fun handleNoParameter(e: MissingServletRequestParameterException): MissingParameterApiError {
    logger.error(e.message, e.cause)
    return MissingParameterApiError("Parameter \"${e.parameterName}\" is missing.")
  }
}
