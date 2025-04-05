package ru.example;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "parfum_store")
public class Parfume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название изделия не должно быть пустым")
    @Size(max = 100, message = "Название изделия не должно превышать 100 символов")
    private String name;

    @NotBlank(message = "Материал не должен быть пустым")
    @Size(max = 50, message = "Материал не должен превышать 50 символов")
    private String type;

    @Size(max = 255, message = "Описание не должно превышать 255 символов")
    private String description;

    @NotNull(message = "Вес изделия обязателен")
    @DecimalMin(value = "0.1", message = "Вес изделия должен быть больше 0")
    private double weight;

    @NotNull(message = "Цена изделия обязательна")
    @DecimalMin(value = "0.1", message = "Цена изделия должна быть больше 0")
    private double price;

    // Конструктор с id
    public Parfume(Long id, String name, String type, String description, double weight, double price) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.weight = weight;
        this.price = price;
    }

    public Parfume(String name2, String type2, String description2, double weight2, double price2) {

    }


    public Parfume() {
        
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public double getWeight() { return weight; }
    public double getPrice() { return price; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setDescription(String description) { this.description = description; }
    public void setWeight(double weight) { this.weight = weight; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return "Парфюм [id=" + id + ", название=" + name + ", тип=" + type +
                ", описание=" + description + ", вес=" + weight + ", цена=" + price + "]";
    }
}
