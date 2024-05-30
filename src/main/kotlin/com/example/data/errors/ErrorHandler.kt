package com.example.data.errors

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ErrorHandler(private val errorMapper: ErrorMapper) {
        private val logger: Logger = LoggerFactory.getLogger(ErrorHandler::class.java)

        fun handleError(exception: Exception): Nothing? {
            val (errorCode, httpStatusCode) = errorMapper.parseException(exception)
            val errorMessage =
                "Exception: ${httpStatusCode?.value ?: ""} ${httpStatusCode?.description ?: exception.localizedMessage}"
            logger.logError(errorCode, errorMessage, exception)
            return null
        }

        private fun Logger.logError(code: ErrorCode, status: String? = null, exception: Exception? = null) {
            val errorMessage = status?.let { "${code.message} $it" } ?: code.message
            this.error(errorMessage, exception)
        }
}
