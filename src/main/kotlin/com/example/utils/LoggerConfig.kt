import com.example.utils.ErrorCode
import org.slf4j.LoggerFactory

class LoggerConfig {
    companion object {
        private val logger = LoggerFactory.getLogger(LoggerConfig::class.java)

        fun logError(code: ErrorCode, status: String? = null, e: Exception? = null) {
            val errorMessage = if (status != null) "${code.message} $status" else code.message
            if (e != null) {
                logger.error(errorMessage, e)
            } else {
                logger.error(errorMessage)
            }
        }

        fun logInfo(message: String) {
            logger.info(message)
        }
    }
}