package ru.example.JMS;

import java.util.Date; // Импорт класса Date для работы с датами
import java.util.HashMap; // Импорт HashMap для создания коллекций пар ключ-значение
import java.util.Map; // Импорт интерфейса Map для работы с коллекциями

import org.springframework.beans.factory.annotation.Autowired; // Импорт аннотации для автоматического связывания зависимостей
import org.springframework.beans.factory.annotation.Value; // Импорт аннотации для извлечения значений из файла конфигурации 
import org.springframework.jms.core.JmsTemplate; // Импорт класса JmsTemplate для отправки сообщений в JMS
import org.springframework.stereotype.Component; // Импорт аннотации для определения класса как компонента Spring

import ru.example.Parfume; // Импорт класса Parfume из пакета

@Component // Обозначение данного класса как Spring-компонента
public class ParfumeJmsProducer {

    @Autowired // Автоматическое внедрение зависимости JmsTemplate
    private JmsTemplate jmsTemplate;

    // Извлечение имен очередей из файла конфигурации
    @Value("${parfume.queue.getAll}")
    private String getAllQueueName;

    @Value("${parfume.queue.getById}")
    private String getByIdQueueName;

    @Value("${parfume.queue.add}")
    private String addQueueName;

    @Value("${parfume.queue.update}")
    private String updateQueueName;

    @Value("${parfume.queue.delete}")
    private String deleteQueueName;

    @Value("${parfume.queue.searchByType}")
    private String searchByTypeQueueName;

    @Value("${parfume.queue.response}")
    private String responseQueueName;

    @Value("${parfume.queue.buy}")
    private String responseBuy;

    /**
     * Отправляет данные о покупке парфюма в JMS-очередь.
     * @param id ID парфюма
     * @param parfume полные данные о парфюме
     */
    public void purchaseParfume(Long id, Parfume parfume) {
        // Создание карты для хранения информации о покупке
        Map<String, Object> purchaseInfo = new HashMap<>();
        purchaseInfo.put("action", "PURCHASE"); // Указываем действие
        purchaseInfo.put("parfumeId", id); // Добавляем ID парфюма
        purchaseInfo.put("parfumeData", parfume); // Добавляем данные парфюма
        purchaseInfo.put("purchaseTime", new Date()); // Записываем текущее время покупки
        
        // Отправляем сообщение в очередь для покупок
        jmsTemplate.convertAndSend(responseBuy, purchaseInfo);
    }

    /**
     * Запрос всех парфюмов, отправляя сообщение в соответствующую очередь.
     */
    public void getAllParfumes() {
        jmsTemplate.convertAndSend(getAllQueueName, new ParfumeMessage());
    }

    /**
     * Запрос парфюма по его ID.
     * @param id ID парфюма, который нужно получить
     */
    public void getParfumeById(Long id) {
        ParfumeMessage message = new ParfumeMessage(id); // Создаем сообщение с ID
        jmsTemplate.convertAndSend(getByIdQueueName, message); // Отправляем сообщение в очередь
    }

    /**
     * Добавляет новый парфюм, отправляя его данные в очередь.
     * @param parfume объект Parfume, который нужно добавить
     */
    public void addParfume(Parfume parfume) {
        ParfumeMessage message = new ParfumeMessage(parfume); // Создаем сообщение с данными парфюма
        jmsTemplate.convertAndSend(addQueueName, message); // Отправляем сообщение в очередь добавления
    }

    /**
     * Обновляет данные о парфюме, отправляя сообщение в соответствующую очередь.
     * @param parfume объект Parfume с обновленными данными
     */
    public void updateParfume(Parfume parfume) {
        ParfumeMessage message = new ParfumeMessage(parfume); // Создаем сообщение с обновленными данными
        jmsTemplate.convertAndSend(updateQueueName, message); // Отправляем сообщение в очередь обновления
    }

    /**
     * Удаляет парфюм по его ID, отправляя сообщение в очередь на удаление.
     * @param id ID парфюма, который нужно удалить
     */
    public void deleteParfume(Long id) {
        ParfumeMessage message = new ParfumeMessage(id); // Создаем сообщение с ID парфюма
        jmsTemplate.convertAndSend(deleteQueueName, message); // Отправляем сообщение в очередь удаления
    }

    /**
     * Выполняет поиск парфюмов по типу, отправляя сообщение с типом поиска.
     * @param type тип парфюма для поиска
     */
    public void searchParfumesByType(String type) {
        ParfumeMessage message = new ParfumeMessage(); // Создаем сообщение
        message.setSearchType(type); // Устанавливаем тип поиска
        jmsTemplate.convertAndSend(searchByTypeQueueName, message); // Отправляем сообщение в очередь поиска
    }
}