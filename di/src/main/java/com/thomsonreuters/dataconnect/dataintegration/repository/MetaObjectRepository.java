package com.thomsonreuters.dataconnect.dataintegration.repository;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.MetaObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MetaObjectRepository extends JpaRepository<MetaObject, UUID> {
    @Override
    Optional<MetaObject> findById(UUID uuid);
    Optional<MetaObject> findBySystemNameAndOneSourceDomain(String systemName, String oneSourceDomain);
    Optional<MetaObject> findBySystemName(String systemName);

    MetaObject findByDbTable(String string);

    boolean existsBySystemName(String systemName);
}