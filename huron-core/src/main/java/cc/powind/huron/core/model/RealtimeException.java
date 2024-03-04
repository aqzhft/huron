package cc.powind.huron.core.model;

/**
 *
 * Real-time exception base class
 *
 */
public class RealtimeException extends Exception {

    public RealtimeException() {}

    public RealtimeException(String message) {
        super(message);
    }
}
