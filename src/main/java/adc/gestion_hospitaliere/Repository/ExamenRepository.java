package adc.gestion_hospitaliere.Repository;

import adc.gestion_hospitaliere.Entity.Examen;
import adc.gestion_hospitaliere.Entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ExamenRepository extends JpaRepository<Examen, Integer> {
  //  List<Examen> findByPatientId(Integer idPatient);
    @Query("SELECT p FROM Prescription p WHERE p.patient.idPatient = :patientId")
    List<Examen> findByPatientId(@Param("patientId") Integer patientId);

}
