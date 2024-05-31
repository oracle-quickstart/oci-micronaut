package events.model;

import io.micronaut.serde.annotation.Serdeable;

/**
 * Defines whether events were received.
 */
@Serdeable
public record EventsReceived(boolean success ,int events){
}
// public class EventsReceived {

//     private final boolean success;
//     private final int events;

//     public EventsReceived(boolean success, int events) {
//         this.success = success;
//         this.events = events;
//     }

//     /**
//      * True if the events were successfully received
//      */
//     public boolean isSuccess() {
//         return success;
//     }

//     /**
//      * The number of successfully received events
//      */
//     public int getEvents() {
//         return events;
//     }
// }
