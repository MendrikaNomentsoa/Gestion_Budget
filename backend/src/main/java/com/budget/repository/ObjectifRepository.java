package com.budget.repository;

import java.util.List;

import com.budget.entity.Objectif;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class ObjectifRepository {

    @PersistenceContext(unitName = "budgetPU")
    private EntityManager em;

    public void sauvegarder(Objectif objectif) {
        em.persist(objectif);
    }

    public Objectif modifier(Objectif objectif) {
        return em.merge(objectif);
    }

    public void supprimer(Long id) {
        Objectif o = em.find(Objectif.class, id);
        if (o != null) em.remove(o);
    }

    public Objectif trouverParId(Long id) {
        return em.find(Objectif.class, id);
    }

    /**
     * Retourne tous les objectifs d'un utilisateur
     * triés par date d'échéance
     */
    public List<Objectif> trouverParUserId(Long userId) {
        return em.createQuery(
            "SELECT o FROM Objectif o WHERE o.user.id = :userId ORDER BY o.dateEcheance ASC",
            Objectif.class)
            .setParameter("userId", userId)
            .getResultList();
    }
}