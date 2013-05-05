package Query;

import Config.SearchConfig;

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
    private SearchConfig searchConfig = null;

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

    public void updateSearchConfig(SearchConfig searchConfig){
        try{
            lock.lock();
            this.searchConfig = searchConfig;
        }
        finally {
            lock.unlock();
        }
    }

    public SearchConfig getSearchConfigUpdate(){
        SearchConfig config;
        try {
            lock.lock();
            config = searchConfig;
            searchConfig = null;
        }
        finally {
            lock.unlock();
        }

        return config;
    }
}
