package com.example.crud.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
    Link findByName(String name);

    Page<Link> findByNameContainingOrUrlContainingAllIgnoringCase(
            String name,
            String url,
            Pageable pageable);

    List<Link> findByNameContainingOrUrlContainingAllIgnoringCase(
            String name,
            String url);

}
