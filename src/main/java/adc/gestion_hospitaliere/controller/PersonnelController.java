package adc.gestion_hospitaliere.controller;

import adc.gestion_hospitaliere.dto.ResponseApi.PagedResponse;
import adc.gestion_hospitaliere.dto.personnel.PersonnelRequestDto;
import adc.gestion_hospitaliere.dto.personnel.PersonnelResponseDto;
import adc.gestion_hospitaliere.dto.personnel.PersonnelUpdateDto;
import adc.gestion_hospitaliere.service.PersonnelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/personnel")
@RequiredArgsConstructor
public class PersonnelController {

    private final PersonnelService personnelService;

    @GetMapping
    //@PreAuthorize("hasAnyAuthority('ADMIN', 'MEDECIN', 'SECRETAIRE', 'PHARMACIEN')")
    public ResponseEntity<PagedResponse<PersonnelResponseDto>> getAll(
            @RequestParam(defaultValue = "1") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "idPersonnel,desc") String[] sort) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize, Sort.by(direction, sort[0]));
        return ResponseEntity.ok(personnelService.getAllPersonnels(pageable));
    }

    @GetMapping("/search")
    //@PreAuthorize("hasAnyAuthority('ADMIN', 'MEDECIN', 'SECRETAIRE', 'PHARMACIEN')")
    public ResponseEntity<PagedResponse<PersonnelResponseDto>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        return ResponseEntity.ok(personnelService.searchPersonnels(keyword, pageable));
    }

    @GetMapping("/{id}")
    //@PreAuthorize("hasAnyAuthority('ADMIN', 'MEDECIN', 'SECRETAIRE', 'PHARMACIEN')")
    public ResponseEntity<PersonnelResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(personnelService.getPersonnelById(id));
    }

    @PostMapping
    //@PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PersonnelResponseDto> create(@Valid @RequestBody PersonnelRequestDto dto) {
        return ResponseEntity.ok(personnelService.createPersonnel(dto));
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PersonnelResponseDto> update(@PathVariable Integer id, @Valid @RequestBody PersonnelUpdateDto dto) {
        return ResponseEntity.ok(personnelService.updatePersonnel(id, dto));
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        personnelService.deletePersonnel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistiques")
    //@PreAuthorize("hasAnyAuthority('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<Map<String, Object>> getStatistiques() {
        return ResponseEntity.ok(personnelService.getStatistiques());
    }
}