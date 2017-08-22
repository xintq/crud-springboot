package com.example.crud.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    Region findByCode(String code);

    List<Region> findByNameContaining(String name);

    List<Region> findByCodeContaining(String code);

    Page<Region> findByNameContainingOrLocalNameContainingOrCodeContainingOrTelCodeContainingAllIgnoringCase(
            String name,
            String localName,
            String telCode,
            String code,
            Pageable pageable
    );

    List<Region> findByNameContainingOrLocalNameContainingOrCodeContainingOrTelCodeContainingAllIgnoringCase(
            String name,
            String localName,
            String code,
            String telCode
    );
}
