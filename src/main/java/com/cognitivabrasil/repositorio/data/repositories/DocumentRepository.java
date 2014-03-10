/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.data.repositories;

import com.cognitivabrasil.repositorio.data.entities.Document;
import com.cognitivabrasil.repositorio.data.entities.Subject;
import com.cognitivabrasil.repositorio.data.entities.User;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * <b>ATENTION!</b>
 *
 * Beware the inconsistency between from and until ({@link #from(DateTime, Pageable)},
 * {@link #countFrom(DateTime)}, {@link #until()}, {@link #countUntil(DateTime)},
 * {@link #betweenInclusive(DateTime, DateTime, Pageable)},
 * {@link #countBetweenInclusive(DateTime, DateTime)} in this repository.
 *
 * From is inclusive, until is NOT inclusive. This is necessary for us to ignore
 * the milliseconds portion of the timestamp.
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 * @author Paulo Schreiner <paulo@cognitivabrasil.com.br>
 */
public interface DocumentRepository extends JpaRepository<Document, Integer> {

    @Override
    public List<Document> findAll();

    public List<Document> findBySubjectAndDeletedIsFalseAndObaaXmlNotNullOrderByCreatedDesc(Subject s);

    public Page<Document> findBySubjectAndDeletedIsFalseAndObaaXmlNotNullOrderByCreatedDesc(Subject s, Pageable pageable);

    public List<Document> findByDeletedIsFalseAndObaaXmlNotNullOrderByCreatedDesc();

    public Page<Document> findByDeletedIsFalseAndObaaXmlNotNullOrderByCreatedDesc(Pageable pageable);

    public Document findByObaaEntry(String entry);

    public List<Document> findByCreatedLessThanAndActiveIsFalse(DateTime d);

    public long countByOwnerAndDeletedIsFalseAndActiveIsTrue(User u);
    
    // Hack: we add one second to the date and use non-inclusive comparison for until, and 
    // use incluse queries for from, in this way we ignore fractions of seconds.
    @Query("SELECT d FROM Document d WHERE created >= ?1 AND active is true")
    public Page<Document> from(DateTime dateTime, Pageable p);

    @Query("SELECT count(*) FROM Document d WHERE created >= ?1 AND active is true")
    public Integer countFrom(DateTime dateTime);

    @Query("SELECT d FROM Document d WHERE created < ?1 AND active is true")
    public Page<Document> until(DateTime dateTime, Pageable p);

    @Query("SELECT count(*) FROM Document d WHERE created < ?1 AND active is true")
    public Integer countUntil(DateTime dateTime);

    @Query("SELECT d FROM Document d WHERE created >= ?1 AND created < ?2 AND active is true")
    public Page<Document> betweenInclusive(DateTime from, DateTime until, Pageable p);

    @Query("SELECT count(*) FROM Document d WHERE created >= ?1 AND created < ?2 AND active is true")
    public Integer countBetweenInclusive(DateTime from, DateTime until);

    @Query("SELECT d FROM Document d WHERE active is true")
    public Page<Document> all(Pageable pageable);

    @Query("SELECT count(*) FROM Document d WHERE active is true")
    public Integer countActiveTrue();
    
    @Query("SELECT count(*) FROM Document d WHERE active is true and deleted is false")
    public long countActiveTrueDeletedFalse();
}
