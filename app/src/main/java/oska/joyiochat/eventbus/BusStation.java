package oska.joyiochat.eventbus;

import com.squareup.otto.Bus;

/**
 * Created by theoska on 3/19/17.
 */

public class BusStation {
    private static Bus bus = new Bus();

    public static Bus getBus(){
        return bus;
    }
}
