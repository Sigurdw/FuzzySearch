package Query;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: Sigurd Wien
 * Date: 17.04.13
 * Time: 17:20
 */
public final class QueryUpdateQueue {
    private final Lock lock = new ReentrantLock();
    private final Condition queryUpdated = lock.newCondition();
    private String queryString = null;

    public void updateQueryString(String queryString){
        try{
            lock.lock();
            this.queryString = queryString;
            queryUpdated.signal();
        }
        finally {
            lock.unlock();
        }
    }

    public String getQueryStringUpdate(){
        String update = null;
        try{
            lock.lock();
            if(queryString != null){
                update = queryString;
                queryString = null;
            }
        }
        finally{
            lock.unlock();
        }

        return update;
    }

    public void awaitNextQueryStringUpdate() throws InterruptedException{
        try{
            lock.lock();
            if(queryString == null){
                queryUpdated.await();
            }
        }
        finally{
            lock.unlock();
        }
    }
}
