import org.docheinstein.commons.internal.DocCommonsLogger;
import org.docheinstein.commons.logger.DocLogger;

public class UtilTest {

    private static DocLogger L;

    private static void initLogger() {
        L =  DocLogger.createForTag("{DocUtilTest}");

        DocCommonsLogger.enable(true);
        DocLogger.enableLogLevel(DocLogger.LogLevel.Verbose, true, false);

        DocCommonsLogger.addListener(L::debug);
    }

    public static void main(String args[]) {
        initLogger();
    }
}
