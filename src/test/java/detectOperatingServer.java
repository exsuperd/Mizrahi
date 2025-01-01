
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class detectOperatingServer extends Main {

    final int[] blackList = {};

    @BeforeClass
    public void startWebSession() {
        initBrowser(url);
        login(userName, password);
    }

    @Test
    public void getDesiredServerID() throws InterruptedException {
        verdict(blackList);
    }
}
