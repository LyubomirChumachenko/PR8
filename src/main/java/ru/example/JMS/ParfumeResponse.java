// Пакет, в котором находится класс ParfumeResponse
package ru.example.JMS;

// Импортируем необходимые классы
import ru.example.Parfume; // Импортируем класс Parfume, который описывает духи
import java.io.Serializable; // Импортируем интерфейс Serializable для сериализации объектов
import java.util.Collections; // Импортируем класс Collections для работы с коллекциями
import java.util.List; // Импортируем интерфейс List для работы со списками

// Класс ParfumeResponse реализует интерфейс Serializable
public class ParfumeResponse implements Serializable {
    // Поле, указывающее на успешность операции
    private boolean success;
    // Поле для хранения сообщения, которое описывает результат операции
    private String message;
    // Поле, содержащее список парфюмерии (Parfume)
    private List<Parfume> parfumes;
    // Поле для хранения одного парфюма
    private Parfume parfume;

    // Конструктор без параметров
    public ParfumeResponse() {}

    // Конструктор с параметрами, позволяющий задать успех и сообщение
    public ParfumeResponse(boolean success, String message) {
        this.success = success; // Устанавливаем значение поля success
        this.message = message; // Устанавливаем значение поля message
        this.parfumes = Collections.emptyList(); // Инициализируем список parfumes пустым списком
    }

    // Конструктор, позволяющий задать успех, сообщение и один парфюм
    public ParfumeResponse(boolean success, String message, Parfume parfume) {
        this.success = success; // Устанавливаем значение поля success
        this.message = message; // Устанавливаем значение поля message
        this.parfume = parfume; // Устанавливаем переданный парфюм
    }

    // Конструктор, позволяющий задать успех, сообщение и список парфюмерии
    public ParfumeResponse(boolean success, String message, List<Parfume> parfumes) {
        this.success = success; // Устанавливаем значение поля success
        this.message = message; // Устанавливаем значение поля message
        this.parfumes = parfumes; // Устанавливаем переданный список парфюмерии
    }

    // Геттер для поля success
    public boolean isSuccess() {
        return success; // Возвращаем текущее значение поля success
    }

    // Сеттер для поля success
    public void setSuccess(boolean success) {
        this.success = success; // Устанавливаем значение поля success
    }

    // Геттер для поля message
    public String getMessage() {
        return message; // Возвращаем текущее значение поля message
    }

    // Сеттер для поля message
    public void setMessage(String message) {
        this.message = message; // Устанавливаем значение поля message
    }

    // Геттер для поля parfumes
    public List<Parfume> getParfumes() {
        return parfumes; // Возвращаем список парфюмерии
    }

    // Сеттер для поля parfumes
    public void setParfumes(List<Parfume> parfumes) {
        this.parfumes = parfumes; // Устанавливаем значение поля parfumes
    }

    // Геттер для поля parfume
    public Parfume getParfume() {
        return parfume; // Возвращаем текущий парфюм
    }

    // Сеттер для поля parfume
    public void setParfume(Parfume parfume) {
        this.parfume = parfume; // Устанавливаем значение поля parfume
    }
}