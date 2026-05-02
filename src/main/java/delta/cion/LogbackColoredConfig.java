package delta.cion;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

public final class LogbackColoredConfig extends ForegroundCompositeConverterBase<ILoggingEvent> {
	@Override
	protected String getForegroundColorCode(ILoggingEvent event) {
		return switch (event.getLevel().toInt()) {
			case Level.INFO_INT -> ANSIConstants.GREEN_FG;
			case Level.TRACE_INT, Level.DEBUG_INT -> "90";
			case Level.WARN_INT -> ANSIConstants.YELLOW_FG;
			case Level.ERROR_INT -> ANSIConstants.RED_FG;
			default -> ANSIConstants.DEFAULT_FG;
		};
	}
}
