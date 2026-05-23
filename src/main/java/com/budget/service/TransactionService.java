package com.budget.service;

import com.budget.dto.TransactionDTO;
import com.budget.entity.Category;
import com.budget.entity.Transaction;
import com.budget.entity.User;
import com.budget.repository.CategoryRepository;
import com.budget.repository.TransactionRepository;
import com.budget.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Logique métier pour les transactions
 */
@ApplicationScoped
public class TransactionService {

    @Inject
    private TransactionRepository transactionRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private CategoryRepository categoryRepository;

    /**
     * Crée une nouvelle transaction pour un utilisateur
     *
     * @param userId l'id de l'utilisateur connecté (extrait du JWT)
     * @param dto les données envoyées par le frontend
     * @return la transaction créée sous forme de DTO
     */
    @Transactional
    public TransactionDTO creer(Long userId, TransactionDTO dto) {

        // Récupère l'utilisateur connecté
        User user = userRepository.trouverParId(userId);
        if (user == null) {
            throw new RuntimeException("Utilisateur introuvable");
        }

        // Crée la nouvelle transaction
        Transaction transaction = new Transaction();
        transaction.setMontant(dto.getMontant());
        transaction.setType(Transaction.Type.valueOf(dto.getType()));
        transaction.setDescription(dto.getDescription());
        transaction.setDate(dto.getDate());
        transaction.setUser(user);

        // Associe la catégorie si fournie
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.trouverParId(dto.getCategoryId());
            transaction.setCategory(category);
        }

        transactionRepository.sauvegarder(transaction);
        return new TransactionDTO(transaction);
    }

    /**
     * Modifie une transaction existante
     * Vérifie que la transaction appartient bien à l'utilisateur connecté
     */
    @Transactional
    public TransactionDTO modifier(Long userId, Long transactionId, TransactionDTO dto) {

        Transaction transaction = transactionRepository.trouverParId(transactionId);

        // Sécurité : vérifie que cette transaction appartient à l'utilisateur
        if (transaction == null || !transaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Transaction introuvable");
        }

        transaction.setMontant(dto.getMontant());
        transaction.setType(Transaction.Type.valueOf(dto.getType()));
        transaction.setDescription(dto.getDescription());
        transaction.setDate(dto.getDate());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.trouverParId(dto.getCategoryId());
            transaction.setCategory(category);
        }

        return new TransactionDTO(transactionRepository.modifier(transaction));
    }

    /**
     * Supprime une transaction
     * Vérifie que la transaction appartient bien à l'utilisateur connecté
     */
    @Transactional
    public void supprimer(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.trouverParId(transactionId);

        if (transaction == null || !transaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Transaction introuvable");
        }

        transactionRepository.supprimer(transactionId);
    }

    /**
     * Retourne toutes les transactions de l'utilisateur connecté
     * Convertit chaque entité en DTO avec stream().map()
     */
    public List<TransactionDTO> listerParUser(Long userId) {
        return transactionRepository.trouverParUserId(userId)
                .stream()
                .map(TransactionDTO::new)
                .collect(Collectors.toList());
    }
}