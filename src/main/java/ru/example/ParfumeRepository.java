package ru.example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParfumeRepository extends JpaRepository<Parfume, Long> {
    List<Parfume> findByTypeContainingIgnoreCase(String type);

    @Query("SELECT COALESCE(MAX(p.id), 0) FROM Parfume p")
    Long getMaxId();
}