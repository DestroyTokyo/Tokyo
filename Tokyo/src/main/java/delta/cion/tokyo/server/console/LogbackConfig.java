package delta.cion.tokyo.server.console;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public final class LogbackConfig extends ForegroundCompositeConverterBase<ILoggingEvent> {

	@Override
	protected String getForegroundColorCode(ILoggingEvent event) {
		return switch (event.getLevel().toInt()) {
			case Level.DEBUG_INT 	-> ANSIConstants.MAGENTA_FG;
			case Level.INFO_INT 	-> ANSIConstants.GREEN_FG;
			case Level.TRACE_INT 	-> "90";
			case Level.WARN_INT 	-> ANSIConstants.YELLOW_FG;
			case Level.ERROR_INT 	-> ANSIConstants.RED_FG;
			default -> ANSIConstants.DEFAULT_FG;
		};
	}

	public static void enableDebugLogs(boolean status) {
		Logger LOGGER = (Logger) LoggerFactory.getLogger("delta.cion");
		if (status) LOGGER.setLevel(Level.DEBUG);
		else LOGGER.setLevel(Level.INFO);
	}

}
