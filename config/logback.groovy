import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.ThresholdFilter

import static ch.qos.logback.classic.Level.*

def APP_NAME = "road-modeller"
def PATH_LOG = "logs"
def FILE_PATTERN = "%d{yyyy-MM-dd}_%i.log.gz"
def MAX_FILE_SIZE = "100MB"
def ENCODER_PATTERN = "%date{dd/MM/yy HH:mm:ss} %level [%thread] %logger{10} [%file:%line] %X{GUID} %msg%n"
int MAX_HISTORY = 1

appender("WARN_ERROR_FILE", RollingFileAppender) {
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${PATH_LOG}/${APP_NAME}-warn-${FILE_PATTERN}"
        timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP) {
            maxFileSize = MAX_FILE_SIZE
        }
        maxHistory = MAX_HISTORY
    }
    encoder(PatternLayoutEncoder) {
        pattern = "${ENCODER_PATTERN}"
    }
    filter(ThresholdFilter) {
        level = WARN
    }
}
appender("DEBUG_INFO_FILE", RollingFileAppender) {
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${PATH_LOG}/${APP_NAME}-debug-${FILE_PATTERN}"
        timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP) {
            maxFileSize = MAX_FILE_SIZE
        }
        maxHistory = MAX_HISTORY
    }
    encoder(PatternLayoutEncoder) {
        pattern = "${ENCODER_PATTERN}"
    }
    filter(ThresholdFilter) {
        level = DEBUG
    }
}


appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "${ENCODER_PATTERN}"
    }
}

logger("com.drakezzz.roadmodeller.ModellerApplication", INFO, ["STDOUT"], false)
logger("com.drakezzz.roadmodeller", DEBUG, ["DEBUG_INFO_FILE", "STDOUT"], false)
logger("com.drakezzz.roadmodeller.executor.impl.ModellerExecutor", DEBUG, ["DEBUG_INFO_FILE", "STDOUT"], false)
logger("com.drakezzz.roadmodeller.model.processor.impl", DEBUG, ["DEBUG_INFO_FILE", "STDOUT"], false)
root(ERROR, ["STDOUT"])