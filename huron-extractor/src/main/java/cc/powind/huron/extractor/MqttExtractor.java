package cc.powind.huron.extractor;

import cc.powind.huron.core.model.Realtime;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttExtractor<T, K extends Realtime> {

    private String broker;

    private String clientId;

    private String username;

    private String password;

    private String topic;

    public void init() {

        try {
            MqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());

            // MQTT connection option
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName(username);
            connOpts.setPassword(password.toCharArray());
            // retain session
            connOpts.setCleanSession(true);

            client.setCallback(new OnMessageCallback());

            client.connect(connOpts);

            client.subscribe(topic);

        } catch (MqttException e) {

        }
    }

    public static class OnMessageCallback implements MqttCallback {

        public void connectionLost(Throwable cause) {
            // After the connection is lost, it usually reconnects here
            System.out.println("disconnect，you can reconnect");
        }

        public void messageArrived(String topic, MqttMessage message) throws Exception {
            // The messages obtained after subscribe will be executed here
            System.out.println("Received message topic:" + topic);
            System.out.println("Received message Qos:" + message.getQos());
            System.out.println("Received message content:" + new String(message.getPayload()));
        }

        public void deliveryComplete(IMqttDeliveryToken token) {
            System.out.println("deliveryComplete---------" + token.isComplete());
        }
    }
}
