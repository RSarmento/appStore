package br.roga.appStore.repository;

import br.roga.appStore.domain.App;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AppRepository extends JpaRepository<App, Long> {

    Page<App> findAllByNameAndType(String name, String type, Pageable pageable);

    @Query(value = "SELECT * FROM app where type = :type ORDER BY price ASC ", nativeQuery = true)
    Page<App> findAllByTypeOrderByLowerPrice(String type, Pageable pageable);

}
