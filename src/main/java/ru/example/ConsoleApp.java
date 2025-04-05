package ru.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import ru.example.JMS.ParfumeJmsProducer;
import ru.example.JMS.ParfumeResponse;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class ConsoleApp implements CommandLineRunner {

    private final Scanner scanner = new Scanner(System.in);
    
    @Autowired
    private ParfumeJmsProducer jmsProducer;
    
    @Value("${parfume.queue.response}")
    private String responseQueueName;
    
    // CompletableFuture для обработки ответов JMS асинхронно
    private CompletableFuture<ParfumeResponse> responsePromise;

    @Override
    public void run(String... args) {
        while (true) {
            System.out.println("\n1. Добавить парфюм\n2. Показать все позиции\n3. Редактировать по ID\n4. Удалить по ID\n5. Искать по типу\n6. Выход");
            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1 -> addParfume();
                    case 2 -> showAllParfume();
                    case 3 -> editParfume();
                    case 4 -> deleteParfume();
                    case 5 -> searchParfumeByType();
                    case 6 -> System.exit(0);
                    default -> System.out.println("Неправильный выбор!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите число!");
            } catch (Exception e) {
                System.out.println("Произошла ошибка: " + e.getMessage());
            }
        }
    }

    /**
     * JMS слушатель для получения ответов асинхронно
     * Этот метод будет вызван Spring JMS, когда сообщение придет в очередь ответов
     */
    @JmsListener(destination = "${parfume.queue.response}")
    public void receiveJmsResponse(ParfumeResponse response) {
        System.out.println("Получен ответ: " + (response.isSuccess() ? "УСПЕШНО" : "ОШИБКА"));
        System.out.println("Сообщение: " + response.getMessage());
        
        if (responsePromise != null && !responsePromise.isDone()) {
            responsePromise.complete(response);
        } else {
            // Если текущего ожидающего запроса нет, просто выводим информацию
            displayResponseDetails(response);
        }
    }
    
    /**
     * Вспомогательный метод для отображения деталей ответа
     */
    private void displayResponseDetails(ParfumeResponse response) {
        if (response.isSuccess()) {
            if (response.getParfume() != null) {
                System.out.println("Полученные данные парфюма: " + response.getParfume());
            } else if (response.getParfumes() != null && !response.getParfumes().isEmpty()) {
                System.out.println("Получен список парфюмов, количество записей: " + response.getParfumes().size());
            }
        } else {
            System.out.println("Причина ошибки: " + response.getMessage());
        }
    }

    private void addParfume() {
        try {
            System.out.print("Введите название: ");
            String name = scanner.nextLine();
            System.out.print("Введите тип парфюма: ");
            String type = scanner.nextLine();
            System.out.print("Введите описание парфюма: ");
            String description = scanner.nextLine();
            System.out.print("Введите вес: ");
            double weight = Double.parseDouble(scanner.nextLine());
            System.out.print("Введите цену: ");
            double price = Double.parseDouble(scanner.nextLine());

            // Создаем новый объект Parfume
            Parfume parfume = new Parfume();
            parfume.setName(name);
            parfume.setDescription(description);
            parfume.setType(type);
            parfume.setWeight(weight);
            parfume.setPrice(price);

            // Готовим CompletableFuture для получения ответа
            responsePromise = new CompletableFuture<>();
            
            // Отправляем сообщение через JMS продюсер
            System.out.println("Отправка запроса на добавление парфюма...");
            jmsProducer.addParfume(parfume);
            
            // Ожидаем и обрабатываем ответ асинхронно
            ParfumeResponse response = waitForResponse();
            
            if (response != null) {
                if (response.isSuccess()) {
                    System.out.println("✅ Парфюм успешно добавлен!");
                    System.out.println("Информация о добавленном парфюме: " + response.getParfume());
                } else {
                    System.out.println("❌ Ошибка при добавлении парфюма: " + response.getMessage());
                }
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Неверный формат числа");
        } catch (Exception e) {
            System.out.println("Произошла ошибка при добавлении парфюма: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAllParfume() {
        try {
            // Готовим CompletableFuture для получения ответа
            responsePromise = new CompletableFuture<>();
            
            // Отправляем запрос на получение всех парфюмов через JMS
            System.out.println("Отправка запроса на получение всех парфюмов...");
            jmsProducer.getAllParfumes();
            
            // Ожидаем и обрабатываем ответ асинхронно
            ParfumeResponse response = waitForResponse();
            
            if (response != null) {
                if (response.isSuccess()) {
                    List<Parfume> parfumeList = response.getParfumes();
                    if (parfumeList != null && !parfumeList.isEmpty()) {
                        System.out.println("✅ Получен список парфюмов (" + parfumeList.size() + " записей):");
                        System.out.println("----------------------------------------------------");
                        parfumeList.forEach(p -> {
                            System.out.println("ID: " + p.getId() + 
                                              " | Название: " + p.getName() + 
                                              " | Тип: " + p.getType() + 
                                              " | Описание: " + p.getDescription() + 
                                              " | Вес: " + p.getWeight() + 
                                              " | Цена: " + p.getPrice());
                        });
                        System.out.println("----------------------------------------------------");
                    } else {
                        System.out.println("ℹ️ Список парфюма пуст.");
                    }
                } else {
                    System.out.println("❌ Ошибка при получении списка парфюма: " + response.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при получении списка парфюма: " + e.getMessage());
        }
    }

    private void editParfume() {
        try {
            System.out.print("Введите ID парфюма для редактирования: ");
            Long id = Long.parseLong(scanner.nextLine());

            // Готовим CompletableFuture для получения ответа
            responsePromise = new CompletableFuture<>();
            
            // Сначала получаем текущие данные парфюма
            System.out.println("Отправка запроса на получение информации о парфюме #" + id + "...");
            jmsProducer.getParfumeById(id);
            
            ParfumeResponse getResponse = waitForResponse();
            
            if (getResponse == null || !getResponse.isSuccess()) {
                System.out.println("❌ Парфюм не найден или ошибка при получении: " + 
                                  (getResponse != null ? getResponse.getMessage() : "Ответ не получен"));
                return;
            }
            
            Parfume parfume = getResponse.getParfume();
            if (parfume == null) {
                System.out.println("❌ Парфюм не найден!");
                return;
            }

            System.out.println("ℹ️ Текущие данные парфюма:");
            System.out.println("ID: " + parfume.getId());
            System.out.println("Название: " + parfume.getName());
            System.out.println("Тип: " + parfume.getType());
            System.out.println("Описание: " + parfume.getDescription());
            System.out.println("Вес: " + parfume.getWeight());
            System.out.println("Цена: " + parfume.getPrice());
            
            // Запрашиваем новые данные
            System.out.println("\nВведите новые данные (оставьте пустым для сохранения текущего значения):");
            
            System.out.print("Название [" + parfume.getName() + "]: ");
            String name = scanner.nextLine();
            if (!name.isEmpty()) parfume.setName(name);
            
            System.out.print("Тип [" + parfume.getType() + "]: ");
            String type = scanner.nextLine();
            if (!type.isEmpty()) parfume.setType(type);
            
            System.out.print("Описание [" + parfume.getDescription() + "]: ");
            String description = scanner.nextLine();
            if (!description.isEmpty()) parfume.setDescription(description);
            
            System.out.print("Вес [" + parfume.getWeight() + "]: ");
            String weight = scanner.nextLine();
            if (!weight.isEmpty()) parfume.setWeight(Double.parseDouble(weight));
            
            System.out.print("Цена [" + parfume.getPrice() + "]: ");
            String price = scanner.nextLine();
            if (!price.isEmpty()) parfume.setPrice(Double.parseDouble(price));

            // Создаем новый CompletableFuture для получения ответа на обновление
            responsePromise = new CompletableFuture<>();
            
            // Отправляем запрос на обновление через JMS
            System.out.println("Отправка запроса на обновление данных парфюма...");
            jmsProducer.updateParfume(parfume);
            
            // Ожидаем и обрабатываем ответ асинхронно
            ParfumeResponse putResponse = waitForResponse();
            
            if (putResponse != null) {
                if (putResponse.isSuccess()) {
                    System.out.println("✅ Информация о парфюме успешно обновлена!");
                    System.out.println("Обновленные данные: " + putResponse.getParfume());
                } else {
                    System.out.println("❌ Ошибка при обновлении парфюма: " + putResponse.getMessage());
                }
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Неверный формат числа");
        } catch (Exception e) {
            System.out.println("Произошла ошибка при редактировании парфюма: " + e.getMessage());
        }
    }

    private void deleteParfume() {
        try {
            System.out.print("Введите ID для удаления парфюма: ");
            Long id = Long.parseLong(scanner.nextLine());
            
            
            
            // Готовим CompletableFuture для получения ответа
            responsePromise = new CompletableFuture<>();
            
            // Отправляем запрос на удаление через JMS
            System.out.println("Отправка запроса на удаление парфюма #" + id + "...");
            jmsProducer.deleteParfume(id);
            
            // Ожидаем и обрабатываем ответ асинхронно
            ParfumeResponse response = waitForResponse();
            
            if (response != null) {
                if (response.isSuccess()) {
                    System.out.println("✅ Парфюм с ID " + id + " успешно удален!");
                } else {
                    System.out.println("❌ Ошибка при удалении парфюма: " + response.getMessage());
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Неверный формат числа");
        } catch (Exception e) {
            System.out.println("Произошла ошибка при удалении парфюма: " + e.getMessage());
        }
    }

    private void searchParfumeByType() {
        try {
            System.out.print("Введите тип парфюма для поиска: ");
            String type = scanner.nextLine();
            
            // Готовим CompletableFuture для получения ответа
            responsePromise = new CompletableFuture<>();
            
            // Отправляем запрос на поиск через JMS
            System.out.println("Отправка запроса на поиск парфюмов с типом '" + type + "'...");
            jmsProducer.searchParfumesByType(type);
            
            // Ожидаем и обрабатываем ответ асинхронно
            ParfumeResponse response = waitForResponse();
            
            if (response != null) {
                if (response.isSuccess()) {
                    List<Parfume> results = response.getParfumes();
                    if (results != null && !results.isEmpty()) {
                        System.out.println("✅ Найдено " + results.size() + " парфюмов с типом '" + type + "':");
                        System.out.println("----------------------------------------------------");
                        results.forEach(p -> {
                            System.out.println("ID: " + p.getId() + 
                            " | Название: " + p.getName() + 
                            " | Тип: " + p.getType() + 
                            " | Цена: " + p.getPrice());
      });
      System.out.println("----------------------------------------------------");
  } else {
      System.out.println("ℹ️ Парфюмы с типом '" + type + "' не найдены.");
  }
} else {
  System.out.println("❌ Ошибка при поиске парфюмов: " + response.getMessage());
}
}
} catch (Exception e) {
System.out.println("Произошла ошибка при поиске парфюмов: " + e.getMessage());
}
}

/**
* Ожидает ответ через CompletableFuture
* @return Полученный ответ или null, если ответ не получен за указанное время
*/
private ParfumeResponse waitForResponse() {
try {
System.out.println("⏳ Ожидание ответа от сервера...");

// Ждем ответ в течение 10 секунд
ParfumeResponse response = responsePromise.get(10, TimeUnit.SECONDS);
return response;
} catch (InterruptedException e) {
System.out.println("❌ Ожидание ответа было прервано.");
Thread.currentThread().interrupt();
return null;
} catch (ExecutionException e) {
System.out.println("❌ Ошибка при получении ответа: " + e.getCause().getMessage());
return null;
} catch (TimeoutException e) {
System.out.println("⏰ Время ожидания ответа истекло (10 секунд).");
return null;
} finally {
// Если CompletableFuture еще не выполнен, отменяем его
if (responsePromise != null && !responsePromise.isDone()) {
responsePromise.cancel(true);
}
}
}
}