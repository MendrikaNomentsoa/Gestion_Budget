package com.budget.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.budget.dto.ObjectifDTO;
import com.budget.entity.Objectif;
import com.budget.entity.User;
import com.budget.repository.ObjectifRepository;
import com.budget.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ObjectifService {

    @Inject
    private ObjectifRepository objectifRepository;

    @Inject
    private UserRepository userRepository;

    /**
     * Crée un nouvel objectif financier
     */
    @Transactional
    public ObjectifDTO creer(Long userId, ObjectifDTO dto) {
        User user = userRepository.trouverParId(userId);
        if (user == null) {
            throw new RuntimeException("Utilisateur introuvable");
        }

        Objectif objectif = new Objectif();
        objectif.setNom(dto.getNom());
        objectif.setMontantCible(BigDecimal.valueOf(dto.getMontantCible()));
        objectif.setMontantActuel(BigDecimal.ZERO);
        objectif.setDescription(dto.getDescription());
        objectif.setUser(user);

        if (dto.getDateEcheance() != null) {
            objectif.setDateEcheance(dto.getDateEcheance());
        }

        objectifRepository.sauvegarder(objectif);
        return new ObjectifDTO(objectif);
    }

    /**
     * Ajoute un montant à l'objectif
     * ex: l'utilisateur économise 50 000 Ar ce mois
     * Vérifie automatiquement si l'objectif est atteint
     */
    @Transactional
    public ObjectifDTO ajouterMontant(Long userId, Long objectifId, double montant) {
        Objectif objectif = objectifRepository.trouverParId(objectifId);

        if (objectif == null || !objectif.getUser().getId().equals(userId)) {
            throw new RuntimeException("Objectif introuvable");
        }

        // Ajoute le montant
        BigDecimal nouveauMontant = objectif.getMontantActuel()
                .add(BigDecimal.valueOf(montant));
        objectif.setMontantActuel(nouveauMontant);

        // Vérifie si l'objectif est atteint
        if (nouveauMontant.compareTo(objectif.getMontantCible()) >= 0) {
            objectif.setStatut(Objectif.Statut.ATTEINT);
        }

        return new ObjectifDTO(objectifRepository.modifier(objectif));
    }

    /**
     * Modifie un objectif existant
     */
    @Transactional
    public ObjectifDTO modifier(Long userId, Long objectifId, ObjectifDTO dto) {
        Objectif objectif = objectifRepository.trouverParId(objectifId);

        if (objectif == null || !objectif.getUser().getId().equals(userId)) {
            throw new RuntimeException("Objectif introuvable");
        }

        objectif.setNom(dto.getNom());
        objectif.setMontantCible(BigDecimal.valueOf(dto.getMontantCible()));
        objectif.setDescription(dto.getDescription());

        if (dto.getDateEcheance() != null) {
            objectif.setDateEcheance(dto.getDateEcheance());
        }

        /**
         * Recalcule le statut après modification
         * Si le nouveau montant cible est supérieur au montant actuel
         * → remet le statut à EN_COURS
         * Si le montant actuel >= nouveau montant cible
         * → garde ATTEINT
         */
        if (objectif.getMontantActuel().compareTo(objectif.getMontantCible()) >= 0) {
            objectif.setStatut(Objectif.Statut.ATTEINT);
        } else {
            objectif.setStatut(Objectif.Statut.EN_COURS);
        }

        return new ObjectifDTO(objectifRepository.modifier(objectif));
    }

    /**
     * Supprime un objectif
     */
    @Transactional
    public void supprimer(Long userId, Long objectifId) {
        Objectif objectif = objectifRepository.trouverParId(objectifId);

        if (objectif == null || !objectif.getUser().getId().equals(userId)) {
            throw new RuntimeException("Objectif introuvable");
        }

        objectifRepository.supprimer(objectifId);
    }

    /**
     * Liste tous les objectifs de l'utilisateur
     */
    public List<ObjectifDTO> listerParUser(Long userId) {
        return objectifRepository.trouverParUserId(userId)
                .stream()
                .map(ObjectifDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Retire un montant de l'objectif
     * ex: urgence — l'utilisateur prend 50 000 Ar dans ses économies vacances
     * Le statut repasse EN_COURS si le montant descend sous la cible
     */
    @Transactional
    public ObjectifDTO retirerMontant(Long userId, Long objectifId, double montant) {
        Objectif objectif = objectifRepository.trouverParId(objectifId);

        if (objectif == null || !objectif.getUser().getId().equals(userId)) {
            throw new RuntimeException("Objectif introuvable");
        }

        // Vérifie qu'on ne retire pas plus que ce qui est épargné
        if (BigDecimal.valueOf(montant).compareTo(objectif.getMontantActuel()) > 0) {
            throw new RuntimeException("Montant insuffisant dans cet objectif");
        }

        BigDecimal nouveauMontant = objectif.getMontantActuel()
                .subtract(BigDecimal.valueOf(montant));
        objectif.setMontantActuel(nouveauMontant);

        // Recalcule le statut
        if (nouveauMontant.compareTo(objectif.getMontantCible()) >= 0) {
            objectif.setStatut(Objectif.Statut.ATTEINT);
        } else {
            objectif.setStatut(Objectif.Statut.EN_COURS);
        }

        return new ObjectifDTO(objectifRepository.modifier(objectif));
    }
}