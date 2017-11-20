package bizsocket.rx2;

import bizsocket.base.Query;
import bizsocket.base.Request;
import bizsocket.base.Tag;
import io.reactivex.Observable;

/**
 * Created by tong on 16/10/6.
 */
public interface TestService {
    @Request(cmd = 1)
    Observable<String> login(@Tag Object tag
            , @Query("username") String username,
                             @Query("password") String password);
}
