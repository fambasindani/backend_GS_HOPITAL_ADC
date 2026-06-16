package adc.gestion_hospitaliere.service;
import adc.gestion_hospitaliere.Entity.Consultation;
import adc.gestion_hospitaliere.Entity.Medecin;
import adc.gestion_hospitaliere.Entity.Patient;
import adc.gestion_hospitaliere.Enums.EvolutionConsultation;
import adc.gestion_hospitaliere.Repository.ConsultationRepository;
import adc.gestion_hospitaliere.Repository.MedecinRepository;
import adc.gestion_hospitaliere.Repository.PatientRepository;
import adc.gestion_hospitaliere.dto.consultation.ConsultationRequestDto;
import adc.gestion_hospitaliere.dto.consultation.ConsultationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;

    public Page<ConsultationResponseDto> getConsultationsByPatient(Integer patientId, Pageable pageable) {
        Page<Consultation> page = consultationRepository.findByIdPatient(patientId, pageable);
        return page.map(this::convertToDto);
    }

    public Page<ConsultationResponseDto> getConsultationsByMedecin(Integer medecinId, Pageable pageable) {
        Page<Consultation> page = consultationRepository.findByIdMedecin(medecinId, pageable);
        return page.map(this::convertToDto);
    }

    public Page<ConsultationResponseDto> getAllConsultations(Pageable pageable) {
        Page<Consultation> page = consultationRepository.findAll(pageable);
        return page.map(this::convertToDto);
    }

    public ConsultationResponseDto getConsultation(Integer id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultation non trouvée"));
        return convertToDto(consultation);
    }

    @Transactional
    public ConsultationResponseDto createConsultation(ConsultationRequestDto dto) {
        Patient patient = patientRepository.findById(dto.getIdPatient())
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
        Medecin medecin = medecinRepository.findById(dto.getIdMedecin())
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));

        Consultation consultation = Consultation.builder()
                .idRdv(dto.getIdRdv())
                .idPatient(dto.getIdPatient())
                .idMedecin(dto.getIdMedecin())
                .dateConsultation(dto.getDateConsultation())
                .motifConsultation(dto.getMotifConsultation())
                .histoireMaladie(dto.getHistoireMaladie())
                .diagnostic(dto.getDiagnostic())
                .traitementPrescris(dto.getTraitementPrescris())
                .observations(dto.getObservations())
                .temperature(dto.getTemperature())
                .pouls(dto.getPouls())
                .pressionSystolique(dto.getPressionSystolique())
                .pressionDiastolique(dto.getPressionDiastolique())
                .saturation(dto.getSaturation())
                .glycemie(dto.getGlycemie())
                .poids(dto.getPoids())
                .taille(dto.getTaille())
                .certificatMedical(dto.getCertificatMedical())
                .arretTravailDebut(dto.getArretTravailDebut())
                .arretTravailFin(dto.getArretTravailFin())
                .evolution(dto.getEvolution() != null ? EvolutionConsultation.valueOf(dto.getEvolution()) : null)
                .prochainRdv(dto.getProchainRdv())
                .notesConfidentielles(dto.getNotesConfidentielles())
                .build();

        // Calculer IMC si poids et taille présents
        if (consultation.getPoids() != null && consultation.getTaille() != null
                && consultation.getTaille().compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal imc = consultation.getPoids()
                    .divide(consultation.getTaille().multiply(consultation.getTaille()), 2, BigDecimal.ROUND_HALF_UP);
            consultation.setImc(imc);
        }

        Consultation saved = consultationRepository.save(consultation);
        // Mettre à jour le statut du rendez-vous si idRdv existe (optionnel)
        return convertToDto(saved);
    }

    @Transactional
    public ConsultationResponseDto updateConsultation(Integer id, ConsultationRequestDto dto) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultation non trouvée"));

        consultation.setDateConsultation(dto.getDateConsultation());
        consultation.setMotifConsultation(dto.getMotifConsultation());
        consultation.setHistoireMaladie(dto.getHistoireMaladie());
        consultation.setDiagnostic(dto.getDiagnostic());
        consultation.setTraitementPrescris(dto.getTraitementPrescris());
        consultation.setObservations(dto.getObservations());
        consultation.setTemperature(dto.getTemperature());
        consultation.setPouls(dto.getPouls());
        consultation.setPressionSystolique(dto.getPressionSystolique());
        consultation.setPressionDiastolique(dto.getPressionDiastolique());
        consultation.setSaturation(dto.getSaturation());
        consultation.setGlycemie(dto.getGlycemie());
        consultation.setPoids(dto.getPoids());
        consultation.setTaille(dto.getTaille());
        consultation.setCertificatMedical(dto.getCertificatMedical());
        consultation.setArretTravailDebut(dto.getArretTravailDebut());
        consultation.setArretTravailFin(dto.getArretTravailFin());
        if (dto.getEvolution() != null) {
            consultation.setEvolution(EvolutionConsultation.valueOf(dto.getEvolution()));
        }
        consultation.setProchainRdv(dto.getProchainRdv());
        consultation.setNotesConfidentielles(dto.getNotesConfidentielles());

        // Recalcul IMC
        if (consultation.getPoids() != null && consultation.getTaille() != null
                && consultation.getTaille().compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal imc = consultation.getPoids()
                    .divide(consultation.getTaille().multiply(consultation.getTaille()), 2, BigDecimal.ROUND_HALF_UP);
            consultation.setImc(imc);
        } else {
            consultation.setImc(null);
        }

        Consultation updated = consultationRepository.save(consultation);
        return convertToDto(updated);
    }

    @Transactional
    public void deleteConsultation(Integer id) {
        if (!consultationRepository.existsById(id)) {
            throw new RuntimeException("Consultation non trouvée");
        }
        consultationRepository.deleteById(id);
    }




    private ConsultationResponseDto convertToDto(Consultation c) {
        return ConsultationResponseDto.builder()
                .idConsultation(c.getIdConsultation())
                .idRdv(c.getIdRdv())
                .idPatient(c.getIdPatient())
                .patientNom(c.getPatient() != null ? c.getPatient().getNom() : null)
                .patientPrenom(c.getPatient() != null ? c.getPatient().getPrenom() : null)
                .idMedecin(c.getIdMedecin())
                .medecinNom(c.getMedecin() != null ? c.getMedecin().getNom() : null)
                .medecinPrenom(c.getMedecin() != null ? c.getMedecin().getPrenom() : null)
                .dateConsultation(c.getDateConsultation())
                .motifConsultation(c.getMotifConsultation())
                .histoireMaladie(c.getHistoireMaladie())
                .diagnostic(c.getDiagnostic())
                .traitementPrescris(c.getTraitementPrescris())
                .observations(c.getObservations())
                .temperature(c.getTemperature())
                .pouls(c.getPouls())
                .pressionSystolique(c.getPressionSystolique())
                .pressionDiastolique(c.getPressionDiastolique())
                .saturation(c.getSaturation())
                .glycemie(c.getGlycemie())
                .poids(c.getPoids())
                .taille(c.getTaille())
                .imc(c.getImc())
                .certificatMedical(c.getCertificatMedical())
                .arretTravailDebut(c.getArretTravailDebut())
                .arretTravailFin(c.getArretTravailFin())
                .evolution(c.getEvolution() != null ? c.getEvolution().name() : null)
                .prochainRdv(c.getProchainRdv())
                .notesConfidentielles(c.getNotesConfidentielles())
                .build();
    }
}
