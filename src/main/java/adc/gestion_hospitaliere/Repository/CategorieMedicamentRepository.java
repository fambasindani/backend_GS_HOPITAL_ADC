package adc.gestion_hospitaliere.Repository;

import adc.gestion_hospitaliere.Entity.CategorieMedicament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategorieMedicamentRepository extends JpaRepository<CategorieMedicament, Integer> {
    Page<CategorieMedicament> findByNomCategorieContainingIgnoreCase(String nom, Pageable pageable);
    boolean existsByNomCategorieIgnoreCase(String nom);
    boolean existsByNomCategorieIgnoreCaseAndIdCategorieNot(String nom, Integer id);
}