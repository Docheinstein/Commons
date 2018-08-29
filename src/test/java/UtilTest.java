import org.docheinstein.commons.utils.http.HttpRequester;
import org.docheinstein.commons.utils.http.HttpUtil;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class UtilTest {
    public static void main(String args[]) throws InterruptedException {

        // FileUtil.deleteRecursive("/tmp/_.Y");

        String direct = HttpUtil
            .head("https://openload.co/stream/cZwIjzPELAM~1535622523~109.168.0.0~Y-rcs13M")
            .allowRedirect(false)
            .send()
            .getHeaderFields()
            .get("Location").get(0);

        System.out.println("\n\nDirect: " + direct);

        printHeaderFields(HttpUtil
            .head(direct)
            .allowRedirect(false)
            .initialized()
            .userAgent("curl/7.52.1")
            .accept("*/*")
            .send()
            .getHeaderFields()
        );
    }

    private static void printHeaderFields(Map<String, List<String>> headerFields) {
        headerFields.forEach((k, vs) -> {
            System.out.println("Key = " + k);
            vs.forEach(v -> System.out.println("--Value = " + v));
        });
    }
}
