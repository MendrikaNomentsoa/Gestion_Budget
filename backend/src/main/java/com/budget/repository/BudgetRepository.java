package com.budget.repository;

import java.util.List;

import com.budget.entity.Budget;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class BudgetRepository {

    @PersistenceContext(unitName = "budgetPU")
    private EntityManager em;

    public void sauvegarder(Budget budget) {
        em.persist(budget);
    }

    public Budget modifier(Budget budget) {
        return em.merge(budget);
    }

    public void supprimer(Long id) {
        Budget b = em.find(Budget.class, id);
        if (b != null) em.remove(b);
    }

    public Budget trouverParId(Long id) {
        return em.find(Budget.class, id);
    }

    /**
     * Retourne tous les budgets d'un utilisateur
     */
    public List<Budget> trouverParUserId(Long userId) {
        return em.createQuery(
            "SELECT b FROM Budget b WHERE b.user.id = :userId",
            Budget.class)
            .setParameter("userId", userId)
            .getResultList();
    }

    /**
     * Trouve un budget pour une catégorie et un mois précis
     * Utilisé par AlertService pour vérifier les dépassements
     */
    public Budget trouverParUserCategoryMois(Long userId, Long categoryId, int mois, int annee) {
        try {
            return em.createQuery(
                "SELECT b FROM Budget b WHERE b.user.id = :userId " +
                "AND b.category.id = :categoryId " +
                "AND b.mois = :mois AND b.annee = :annee",
                Budget.class)
                .setParameter("userId", userId)
                .setParameter("categoryId", categoryId)
                .setParameter("mois", mois)
                .setParameter("annee", annee)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}