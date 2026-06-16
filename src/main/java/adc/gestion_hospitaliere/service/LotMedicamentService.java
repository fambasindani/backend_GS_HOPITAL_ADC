package adc.gestion_hospitaliere.service;

import adc.gestion_hospitaliere.Entity.LotMedicament;
import adc.gestion_hospitaliere.Entity.Medicament;
import adc.gestion_hospitaliere.Entity.FournisseurPharma;
import adc.gestion_hospitaliere.Enums.StatutLot;
import adc.gestion_hospitaliere.Repository.LotMedicamentRepository;
import adc.gestion_hospitaliere.Repository.MedicamentRepository;
import adc.gestion_hospitaliere.Repository.FournisseurPharmaRepository;
import adc.gestion_hospitaliere.dto.lot.LotRequestDto;
import adc.gestion_hospitaliere.dto.lot.LotResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LotMedicamentService {

    private final LotMedicamentRepository repository;
    private final MedicamentRepository medicamentRepository;
    private final FournisseurPharmaRepository fournisseurRepository;

    public Page<LotResponseDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponseDto);
    }

    public Page<LotResponseDto> search(Integer idMedicament, String numeroLot, StatutLot statut, Pageable pageable) {
        return repository.search(idMedicament, numeroLot, statut, pageable).map(this::toResponseDto);
    }

    public LotResponseDto getById(Integer id) {
        LotMedicament lot = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lot non trouvé"));
        return toResponseDto(lot);
    }

    @Transactional
    public LotResponseDto create(LotRequestDto dto) {
        // Vérifier que le médicament existe
        medicamentRepository.findById(dto.getIdMedicament())
                .orElseThrow(() -> new RuntimeException("Médicament non trouvé"));
        if (dto.getIdFournisseur() != null) {
            fournisseurRepository.findById(dto.getIdFournisseur())
                    .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé"));
        }
        LotMedicament lot = new LotMedicament();
        updateEntity(lot, dto);
        lot = repository.save(lot);
        return toResponseDto(lot);
    }

    @Transactional
    public LotResponseDto update(Integer id, LotRequestDto dto) {
        LotMedicament lot = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lot non trouvé"));
        // Mise à jour
        updateEntity(lot, dto);
        lot = repository.save(lot);
        return toResponseDto(lot);
    }

    @Transactional
    public void delete(Integer id) {
        LotMedicament lot = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lot non trouvé"));
        // On peut ajouter des vérifications (prescriptions, délivrances, etc.)
        if (lot.getPrescriptions() != null && !lot.getPrescriptions().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer un lot associé à des prescriptions");
        }
        repository.delete(lot);
    }

    private void updateEntity(LotMedicament lot, LotRequestDto dto) {
        lot.setIdMedicament(dto.getIdMedicament());
        lot.setNumeroLot(dto.getNumeroLot());
        lot.setIdFournisseur(dto.getIdFournisseur());
        lot.setDateFabrication(dto.getDateFabrication());
        lot.setDatePeremption(dto.getDatePeremption());
        lot.setQuantiteInitial(dto.getQuantiteInitial());
        // Si quantiteRestante n'est pas fournie, on la met égale à quantiteInitial
        lot.setQuantiteRestante(dto.getQuantiteRestante() != null ? dto.getQuantiteRestante() : dto.getQuantiteInitial());
        lot.setPrixAchatUnitaire(dto.getPrixAchatUnitaire());
        lot.setPrixVenteUnitaire(dto.getPrixVenteUnitaire());
        lot.setEmplacementStockage(dto.getEmplacementStockage());
        lot.setDateReception(dto.getDateReception());
        lot.setBonCommande(dto.getBonCommande());
        lot.setFactureFournisseur(dto.getFactureFournisseur());
        lot.setControleQualite(dto.getControleQualite() != null ? dto.getControleQualite() : false);
        lot.setStatut(dto.getStatut() != null ? dto.getStatut() : StatutLot.Disponible);
        lot.setNotes(dto.getNotes());
    }

    private LotResponseDto toResponseDto(LotMedicament lot) {
        String medicamentNom = null;
        if (lot.getMedicament() != null) {
            medicamentNom = lot.getMedicament().getNomCommercial();
        }
        String fournisseurNom = null;
        if (lot.getFournisseur() != null) {
            fournisseurNom = lot.getFournisseur().getNomFournisseur();
        }
        return LotResponseDto.builder()
                .idLot(lot.getIdLot())
                .idMedicament(lot.getIdMedicament())
                .medicamentNom(medicamentNom)
                .numeroLot(lot.getNumeroLot())
                .idFournisseur(lot.getIdFournisseur())
                .fournisseurNom(fournisseurNom)
                .dateFabrication(lot.getDateFabrication())
                .datePeremption(lot.getDatePeremption())
                .quantiteInitial(lot.getQuantiteInitial())
                .quantiteRestante(lot.getQuantiteRestante())
                .prixAchatUnitaire(lot.getPrixAchatUnitaire())
                .prixVenteUnitaire(lot.getPrixVenteUnitaire())
                .emplacementStockage(lot.getEmplacementStockage())
                .dateReception(lot.getDateReception())
                .bonCommande(lot.getBonCommande())
                .factureFournisseur(lot.getFactureFournisseur())
                .controleQualite(lot.getControleQualite())
                .statut(lot.getStatut())
                .notes(lot.getNotes())
                .build();
    }
}