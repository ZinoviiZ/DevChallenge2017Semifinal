package com.dev.challenge.repository;

import com.dev.challenge.model.entity.Page;
import com.dev.challenge.model.entity.PageStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Layer of page db processing
 */
@Repository
public interface PageRepository  extends MongoRepository<Page, String> {

    List<Page> findByIdNotIn(List<String> ids);
    List<Page> findByLastScanBeforeAndStatusNot(String twoDaysAgo, PageStatus status);
    List<Page> findByAddDateBetween(String after, String before);
    org.springframework.data.domain.Page<Page> findByStatus(Pageable pageable, PageStatus status);
    List<Page> findByStatus(PageStatus status);
}
