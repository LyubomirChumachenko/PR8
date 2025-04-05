package ru.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/parfume")
public class ParfumeController {

    @Autowired
    private ParfumeService parfumeService;

    // Показ страницы с формой добавления
    @GetMapping("/new")
    public String showAddParfumeForm(Model model) {
        model.addAttribute("parfume", new Parfume());
        return "parfume-add";
    }

    // Обработка формы добавления - теперь перенаправляем на REST API
    @PostMapping("/new")
    public String addParfumeForm(@ModelAttribute Parfume parfume) {
        parfumeService.saveParfume(parfume);
        return "redirect:/parfume";
    }

    // Показ страницы с формой редактирования
    @GetMapping("/edit/{id}")
    public String showEditParfumeForm(@PathVariable Long id, Model model) {
        Parfume parfume = parfumeService.getParfumeById(id);
        if (parfume != null) {
            model.addAttribute("parfume", parfume);
            return "parfume-edit";
        } else {
            return "redirect:/parfume";
        }
    }

    // Обработка формы редактирования - теперь используем PUT (через скрытое поле _method)
    @PostMapping("/edit/{id}")
    public String updateParfumeForm(@PathVariable Long id, @ModelAttribute Parfume parfumeDetails, Model model) {
        try {
            parfumeService.updateParfume(id, parfumeDetails);
            return "redirect:/parfume";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("parfume", parfumeDetails);
            return "parfume-edit";
        }
    }

    // Показать все парфюмы
    @GetMapping
    public String showAllParfumes(Model model) {
        model.addAttribute("parfumes", parfumeService.getAllParfume());
        return "parfume-list";
    }

    // Поиск парфюмов по типу
    @GetMapping("/search")
    public String searchParfumeByType(@RequestParam(value = "type", required = false) String type, Model model) {
        List<Parfume> parfumeList = new ArrayList<>();

        if (type != null && !type.isEmpty()) {
            parfumeList = parfumeService.searchByType(type);
        }

        model.addAttribute("parfumeList", parfumeList);
        model.addAttribute("searchType", type);
        return "parfume-search";
    }

    // Вместо простого GET запроса используем форму с методом DELETE
    @GetMapping("/delete/{id}")
    public String showDeleteConfirmation(@PathVariable Long id, Model model) {
        Parfume parfume = parfumeService.getParfumeById(id);
        if (parfume != null) {
            model.addAttribute("parfume", parfume);
            return "parfume-delete-confirm";
        } else {
            return "redirect:/parfume";
        }
    }

    // Обработка запроса на выход
    @GetMapping("/exit")
    public String exitApplication() {
        System.exit(0);
        return "exit";
    }
}