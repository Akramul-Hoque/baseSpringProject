package org.averdev.basepeoject.common.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BaseService<T, ID> {
    
    T save(T entity);
    
    Optional<T> findById(ID id);
    
    List<T> findAll();
    
    Page<T> findAll(Pageable pageable);
    
    T update(ID id, T entity);
    
    void deleteById(ID id);
    
    boolean existsById(ID id);
    
    long count();
}
