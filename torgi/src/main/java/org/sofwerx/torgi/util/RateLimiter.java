package org.sofwerx.torgi.util;

public class RateLimiter {
    int limit;
    double available;
    long interval;

    long lastTimeStamp;

    public RateLimiter(int limit, long interval) {
        this.limit = limit;
        this.interval = interval;

        available = 0;
        lastTimeStamp = System.currentTimeMillis();
    }

   public synchronized boolean canAdd() {
        long now = System.currentTimeMillis();
        // more token are released since last request
        available += (now-lastTimeStamp)*1.0/interval*limit;
        if (available>limit)
            available = limit;

        if (available<1)
            return false;
        else {
            available--;
            lastTimeStamp = now;
            return true;
        }
    }
}
