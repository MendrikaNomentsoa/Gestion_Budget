package com.budget.service;

import java.util.List;
import java.util.stream.Collectors;

import com.budget.dto.CategoryDTO;
import com.budget.entity.Category;
import com.budget.entity.User;
import com.budget.repository.CategoryRepository;
import com.budget.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CategoryService {

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private UserRepository userRepository;

    /**
     * Crée une nouvelle catégorie pour l'utilisateur connecté
     */
    @Transactional
    public CategoryDTO creer(Long userId, CategoryDTO dto) {
        User user = userRepository.trouverParId(userId);
        if (user == null) {
            throw new RuntimeException("Utilisateur introuvable");
        }

        Category category = new Category();
        category.setNom(dto.getNom());
        category.setCouleur(dto.getCouleur());
        category.setUser(user);

        categoryRepository.sauvegarder(category);
        return new CategoryDTO(category);
    }

    /**
     * Retourne toutes les catégories de l'utilisateur connecté
     */
    public List<CategoryDTO> listerParUser(Long userId) {
        return categoryRepository.trouverParUserId(userId)
                .stream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Supprime une catégorie
     * Vérifie que la catégorie appartient à l'utilisateur connecté
     */
    @Transactional
    public void supprimer(Long userId, Long categoryId) {
        Category category = categoryRepository.trouverParId(categoryId);

        if (category == null || !category.getUser().getId().equals(userId)) {
            throw new RuntimeException("Catégorie introuvable");
        }

        categoryRepository.supprimer(categoryId);
    }
}