package com.example.crud.domain;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/***
 * CustomerDao = CustomerRepository
 * Keyword              Sample                              JPQL snipplet
 * And                  findByLastnameAndFirstname          ...where x.lastname=?1 and x.firstname=?2
 * Or                   findByLastnameOrFirstname           ...where x.lastname=?1 or x.firstname=?2
 * Between              findByStartDateBetween              ...where x.startDate between ? and ?
 * LessThan             findByAgeLessThan                   ...where x.age < ?
 * GreaterThan          findByAgeGreaterThan                ...where x.age > ?
 * IsNull               findByAgeIsNull                     ...where x.age is null
 * IsNotNull,NotNull    findByAge(Is)NotNull                ...where x.ahe not null
 * Like                 findByFirstnameLike                 ...where x.firstname like ?
 * NotLike              findByFirstnameNotLike              ...where x.firstname not like ?
 * StartingWith         findByFirstnameStartingWith         ...where x.firstname like %?
 * EndingWith           findByFirstnameEndingWith           ...where x.firstname like ?%
 * Containing           findByFirstnameContaining           ...where x.firstname like %?%
 * OrderBy              findByAgeOrderByLastnameDesc        ...where x.age=? order by x.lastname desc
 * Not                  findBylastnameNot                   ...where x.lastname <> ?
 * In                   findByAgeIn(Collection<Age> c)      ...where x.age in  ?
 * NotIn                findByAgeNotIn(Collection<Age> c)   ...where x.age not in ?
 * True                 findByActiveTrue()                  ...where x.active = true
 * False                findByActiveFalse()                 ...where x.active = false
 * IgnoreCase           findByFirstnameIgnoreCase           ...where UPPER(x.firstame) = UPPER(?1)
 */
@Repository //用于将数据访问层 (DAO 层 ) 的类标识为 Spring Bean
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByName(String name);

    List<Customer> findByAlias(String alias);

    @Query("SELECT c FROM Customer c JOIN c.region r WHERE r.code=:regionCode")
    List<Customer> findByRegion(@Param("regionCode") String regionCode);

    List<Customer> findByIndustry(String industry);

    Page<Customer> findByNameContainingOrAliasContainingAllIgnoringCase(
            String name,
            String alias,
            Pageable pageable
    );

    @Query("SELECT c FROM Customer c JOIN c.region r " +
            " WHERE c.name LIKE %:name% " +
            " OR c.alias LIKE %:alias% " +
            " OR c.industry LIKE %:industry% " +
            " OR r.code LIKE %:region% " +
            " ORDER BY c.name")
    Page<Customer> findAny(
            @Param("name") String name,
            @Param("alias") String alias,
            @Param("industry") String industry,
            @Param("region") String region,
            Pageable pageable
    );

    @Query("SELECT c FROM Customer c JOIN c.region r " +
            " WHERE c.name LIKE %:name% " +
            " OR c.alias LIKE %:alias% " +
            " OR c.industry LIKE %:industry% " +
            " OR r.code LIKE %:region% " +
            " ORDER BY c.name")
    List<Customer> findAny(
            @Param("name") String name,
            @Param("alias") String alias,
            @Param("industry") String industry,
            @Param("region") String region
    );
}
