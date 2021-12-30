package br.roga.appStore.repository;

import br.roga.appStore.domain.App;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AppRepository extends JpaRepository<App, Long> {

    @Query(value = "SELECT * FROM app WHERE " +
            "((:name = '') OR (:name IS NOT NULL AND name = :name)) AND " +
            "((:type = '') OR (:type IS NOT NULL AND type = :type))", nativeQuery = true)
    Page<App> findAllByNameAndType(String name, String type, Pageable pageable);

    @Query(value = "SELECT * FROM app where type = :type ORDER BY price ASC ", nativeQuery = true)
    Page<App> findAllByTypeOrderByLowerPrice(String type, Pageable pageable);


}
