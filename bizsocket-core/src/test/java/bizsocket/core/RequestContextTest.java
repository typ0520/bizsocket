package bizsocket.core;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by tong on 16/10/6.
 */
public class RequestContextTest extends TestCase {
    boolean timeout = false;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        timeout = false;
    }

    @Test
    public void testStartTimeoutTimer() throws Exception {
        RequestContext requestContext = new RequestContext(null,null,null);

        requestContext.setOnRequestTimeoutListener(new RequestContext.OnRequestTimeoutListener() {
            @Override
            public void onRequestTimeout(RequestContext context) {
                timeout = true;
            }
        });
        requestContext.setReadTimeout(2);
        assertEquals(timeout,false);
        requestContext.startTimeoutTimer();
        assertEquals(timeout, false);
        Thread.sleep(2100);
        assertEquals(timeout, true);
    }

    @Test
    public void testOnAddToQuote() throws Exception {
        RequestContext requestContext = new RequestContext(null,null,null);

        requestContext.setOnRequestTimeoutListener(new RequestContext.OnRequestTimeoutListener() {
            @Override
            public void onRequestTimeout(RequestContext context) {
                timeout = true;
            }
        });
        requestContext.setReadTimeout(2);
        assertEquals(timeout,false);
        requestContext.onAddToQueue();
        assertEquals(timeout, false);
        Thread.sleep(2100);
        assertEquals(timeout, true);
    }

    @Test
    public void testOnRemoveFromQuoue() throws Exception {
        RequestContext requestContext = new RequestContext(null,null,null);

        requestContext.setOnRequestTimeoutListener(new RequestContext.OnRequestTimeoutListener() {
            @Override
            public void onRequestTimeout(RequestContext context) {
                timeout = true;
            }
        });
        requestContext.setReadTimeout(2);
        assertEquals(timeout,false);
        requestContext.onAddToQueue();
        assertEquals(timeout, false);

        requestContext.onRemoveFromQueue();
        Thread.sleep(2100);
        assertEquals(timeout, false);
    }
}
