package bizsocket.core;

import bizsocket.core.internal.RequestContextQueue;
import junit.framework.TestCase;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by tong on 16/10/6.
 */
public class RequestContextQueueTest extends TestCase {
    final RequestContextQueue requestContextQueue = new RequestContextQueue();
    boolean isInvokeOnAddToQuote;
    boolean isInvokeOnRemoveFromQuoue;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        requestContextQueue.clear();
        isInvokeOnAddToQuote = false;
        isInvokeOnRemoveFromQuoue = false;
    }

    @Test
    public void testConcurrent() {
        for (int i = 0; i < 40; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 20; j++) {
                        int i = new Random().nextInt(10);
                        if (i == 1) {
                            if (requestContextQueue.size() > 0) {
                                int position = new Random().nextInt(requestContextQueue.size());
                                if (position < 0) {
                                    position = 0;
                                }
                                if (position >= requestContextQueue.size()) {
                                    position = requestContextQueue.size() - 1;
                                }
                                requestContextQueue.remove(position);
                            }
                        }
                        else if (i == 2) {
                            for (RequestContext context : requestContextQueue) {
                                System.out.println(context);
                            }
                        }
                        else if (i == 3) {
                            requestContextQueue.clear();
                        }
                        else {
                            requestContextQueue.add(new RequestContext(null,null,null));
                        }
                    }
                }
            }).start();
        }
    }

    @Test
    public void testAdd() throws Exception {
        final RequestContext requestContext = new RequestContext(null,null,null){
            @Override
            public void onAddToQueue() {
                isInvokeOnAddToQuote = true;
                super.onAddToQueue();
            }
        };

        requestContextQueue.add(requestContext);
        assertEquals(isInvokeOnAddToQuote,true);
        requestContextQueue.clear();

        requestContextQueue.add(0,requestContext);
        assertEquals(isInvokeOnAddToQuote,true);
        requestContextQueue.clear();

        requestContextQueue.addAll(new ArrayList<RequestContext>(){{add(requestContext);}});
        assertEquals(isInvokeOnAddToQuote, true);
    }

    @Test
    public void testRemove() throws Exception {
        final RequestContext requestContext = new RequestContext(null,null,null){
            @Override
            public void onRemoveFromQueue() {
                isInvokeOnRemoveFromQuoue = true;
                super.onRemoveFromQueue();
            }
        };

        requestContextQueue.add(requestContext);
        assertEquals(isInvokeOnRemoveFromQuoue,false);
        requestContextQueue.remove(requestContext);
        assertEquals(isInvokeOnRemoveFromQuoue,true);

        isInvokeOnRemoveFromQuoue = false;
        requestContextQueue.add(requestContext);
        assertEquals(isInvokeOnRemoveFromQuoue,false);
        requestContextQueue.remove(0);
        assertEquals(isInvokeOnRemoveFromQuoue,true);

        isInvokeOnRemoveFromQuoue = false;
        requestContextQueue.add(requestContext);
        assertEquals(isInvokeOnRemoveFromQuoue,false);
        requestContextQueue.removeAll(new ArrayList<Object>(){{add(requestContext);}});
        assertEquals(isInvokeOnRemoveFromQuoue, true);

        isInvokeOnRemoveFromQuoue = false;
        requestContextQueue.add(requestContext);
        assertEquals(isInvokeOnRemoveFromQuoue,false);
        requestContextQueue.clear();
        assertEquals(isInvokeOnRemoveFromQuoue,true);
    }
}
