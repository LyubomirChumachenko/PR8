package ru.example.JMS;

import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import jakarta.jms.ConnectionFactory;

// Класс конфигурации для настройки JMS (Java Message Service)
@Configuration
public class JmsConfig {

    // Метод для создания фабрики слушателей JMS
    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory(
            ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer) {
        // Создаем экземпляр фабрики слушателей
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // Настраиваем фабрику с использованием предоставленной конфигурации и ConnectionFactory
        configurer.configure(factory, connectionFactory);
        // Возвращаем настроенную фабрику слушателей
        return factory;
    }

    // Метод для настройки конвертера сообщений для работы с JSON
    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        // Создаем экземпляр конвертера, который будет конвертировать сообщения в формат JSON и обратно
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        // Устанавливаем тип сообщения в текстовый (TEXT)
        converter.setTargetType(MessageType.TEXT);
        // Устанавливаем название свойства, которое будет хранить идентификатор типа сообщения
        converter.setTypeIdPropertyName("_type");
        // Возвращаем настроенный конвертер сообщений
        return converter;
    }
}