package com.thomsonreuters.dataconnect.dataintegration.repository;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.MetaObjectRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

/**
 * Repository to query relations between meta objects.
 * Used to determine if a given MetaObject is a child (has a parent entry).
 */
@Repository
public interface MetaObjectRelationRepository extends JpaRepository<MetaObjectRelation, UUID> {

    /**
     * Returns true if the supplied meta object id appears as a child in any relation.
     * (Column path uses Spring Data derived property navigation.)
     */
    boolean existsByChildObject_Id(UUID childId);

    Optional<MetaObjectRelation> findByChildObject_Id(UUID childId);
}
