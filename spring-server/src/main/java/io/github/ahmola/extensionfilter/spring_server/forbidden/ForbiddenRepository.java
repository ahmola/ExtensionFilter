package io.github.ahmola.extensionfilter.spring_server.forbidden;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForbiddenRepository extends JpaRepository<Forbidden, Long> {
    boolean existsByExtensionName(String extensionName);
    void deleteByExtensionName(String extensionName);

}