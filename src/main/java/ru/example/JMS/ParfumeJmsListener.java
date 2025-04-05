package ru.example.JMS;

// Импортирование необходимых классов из Spring и других пакетов
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import ru.example.Parfume; // Импорт класса парфюма
import ru.example.ParfumeService; // Импорт сервиса, который управляет парфюмами

import java.util.List; // Импорт класса для работы со списками
import java.util.Map; // Импорт класса для работы с картами (ассоциативные массивы)

// Указывает, что этот класс является компонентом Spring и должен быть включён в контекст приложения
@Component
public class ParfumeJmsListener {

    // Автоматическая реализация зависимости ParfumeService
    @Autowired
    private ParfumeService parfumeService;

    // Обработчик запросов на получение всех парфюмов из очереди, адрес которой задаётся в конфигурации
    @JmsListener(destination = "${parfume.queue.getAll}")
    @SendTo("${parfume.queue.response}") // Отправлять ответ в заданную очередь
    public ParfumeResponse handleGetAllRequest(ParfumeMessage message) {
        try {
            // Получение списка всех парфюмов из сервиса
            List<Parfume> parfumes = parfumeService.getAllParfume();
            // Формирование успешного ответа с полученным списком парфюмов
            return new ParfumeResponse(true, "Успешно получен список парфюмов", parfumes);
        } catch (Exception e) {
            // Обработка ошибок и возврат ответа с сообщением об ошибке
            return new ParfumeResponse(false, "Ошибка при получении списка парфюмов: " + e.getMessage());
        }
    }

    // Обработчик запросов на получение парфюма по ID
    @JmsListener(destination = "${parfume.queue.getById}")
    @SendTo("${parfume.queue.response}")
    public ParfumeResponse handleGetByIdRequest(ParfumeMessage message) {
        try {
            // Получение парфюма по ID, переданному в сообщении
            Parfume parfume = parfumeService.getParfumeById(message.getId());
            // Проверка, найден ли парфюм
            if (parfume != null) {
                return new ParfumeResponse(true, "Парфюм успешно найден", parfume);
            } else {
                return new ParfumeResponse(false, "Парфюм с ID " + message.getId() + " не найден");
            }
        } catch (Exception e) {
            return new ParfumeResponse(false, "Ошибка при получении парфюма: " + e.getMessage());
        }
    }

    // Обработчик запросов на добавление нового парфюма
    @JmsListener(destination = "${parfume.queue.add}")
    @SendTo("${parfume.queue.response}")
    public ParfumeResponse handleAddRequest(ParfumeMessage message) {
        try {
            // Сохранение нового парфюма в базе данных
            Parfume savedParfume = parfumeService.saveParfume(message.getParfume());
            return new ParfumeResponse(true, "Парфюм успешно добавлен", savedParfume);
        } catch (Exception e) {
            return new ParfumeResponse(false, "Ошибка при добавлении парфюма: " + e.getMessage());
        }
    }

    // Обработчик запросов на обновление существующего парфюма
    @JmsListener(destination = "${parfume.queue.update}")
    @SendTo("${parfume.queue.response}")
    public ParfumeResponse handleUpdateRequest(ParfumeMessage message) {
        try {
            // Получение объекта парфюма из сообщения
            Parfume parfume = message.getParfume();
            // Обновление парфюма и возврат ответом обновленного объекта
            Parfume updatedParfume = parfumeService.updateParfume(parfume.getId(), parfume);
            return new ParfumeResponse(true, "Парфюм успешно обновлен", updatedParfume);
        } catch (IllegalArgumentException e) {
            return new ParfumeResponse(false, "Парфюм с указанным ID не найден: " + e.getMessage());
        } catch (Exception e) {
            return new ParfumeResponse(false, "Ошибка при обновлении парфюма: " + e.getMessage());
        }
    }

    // Обработчик запросов на удаление парфюма по ID
    @JmsListener(destination = "${parfume.queue.delete}")
    @SendTo("${parfume.queue.response}")
    public ParfumeResponse handleDeleteRequest(ParfumeMessage message) {
        try {
            Long id = message.getId();
            // Удаление парфюма и возврат результата удаления
            boolean deleted = parfumeService.deleteParfume(id);
            if (deleted) {
                return new ParfumeResponse(true, "Парфюм успешно удален");
            } else {
                return new ParfumeResponse(false, "Парфюм с ID " + id + " не найден");
            }
        } catch (Exception e) {
            return new ParfumeResponse(false, "Ошибка при удалении парфюма: " + e.getMessage());
        }
    }

    // Обработчик запросов на поиск парфюма по типу
    @JmsListener(destination = "${parfume.queue.searchByType}")
    @SendTo("${parfume.queue.response}")
    public ParfumeResponse handleSearchByTypeRequest(ParfumeMessage message) {
        try {
            String type = message.getSearchType();
            // Поиск парфюмов по типу в сервисе
            List<Parfume> parfumes = parfumeService.searchByType(type);
            return new ParfumeResponse(true, "Парфюмы успешно найдены по типу: " + type, parfumes);
        } catch (Exception e) {
            return new ParfumeResponse(false, "Ошибка при поиске парфюмов по типу: " + e.getMessage());
        }
    }

    /**
     * Обрабатывает запрос на покупку парфюма.
     * @param purchaseInfo информация о покупке
     * @return результат обработки покупки
     */
    @JmsListener(destination = "${parfume.queue.buy}")
    @SendTo("${parfume.queue.response}")
    public ParfumeResponse handlePurchaseRequest(Map<String, Object> purchaseInfo) {
        try {
            // Извлекаем данные о парфюме из карты
            Long parfumeId = Long.valueOf(purchaseInfo.get("parfumeId").toString());

            // Получаем парфюм по ID из базы данных
            Parfume parfume = parfumeService.getParfumeById(parfumeId);

            // Проверка, найден ли парфюм
            if (parfume == null) {
                return new ParfumeResponse(false, "Парфюм с ID " + parfumeId + " не найден");
            }

            // Здесь можно добавить логику обработки покупки, например, уменьшение количества на складе
            // parfumeService.processPurchase(parfumeId);

            return new ParfumeResponse(true, "Покупка парфюма успешно обработана", parfume);
        } catch (Exception e) {
            return new ParfumeResponse(false, "Ошибка при обработке покупки парфюма: " + e.getMessage());
        }
    }
}