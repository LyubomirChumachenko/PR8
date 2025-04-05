package ru.example.JMS;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;

public class MessageBrowser {
    public static void main(String[] args) {
        try {
            // Создаем фабрику соединений
            ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            
            // Устанавливаем соединение
            Connection connection = factory.createConnection();
            connection.start();
            
            // Создаем сессию
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            // Список очередей, которые нужно проверить
            String[] queueNames = {
                "parfume.queue.getAll",
                "parfume.queue.getById",
                "parfume.queue.add",
                "parfume.queue.update",
                "parfume.queue.delete",
                "parfume.queue.searchByType",
                "parfume.queue.response",
                "parfume.queue.buy"
            };
            
            System.out.println("==== СООБЩЕНИЯ В БРОКЕРЕ ACTIVEMQ ====");
            int totalMessages = 0;
            
            // Проходим по всем очередям
            for (String queueName : queueNames) {
                Queue queue = session.createQueue(queueName);
                QueueBrowser browser = session.createBrowser(queue);
                
                @SuppressWarnings("unchecked")
                Enumeration<Message> enumeration = browser.getEnumeration();
                
                int queueMessageCount = 0;
                List<String> messageDetails = new ArrayList<>();
                
                // Проходим по всем сообщениям в текущей очереди
                while (enumeration.hasMoreElements()) {
                    Message message = enumeration.nextElement();
                    queueMessageCount++;
                    
                    StringBuilder messageInfo = new StringBuilder();
                    messageInfo.append("   Сообщение #").append(queueMessageCount)
                               .append(" [ID: ").append(message.getJMSMessageID()).append("]");
                    
                    // Извлекаем содержимое в зависимости от типа сообщения
                    if (message instanceof TextMessage) {
                        messageInfo.append(" Текст: ").append(((TextMessage) message).getText());
                    } else if (message instanceof ObjectMessage) {
                        Object obj = ((ObjectMessage) message).getObject();
                        messageInfo.append(" Объект: ").append(obj.getClass().getSimpleName())
                                  .append(" - ").append(obj.toString());
                    } else if (message instanceof MapMessage) {
                        messageInfo.append(" MapMessage");
                    } else if (message instanceof BytesMessage) {
                        messageInfo.append(" BytesMessage");
                    } else if (message instanceof StreamMessage) {
                        messageInfo.append(" StreamMessage");
                    } else {
                        messageInfo.append(" (тип: ").append(message.getClass().getSimpleName()).append(")");
                    }
                    
                    messageDetails.add(messageInfo.toString());
                }
                
                // Выводим информацию о сообщениях в очереди только если они есть
                if (queueMessageCount > 0) {
                    System.out.println("\nОчередь: " + queueName + " (" + queueMessageCount + " сообщений)");
                    for (String detail : messageDetails) {
                        System.out.println(detail);
                    }
                    totalMessages += queueMessageCount;
                }
                
                browser.close();
            }
            
            if (totalMessages == 0) {
                System.out.println("\nВ брокере нет сообщений.");
            } else {
                System.out.println("\nВсего сообщений в брокере: " + totalMessages);
            }
            
            // Закрываем ресурсы
            session.close();
            connection.close();
            
        } catch (Exception e) {
            System.out.println("Ошибка при доступе к брокеру сообщений:");
            e.printStackTrace();
        }
    }
}