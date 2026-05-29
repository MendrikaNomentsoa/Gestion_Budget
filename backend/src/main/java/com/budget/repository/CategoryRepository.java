package com.budget.repository;

import java.util.List;

import com.budget.entity.Category;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class CategoryRepository {

    @PersistenceContext(unitName = "budgetPU")
    private EntityManager em;

    public void sauvegarder(Category category) {
        em.persist(category);
    }

    public Category trouverParId(Long id) {
        return em.find(Category.class, id);
    }

    /**
     * Retourne toutes les catégories d'un utilisateur
     */
    public List<Category> trouverParUserId(Long userId) {
        return em.createQuery(
            "SELECT c FROM Category c WHERE c.user.id = :userId",
            Category.class)
            .setParameter("userId", userId)
            .getResultList();
    }

    public void supprimer(Long id) {
        Category c = em.find(Category.class, id);
        if (c != null) em.remove(c);
    }
}