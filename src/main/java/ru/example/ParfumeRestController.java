package ru.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.example.JMS.MessageBrowser;
import ru.example.JMS.ParfumeJmsProducer;

import java.util.List;

/**
 * REST контроллер для работы с парфюмами.
 * Поддерживает как синхронную обработку через ParfumeService,
 * так и асинхронную обработку через JMS (ParfumeJmsProducer).
 * При любой операции отправляет JMS-сообщение в очередь.
 */
@RestController
@RequestMapping("/api/parfume")  // Устанавливаем базовый путь для всех методов контроллера
public class ParfumeRestController {

    @Autowired
    private ParfumeService parfumeService;  // Сервис для работы с парфюмами

    @Autowired
    private ParfumeJmsProducer jmsProducer;  // Производитель JMS-сообщений для асинхронной обработки

    /**
     * Получение всех парфюмов.
     * @param async Если true, возвращает только статус отправки запроса в JMS
     * @return ResponseEntity со списком парфюмов или статусом запроса
     */
    @GetMapping
    public ResponseEntity<?> getAllParfumes(@RequestParam(value = "async", defaultValue = "false") boolean async) {
        // Всегда отправляем сообщение в JMS для получения всех парфюмов
        jmsProducer.getAllParfumes();
        sendAndCheck();  // Проверка состояния очереди после отправки

        if (async) {
            // Если запрашивается асинхронный режим, возвращаем статус запроса
            return ResponseEntity.accepted().body("Запрос на получение всех парфюмов отправлен");
        } else {
            // В синхронном режиме выполняем запрос напрямую к сервису
            List<Parfume> parfumes = parfumeService.getAllParfume();
            return new ResponseEntity<>(parfumes, HttpStatus.OK);  // Возвращаем список парфюмов с успешным статусом
        }
    }

    /**
     * Получение парфюма по ID.
     * @param id ID парфюма
     * @param async Если true, возвращает только статус отправки запроса в JMS
     * @return ResponseEntity с парфюмом или статусом запроса
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getParfumeById(
            @PathVariable Long id,  // Извлекаем ID из URL
            @RequestParam(value = "async", defaultValue = "false") boolean async) {
        
        // Отправляем сообщение в JMS для получения парфюма по ID
        jmsProducer.getParfumeById(id);
        sendAndCheck();  // Проверка состояния очереди

        if (async) {
            // Для асинхронного режима возвращаем статус отправки запроса
            return ResponseEntity.accepted().body("Запрос на получение парфюма с ID " + id + " отправлен");
        } else {
            // Для синхронного запроса получаем парфюм по ID
            Parfume parfume = parfumeService.getParfumeById(id);
            // Проверяем, существует ли парфюм
            if (parfume != null) {
                return new ResponseEntity<>(parfume, HttpStatus.OK);  // Успешный ответ с парфюмом
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Возвращаем статус 404, если парфюм не найден
            }
        }
    }

    /**
     * Создание нового парфюма.
     * @param parfume Данные парфюма
     * @param async Если true, возвращает только статус отправки запроса в JMS
     * @return ResponseEntity с созданным парфюмом или статусом запроса
     */
    @PostMapping
    public ResponseEntity<?> createParfume(
            @RequestBody Parfume parfume,  // Данные парфюма передаются в теле запроса
            @RequestParam(value = "async", defaultValue = "false") boolean async) {
        
        // Отправляем сообщение в JMS для создания нового парфюма
        if (async) {
            // Если запрашивается асинхронный режим, возвращаем статус отправки запроса
            return ResponseEntity.accepted().body("Запрос на добавление парфюма отправлен");
        } else {
            try {
                jmsProducer.addParfume(parfume);  // Отправка парфюма в JMS
                sendAndCheck();  // Проверка состояния очереди
                return new ResponseEntity<>(HttpStatus.CREATED);  // Успешный ответ с кодом 201
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Обработка ошибок
            }
        }
    }

    /**
     * Обновление существующего парфюма.
     * @param id ID парфюма
     * @param parfumeDetails Новые данные парфюма
     * @param async Если true, возвращает только статус отправки запроса в JMS
     * @return ResponseEntity с обновленным парфюмом или статусом запроса
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateParfume(
            @PathVariable Long id,  // Извлечение ID из URL
            @RequestBody Parfume parfumeDetails,  // Новые данные парфюма
            @RequestParam(value = "async", defaultValue = "false") boolean async) {
        
        // Установка ID парфюма для обновления
        parfumeDetails.setId(id);

        // Отправляем сообщение в JMS для обновления парфюма
        jmsProducer.updateParfume(parfumeDetails);
        sendAndCheck();  // Проверка состояния очереди

        if (async) {
            // Если запрашивается асинхронный режим, возвращаем статус отправки запроса
            return ResponseEntity.accepted().body("Запрос на обновление парфюма с ID " + id + " отправлен");
        } else {
            try {
                // Обновление парфюма через сервис
                Parfume updatedParfume = parfumeService.updateParfume(id, parfumeDetails);
                return new ResponseEntity<>(updatedParfume, HttpStatus.OK);  // Возвращаем обновленный парфюм
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Парфюм не найден
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Обработка ошибок
            }
        }
    }

    /**
     * Удаление парфюма.
     * @param id ID парфюма
     * @param async Если true, возвращает только статус отправки запроса в JMS
     * @return ResponseEntity со статусом операции
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParfume(
            @PathVariable Long id,  // Извлечение ID из URL
            @RequestParam(value = "async", defaultValue = "false") boolean async) {
        
        // Отправляем сообщение в JMS для удаления парфюма
        jmsProducer.deleteParfume(id);
        sendAndCheck();  // Проверка состояния очереди

        if (async) {
            // Если запрашивается асинхронный режим, возвращаем статус отправки запроса
            return ResponseEntity.accepted().body("Запрос на удаление парфюма с ID " + id + " отправлен");
        } else {
            try {
                boolean deleted = parfumeService.deleteParfume(id);  // Удаление парфюма через сервис
                if (deleted) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // Успешный ответ с кодом 204
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Парфюм не найден
                }
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Обработка ошибок
            }
        }
    }

    /**
     * Поиск парфюмов по типу.
     * @param type Тип парфюма для поиска
     * @param async Если true, возвращает только статус отправки запроса в JMS
     * @return ResponseEntity со списком найденных парфюмов или статусом запроса
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchParfumesByType(
            @RequestParam(value = "type") String type,  // Тип парфюма передается в параметрах запроса
            @RequestParam(value = "async", defaultValue = "false") boolean async) {
        
        // Проверка на наличие типа парфюма
        if (type == null || type.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // 400 Bad Request если тип не указан
        }

        // Отправляем сообщение в JMS для поиска по типу
        jmsProducer.searchParfumesByType(type);
        sendAndCheck();  // Проверка состояния очереди

        if (async) {
            // Если запрашивается асинхронный режим, возвращаем статус отправки запроса
            return ResponseEntity.accepted().body("Запрос на поиск парфюмов по типу '" + type + "' отправлен");
        } else {
            // Выполнение поиска через сервис
            List<Parfume> parfumes = parfumeService.searchByType(type);
            return new ResponseEntity<>(parfumes, HttpStatus.OK);  // Возвращаем список найденных парфюмов
        }
    }

    /**
     * Обработка покупки парфюма.
     * @param id ID парфюма
     * @return ResponseEntity со статусом операции
     */
    @PostMapping("/{id}/purchase")
    public ResponseEntity<?> purchaseParfume(@PathVariable Long id) {
        try {
            // Получаем парфюм по ID для подтверждения покупки
            Parfume parfume = parfumeService.getParfumeById(id);
            if (parfume == null) {
                return new ResponseEntity<>("Парфюм с ID " + id + " не найден", HttpStatus.NOT_FOUND);  // 404 если парфюм не найден
            }

            // Сохраняем название парфюма для сообщения
            String parfumeName = parfume.getName();

            // Отправляем данные о покупке в JMS-очередь
            jmsProducer.purchaseParfume(id, parfume);
            sendAndCheck();  // Проверка состояния очереди

            // Удаляем парфюм из базы данных сразу после отправки сообщения
            boolean deleted = parfumeService.deleteParfume(id);

            if (deleted) {
                return ResponseEntity.ok("Парфюм '" + parfumeName + "' успешно куплен и удален из базы данных");  // Успешный ответ
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Покупка оформлена, но возникла ошибка при удалении парфюма из базы данных");  // Ошибка при удалении
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Ошибка при оформлении заказа: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR);  // Обработка ошибок
        }
    }

    // Пример кода для проверки после отправки сообщения
    public void sendAndCheck() {
        System.out.println("Сообщение отправлено, проверяем очередь:");
        MessageBrowser.main(new String[]{});  // Вызов метода для проверки состояния очереди

        // Добавляем небольшую задержку, чтобы увидеть сообщение в очереди
        try {
            Thread.sleep(1000); // Ждем 1 секунду
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // Восстанавливаем состояние потока
        }
    }
}