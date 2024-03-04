package cc.powind.huron.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RealtimeError {

    private Realtime realtime;

    private List<Error> errors = new ArrayList<>();

    private String message;

    public RealtimeError(Realtime realtime, String message) {
        this.realtime = realtime;
        this.message = message;
    }

    public RealtimeError(Realtime realtime, List<Error> errors) {
        this.realtime = realtime;
        this.errors = errors;
    }

    public RealtimeError(Realtime realtime, List<Error> errors, String message) {
        this.realtime = realtime;
        this.errors = errors;
        this.message = message;
    }

    public Realtime getRealtime() {
        return realtime;
    }

    public void setRealtime(Realtime realtime) {
        this.realtime = realtime;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean errorIsEmpty() {
        return errors.isEmpty();
    }

    public List<String> getErrorDescription() {
        return errors.stream().map(Error::getDescription).collect(Collectors.toList());
    }

    public static class Error {

        private String field;

        private String description;

        public Error(String field, String description) {
            this.field = field;
            this.description = description;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
