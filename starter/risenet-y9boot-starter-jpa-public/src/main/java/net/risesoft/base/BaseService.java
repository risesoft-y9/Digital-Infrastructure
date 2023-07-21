package net.risesoft.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface BaseService<T, ID, Repository extends JpaRepository<T, ID>> {

    default List<T> findAll() {
        return getRepository().findAll();
    }

    default List<T> findAll(Sort sort) {
        return getRepository().findAll(sort);
    }

    default T getById(ID id) {
        return getRepository().getReferenceById(id);
    }

    default T findById(ID id) {
        return getRepository().findById(id).orElse(null);
    }

    default Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    @Transactional(readOnly = false)
    default T save(T entity) {
        return getRepository().save(entity);
    }

    default boolean existsById(ID id) {
        return getRepository().existsById(id);
    }

    default long count() {
        return getRepository().count();
    }

    @Transactional(readOnly = false)
    default void deleteById(ID id) {
        getRepository().deleteById(id);
    }

    @Transactional(readOnly = false)
    default void delete(T entity) {
        getRepository().delete(entity);
    }

    @Transactional(readOnly = false)
    default void deleteAllById(Iterable<? extends ID> ids) {
        getRepository().deleteAllById(ids);
    }

    @Transactional(readOnly = false)
    default void deleteAll() {
        getRepository().deleteAll();
    }

    Repository getRepository();
}
