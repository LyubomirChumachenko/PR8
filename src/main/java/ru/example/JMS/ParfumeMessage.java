// ParfumeMessage.java
package ru.example.JMS;

// Импортируем необходимые классы
import ru.example.Parfume; // Импортируем класс Parfume для использования в нашем сообщении
import java.io.Serializable; // Импортируем интерфейс Serializable для обеспечения возможности сериализации сообщений

// Класс ParfumeMessage, реализующий интерфейс Serializable для передачи сообщений
public class ParfumeMessage implements Serializable {
    // Уникальный идентификатор сообщения
    private Long id;
    // Экземпляр класса Parfume, представляющий парфюм
    private Parfume parfume;
    // Тип поиска, который может быть использован для фильтрации запросов
    private String searchType;

    // Конструкторы класса ParfumeMessage

    // Конструктор по умолчанию, который необходим для сериализации
    public ParfumeMessage() {}

    // Конструктор, принимающий идентификатор парфюма
    public ParfumeMessage(Long id) {
        this.id = id; // Устанавливаем переданный идентификатор
    }

    // Конструктор, принимающий объект Parfume
    public ParfumeMessage(Parfume parfume) {
        this.parfume = parfume; // Устанавливаем переданный объект Parfume
    }

    // Конструктор, принимающий тип поиска
    public ParfumeMessage(String searchType) {
        this.searchType = searchType; // Устанавливаем переданный тип поиска
    }

    // Геттеры и сеттеры для доступа к полям

    // Метод для получения идентификатора сообщения
    public Long getId() {
        return id; // Возвращаем уникальный идентификатор
    }

    // Метод для установки идентификатора сообщения
    public void setId(Long id) {
        this.id = id; // Устанавливаем уникальный идентификатор
    }

    // Метод для получения объекта Parfume
    public Parfume getParfume() {
        return parfume; // Возвращаем объект Parfume
    }

    // Метод для установки объекта Parfume
    public void setParfume(Parfume parfume) {
        this.parfume = parfume; // Устанавливаем объект Parfume
    }

    // Метод для получения типа поиска
    public String getSearchType() {
        return searchType; // Возвращаем тип поиска
    }

    // Метод для установки типа поиска
    public void setSearchType(String searchType) {
        this.searchType = searchType; // Устанавливаем тип поиска
    }
}