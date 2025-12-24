package com.grind.statistics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

@Configuration
@EnableKafka
public class KafkaListenerContainerConfig {
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory,
            CommonErrorHandler errorHandler
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory);

        // 1) batch mode: один вызов listener на пачку из poll()
        factory.setBatchListener(true);

        // 2) ручной ack (commit offsets только после успешной записи в CH)
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        // 3) сколько потоков/consumer-ов в одной группе (<= числа партиций)
        factory.setConcurrency(1); // в dev лучше 1, в prod поднимешь

        // 4) poll timeout (как часто poll() возвращает управление)
        factory.getContainerProperties().setPollTimeout(1500);

        // 5) обработчик ошибок/ретраи
        factory.setCommonErrorHandler(errorHandler);

        // опционально: если хочешь, чтобы container стартовал сам
        factory.setAutoStartup(true);

        return factory;
    }

    /**
     * Ретраи с бэкоффом: если ClickHouse лег/таймаут, не коммитим offsets,
     * повторяем обработку batch.
     */
    @Bean
    public CommonErrorHandler errorHandler() {
        // 200ms, 400ms, 800ms, 1600ms, 3000ms (пример)
        var backOff = new ExponentialBackOffWithMaxRetries(5);
        backOff.setInitialInterval(200);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(3000);

        var handler = new DefaultErrorHandler(backOff);

        // ВАЖНО: по умолчанию handler делает seek/retry.
        // Для "проблемы данных" можно отправлять в DLT:
        // handler.addNotRetryableExceptions(DeserializationException.class, ...);

        return handler;
    }
}
