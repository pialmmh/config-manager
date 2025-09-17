package freeswitch.config.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
public class ConfigUpdateNotifier {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AdminClient adminClient;
    private static final String TOPIC = "config_event_loader";
    private static final String MESSAGE = "config reloaded";

    public ConfigUpdateNotifier(KafkaTemplate<String, String> kafkaTemplate, AdminClient adminClient) {
        this.kafkaTemplate = kafkaTemplate;
        this.adminClient = adminClient;
    }

    public void publish() {
        try {
            Set<String> existingTopics = adminClient.listTopics().names().get();

            if (!existingTopics.contains(TOPIC)) {
                NewTopic newTopic = new NewTopic(TOPIC, 3, (short) 1);
                adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
                System.out.println("Topic created: " + TOPIC);
            }

            System.out.println(MESSAGE);
            kafkaTemplate.send(TOPIC, MESSAGE);

        } catch (ExecutionException e) {
            if (e.getCause() instanceof TopicExistsException) {
                kafkaTemplate.send(TOPIC, MESSAGE);
            } else {
                System.err.println("Failed to check or create topic");
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while waiting for topic creation.");
        }
    }
}