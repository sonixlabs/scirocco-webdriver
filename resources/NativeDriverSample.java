package test.sample;

import junit.framework.TestCase;

import com.android.test.scirocco2.jar.NDUtil;
import com.google.android.testing.nativedriver.client.AndroidNativeDriver;

public class NativeDriverSample extends TestCase {
    private AndroidNativeDriver driver;
    
    private final int SLEEP = 800;
    
    @Override
    protected void setUp() {
        driver = NDUtil.getDriver();
    }

    @Override
    protected void tearDown() {
        driver.quit();
    }

    public void testMethod() throws InterruptedException {
        driver.startActivity("target_app_package.{your test activity}");
        Thread.sleep(SLEEP);
        NDUtil.getScreenShot(driver);
    }
}

