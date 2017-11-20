package bizsocket.base;

import junit.framework.TestCase;

import okio.ByteString;
import org.json.JSONObject;
import org.junit.Test;
import rx.Observable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tong on 16/10/8.
 */
public class JSONRequestConverterTest extends TestCase {
    public interface TestService1 {
        @Request(cmd = 1)
        Observable<String> method1(@Tag Object tag
                ,@Query("username") String username,
                                   @Query("password") String password);

        @Request(cmd = 1)
        Observable<String> method2(@Tag Object tag
                ,@Query("username") String username,
                                   @Query("password") String password,@QueryMap Map map);

        @Request(cmd = 1)
        Observable<String> method3(String username,String password);
    }

    private Class<?> serviceClazz = TestService1.class;

    @Test
    public void test1() throws Exception {
        JSONRequestConverter converter = new JSONRequestConverter();
        Method method = serviceClazz.getMethod("method1",Object.class,String.class,String.class);

        String username = "username1";
        String password = "password1";
        ByteString byteString = converter.converter(method,this,username,password);
        System.out.println(byteString.utf8());
        JSONObject obj = new JSONObject(byteString.utf8());

        assertEquals(username,obj.optString("username"));
        assertEquals(password,obj.optString("password"));
    }

    @Test
    public void test2() throws Exception {
        JSONRequestConverter converter = new JSONRequestConverter();
        Method method = serviceClazz.getMethod("method2",Object.class,String.class,String.class,Map.class);

        Map<String,String> map = new HashMap<String, String>();
        map.put("username","username_map");
        map.put("mapmap","true");

        ByteString byteString = converter.converter(method,this,"username","password",map);
        System.out.println(byteString.utf8());
        JSONObject obj = new JSONObject(byteString.utf8());

        assertEquals("username_map",obj.optString("username"));
        assertEquals("true",obj.optString("mapmap"));
    }

    @Test
    public void test3() throws Exception {
        JSONRequestConverter converter = new JSONRequestConverter();
        Method method = serviceClazz.getMethod("method3",String.class,String.class);

        ByteString byteString = converter.converter(method,"username","password");
        System.out.println(byteString.utf8());

        JSONObject obj = new JSONObject(byteString.utf8());
        assertEquals(obj.length(),0);
    }
}
