import org.docheinstein.commons.hierarchy.DNode;
import org.docheinstein.commons.hierarchy.FNode;
import org.docheinstein.commons.hierarchy.Hierarchy;
import org.docheinstein.commons.internal.DocCommonsLogger;
import org.docheinstein.commons.logger.DocLogger;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class UtilTest {

    private static DocLogger L;

    private static void initLogger() {
        L =  DocLogger.createForTag("{DocUtilTest}");

        DocCommonsLogger.enable(true);
        DocLogger.enableLogLevel(DocLogger.LogLevel.Verbose, true, false);

        DocCommonsLogger.addListener(L::debug);
    }

    public static void main(String args[]) throws InterruptedException, IOException, NoSuchAlgorithmException {
        initLogger();

        Hierarchy h = Hierarchy.create(
            "/tmp",
            DNode.create(
                "_.Test1",
                FNode.create("first_content.txt", "The content of the first"),
                FNode.create("second_optional.txt", false)
            ),
            DNode.create(
                "_.Test2",
                DNode.create(
                    "Inner",
                    FNode.create("third_empty.txt")
                )
            )
        );

        h.ensureExistence();

        System.out.println(h.toTree());
    }
}
