package cc.powind.huron.rectifier;

public interface TopicMappings <T> {

    String[] topics();

    String topic(T t);

    Class<T> clazz(String topic);
}
