package com.abctech.repository;

import com.abctech.model.BTBlogEntry;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BlogEntryRepository extends PagingAndSortingRepository<BTBlogEntry, String> {
}