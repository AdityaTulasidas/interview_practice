package com.thomsonreuters.metadataregistry.repository;

import com.thomsonreuters.metadataregistry.model.entity.MetaObjectRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface MetaObjectRelationRepository extends JpaRepository<MetaObjectRelation, UUID> {

    @Query("SELECT m.childObject.id FROM MetaObjectRelation m WHERE m.parentObject.id = :metaObjectId")
    List<UUID> findChildByParent(UUID metaObjectId);

    @Query("SELECT m FROM MetaObjectRelation m WHERE m.childObject.id = :childId")
    MetaObjectRelation findByChild(UUID childId);


    Set<MetaObjectRelation> findByParentObjectId(UUID metaObjectId);


}