package com.budget.repository;

import com.budget.entity.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

/**
 * Repository = couche d'accès à la base de données pour User
 * 
 * @ApplicationScoped → CDI crée une seule instance partagée
 * @PersistenceContext → CDI injecte automatiquement l'EntityManager
 *   (c'est l'objet qui fait les requêtes SQL via JPA)
 */
@ApplicationScoped
public class UserRepository {

    @PersistenceContext(unitName = "budgetPU")
    private EntityManager em;

    /**
     * Enregistre un nouvel utilisateur en base
     * @param user l'utilisateur à sauvegarder
     */
    public void sauvegarder(User user) {
        em.persist(user);
    }

    /**
     * Cherche un utilisateur par son email
     * Retourne null si aucun utilisateur trouvé
     * 
     * @param email l'email à chercher
     * @return l'utilisateur ou null
     */
    public User trouverParEmail(String email) {
        try {
            return em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Cherche un utilisateur par son id
     * 
     * @param id l'id de l'utilisateur
     * @return l'utilisateur ou null
     */
    public User trouverParId(Long id) {
        return em.find(User.class, id);
    }
}