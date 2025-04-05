package ru.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParfumeService {

    @Autowired
    private ParfumeRepository parfumeRepository;

    // Метод должен возвращать сохраненный объект
    public Parfume saveParfume(Parfume parfume) {
        return parfumeRepository.save(parfume);
    }

    public Parfume getParfumeById(Long id) {
        return parfumeRepository.findById(id).orElse(null);
    }

    // Метод должен возвращать обновленный объект
    public Parfume updateParfume(Long id, Parfume parfumeDetails) {
        Parfume parfume = parfumeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Парфюм с ID " + id + " не найден"));
        
        // Обновляем поля
        parfume.setName(parfumeDetails.getName());
        parfume.setType(parfumeDetails.getType());
        parfume.setDescription(parfumeDetails.getDescription());
        parfume.setWeight(parfumeDetails.getWeight());
        parfume.setPrice(parfumeDetails.getPrice());
        
        return parfumeRepository.save(parfume);
    }

    // Метод должен возвращать boolean для отметки успешного удаления
    public boolean deleteParfume(Long id) {
        if (parfumeRepository.existsById(id)) {
            parfumeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Parfume> getAllParfume() {
        return parfumeRepository.findAll();
    }

    public List<Parfume> searchByType(String type) {
        return parfumeRepository.findByTypeContainingIgnoreCase(type);
    }
}