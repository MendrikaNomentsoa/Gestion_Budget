package com.budget.repository;

import java.util.List;

import com.budget.entity.Transaction;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Accès base de données pour les transactions
 */
@ApplicationScoped
public class TransactionRepository {

    @PersistenceContext(unitName = "budgetPU")
    private EntityManager em;

    /**
     * Sauvegarde une nouvelle transaction
     */
    public void sauvegarder(Transaction transaction) {
        em.persist(transaction);
    }

    /**
     * Met à jour une transaction existante
     * merge() → JPA détecte les changements et fait un UPDATE
     */
    public Transaction modifier(Transaction transaction) {
        return em.merge(transaction);
    }

    /**
     * Supprime une transaction par son id
     */
    public void supprimer(Long id) {
        Transaction t = em.find(Transaction.class, id);
        if (t != null) {
            em.remove(t);
        }
    }

    /**
     * Trouve une transaction par son id
     */
    public Transaction trouverParId(Long id) {
        return em.find(Transaction.class, id);
    }

    /**
     * Retourne toutes les transactions d'un utilisateur
     * triées par date décroissante (plus récent en premier)
     *
     * :userId → paramètre JPA, évite les injections SQL
     */
    public List<Transaction> trouverParUserId(Long userId) {
        return em.createQuery(
            "SELECT t FROM Transaction t WHERE t.user.id = :userId ORDER BY t.date DESC",
            Transaction.class)
            .setParameter("userId", userId)
            .getResultList();
    }

    /**
     * Retourne les transactions d'un utilisateur avec pagination
     * firstResult → à partir de quel enregistrement (offset)
     * maxResults → combien d'enregistrements (limit)
     * ex: page 2 avec 10 par page → firstResult=10, maxResults=10
     */
    public List<Transaction> trouverParUserIdPagine(Long userId, int page, int taille) {
        return em.createQuery(
            "SELECT t FROM Transaction t WHERE t.user.id = :userId ORDER BY t.date DESC",
            Transaction.class)
            .setParameter("userId", userId)
            .setFirstResult(page * taille)
            .setMaxResults(taille)
            .getResultList();
    }
}