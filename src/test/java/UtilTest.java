import org.docheinstein.commons.utils.file.FileUtil;
import org.docheinstein.commons.utils.http.HttpRequester;
import org.docheinstein.commons.utils.logger.DocLogger;
import org.docheinstein.commons.utils.time.TimeUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UtilTest {
    public static void main(String args[]) throws InterruptedException {

        // FileUtil.deleteRecursive("/tmp/_.Y");

//        String direct = HttpRequester
//            .head("https://openload.co/stream/cZwIjzPELAM~1535622523~109.168.0.0~Y-rcs13M")
//            .allowRedirect(false)
//            .send()
//            .getHeaderFields()
//            .get("Location").get(0);
//
//        System.out.println("\n\nDirect: " + direct);
//
//        printHeaderFields(HttpRequester
//            .head(direct)
//            .allowRedirect(false)
//            .initialized()
//            .userAgent("curl/7.52.1")
//            .accept("*/*")
//            .send()
//            .getHeaderFields()
//        );


//        String s = FileUtil.readFile("/tmp/empty");
//        if (s == null)
//            System.out.println("Got null");
//        else if (s.isEmpty())
//            System.out.println("Got empty");
//        else
//            System.out.println(s);

//        File dir = new File("/home/stefano/Develop/Java/AnimeDownloader/tmp/complete/");
//
//        File[] files = dir.listFiles();
//
//        Arrays.sort(files, File::compareTo);
//
//        FileUtil.mergeFiles(
//            new File("/home/stefano/Develop/Java/AnimeDownloader/tmp/complete/merge_test.ts"),
//            files
//        );

//        System.out.println(System.getProperty("os.name"));
        DocLogger.enableLoggingOnFiles(
            new File("/tmp/"),
            () -> TimeUtil.dateToString("yyyy_MM_dd") + ".log",
            true
        );
        DocLogger.enableLogLevel(DocLogger.LogLevel.Verbose, true, false);
        DocLogger L = DocLogger.createForTag("{TestLogger}");

        L.debug("This is a debug test");

        L.verbose("This is a verbose test");

    }

    private static void printHeaderFields(Map<String, List<String>> headerFields) {
        headerFields.forEach((k, vs) -> {
            System.out.println("Key = " + k);
            vs.forEach(v -> System.out.println("--Value = " + v));
        });
    }
}
