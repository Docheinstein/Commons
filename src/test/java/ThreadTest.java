import org.docheinstein.commons.thread.ThreadUtil;

public class ThreadTest {
    public static void main(String[] args) {
        ThreadUtil.startSafe(() -> {
            System.out.println("Going to throw exception");
            int x = 100 / 0;
        }, new ThreadUtil.ThreadFailObserver() {
            @Override
            public void onThreadException(Throwable throwable) {
                System.out.println("Catched exception!");
                throwable.printStackTrace();
            }
        });
    }
}
