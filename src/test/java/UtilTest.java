import org.docheinstein.commons.internal.DocCommonsLogger;
import org.docheinstein.commons.utils.crypto.CryptoUtil;
import org.docheinstein.commons.utils.file.FileLinesReader;
import org.docheinstein.commons.utils.file.FileUtil;
import org.docheinstein.commons.utils.logger.DocLogger;
import org.docheinstein.commons.utils.time.TimeUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
//        L.debug("ms: " + TimeUtil.TimeStruct.fromString("00:00:00.1").toString());
//        L.debug("s: " + TimeUtil.TimeStruct.fromString("00:00:01.6").toSeconds());
//        L.debug(TimeUtil.TimeStruct.fromString("03:10:05.252").toString());
//        L.debug(TimeUtil.TimeStruct.fromString("03:10:05").toString());
//        L.debug("ms " + TimeUtil.TimeStruct.fromString("03:10:05").toMillis());
//        L.debug("s " + TimeUtil.TimeStruct.fromString("03:10:05.252").toSeconds());

//        ZipUtil.unzip(new File("/tmp/panel_bundle.zip"), new File("/tmp/panel_bundle/"));
//        ZipUtil.zip(new File("/tmp/t/panel_bundle"), new File("/tmp/t/panel_bundle_copy.zip"));
//        ZipUtil.unzip(UtilTest.class.getResourceAsStream("panel_bundle.zip"), new File("/tmp/panel_bundle_from_res/"));

//        System.out.println(TimeUtil.millisToTime(11405252));
//        System.out.println(TimeUtil.secondsToTime(11405));
//        if (!FileUtil.move(
//            "/home/stefano/Develop/Java/NexiController/NexiController.jar",
//            "/home/stefano/Develop/Java/NexiController/exec/old_versions/nexi-controller-0.9.4-dev-2.jar")) {
//            System.err.println("mv failed!!!");
//        } else {
//            System.out.println("Everything ok!");
//        }

//        initLogger();
//
//        final String URL = "https://1fhjluj.oloadcdn.net/dl/l/gs-ViQTIgZy4jH9D/9edEhCCpdSs/BlackClover_Ep_52_SUB_ITA.mp4";
//        final String OUTPUT = "/tmp/video_test.mp4";
//
//        L.debug("Going to download " + "https://1fiag0f.oloadcdn.net/dl/l");
//        new HttpDownloader().download(
//            URL,
//            OUTPUT,
//            new HttpDownloader.DownloadObserver() {
//                @Override
//                public void onProgress(long downloadedBytes) {
//                    L.debug("Downloaded " + downloadedBytes);
//                }
//            }, 1000 * 1000
//        );


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
//        DocLogger.enableLoggingOnFiles(
//            new File("/tmp/"),
//            () -> TimeUtil.dateToString("yyyy_MM_dd") + ".log2",
//            false
//        );
//        DocLogger.enableLogLevel(DocLogger.LogLevel.Verbose, true, false);
//        DocLogger L = DocLogger.createForTag("{TestLogger}");
//
//        L.debug("This is AAAA debug test");
//
//        DocLogger.createForClass(EnumConstantNotPresentException.class).debug("Ciao");
//        DocLogger.createForClass(HTTPBinding.class).debug("Ciao2");

//        String key = "Bar12345Bar12345"; // 128 bit key
//        String iv = "RandomInitVector"; // 16 bytes IV
//        String plain = "This is a message";
//
//        String enc = CryptoUtil.AES.encryptToBase64(plain, iv, key);
//        String dec = CryptoUtil.AES.decryptFromBase64(enc, iv, key);
//
//        System.out.println("PLAIN: " + plain);
//        System.out.println("ENC: " + enc);
//        System.out.println("DEC: " + dec);

//        System.out.println("DEC: " + CryptoUtil.MD5.encode("Cane"));

//        KeyPair keys = newKeyPair();

//        System.out.println("PUBLIC");
//        System.out.println(CryptoUtil.Base64.encode(keys.getPublic().getEncoded()));
//
//        System.out.println("PRIVATE");
//        System.out.println(CryptoUtil.Base64.encode(keys.getPrivate().getEncoded()));
////

//        String data = "This is a very long message; but could be even longer!";
//        String enc = CryptoUtil.RSA.encryptToBase64(data, pub);
//        String dec = CryptoUtil.RSA.decryptFromBase64(enc, priv);
//
//        System.out.println("enc l: " + enc.length());
//        System.out.println("dec l: " + dec.length());
//        System.out.println("enc : " + enc);
//        System.out.println("dec : " + dec);
//
//        if (data.equals(dec))
//            System.out.println("Works!");


//        String datadata = new String(data.getBytes());
//
//        String encryptedString = CryptoUtil.RSA.encryptString(data, keys.getPublic());
//        byte[] encryptStringToBytes = encryptedString.getBytes();
//
//        byte[] encryptedBytes = CryptoUtil.RSA.encrypt(data, keys.getPublic());
//        String encryptedBytesToString = new String(encryptedBytes);
//
//        System.out.println("S encryptedString length " + encryptedString.length());
//        System.out.println("B encryptStringToBytes length " + encryptStringToBytes.length);
//        System.out.println("B encryptedBytes length " + encryptedBytes.length);
//        System.out.println("S encryptedBytesToString length " + encryptedBytesToString.length());
//        System.out.println("S data " + data.length());
//        System.out.println("S datadata " + datadata.length());
//
//        System.out.println("S encryptedString " + encryptedString);
//        System.out.println("S encryptedBytesToString " + encryptedBytesToString);




//        System.out.println("Encrypted data length " + encryptedMessage.length());
//        System.out.println("Encrypted64 data length " + encryptedMessageB64.length());
//        System.out.println("Encrypted bytes length " + CryptoUtil.RSA.encrypt(data, keys.getPublic()).length);
//
//        String decryptedMessage = CryptoUtil.RSA.decryptString(encryptedMessage, keys.getPrivate());
//        System.out.println("Decrypted data length " + decryptedMessage.length());
//
//        if (!data.equals(decryptedMessage))
//                throw new RuntimeException("RSA doesn't work");
//
//        System.out.println("Everything ok!");


//        System.out.println(CryptoUtil.RSA.encryptToBase64("pippo", PUBK));
//        System.out.println(CryptoUtil.RSA.encryptToBase64("pippo", PUBK));

//        StringBuilder releasedTo = new StringBuilder();
//
//        FileUtil.readFileLineByLine("/tmp/lic", line -> {
//            if (line.startsWith("Rilasciata")) {
//                releasedTo.append(line);
//            }
//            return true;
//        });
//
//        System.out.println(releasedTo);
        System.out.println(TimeUtil.dateToString(TimeUtil.Patterns.DATE_SLASH, LocalDate.now()));
    }
}
