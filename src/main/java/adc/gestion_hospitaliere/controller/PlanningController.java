package adc.gestion_hospitaliere.controller;
import adc.gestion_hospitaliere.Enums.StatutRendezVous;
import adc.gestion_hospitaliere.dto.ResponseApi.PagedResponse;
import adc.gestion_hospitaliere.dto.planning.RendezVousResponseDto;
import adc.gestion_hospitaliere.service.PlanningService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/planning")
@RequiredArgsConstructor
public class PlanningController {

    private final PlanningService planningService;

    // Récupérer l'ID du médecin connecté (à adapter selon votre UserDetails)
    private Integer getCurrentMedecinId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Ici, suppose que l'utilisateur a un attribut "medecinId" ou que son username correspond à l'email du médecin
        // Vous devrez charger le médecin associé à l'utilisateur. Exemple simple :
        return 1; // à remplacer par la vraie logique
    }

    @GetMapping("/rendezvous")
    public ResponseEntity<PagedResponse<RendezVousResponseDto>> getRendezVous(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) StatutRendezVous statut,
            @RequestParam(defaultValue = "1") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize) {

        Integer medecinId = getCurrentMedecinId();
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        Page<RendezVousResponseDto> page = planningService.getRendezVousByMedecin(medecinId, start, end, statut, pageable);
        return ResponseEntity.ok(PagedResponse.of(page));
    }

    @PatchMapping("/rendezvous/{id}/statut")
    public ResponseEntity<Void> updateStatut(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        String nouveauStatut = payload.get("statut");
        if (nouveauStatut == null) {
            throw new RuntimeException("Le statut est requis");
        }
        planningService.updateStatut(id, StatutRendezVous.valueOf(nouveauStatut));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Integer medecinId = getCurrentMedecinId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = now.withHour(23).withMinute(59).withSecond(59);
        long rdvAujourdhui = planningService.countByMedecinAndDateBetween(medecinId, startOfDay, endOfDay);
        // On pourrait ajouter d'autres stats (rdv à venir, passés, etc.)
        Map<String, Object> stats = new HashMap<>();
        stats.put("aujourdhui", rdvAujourdhui);
        return ResponseEntity.ok(stats);
    }
}