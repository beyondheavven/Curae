package com.vention.medfast.search.service;


import com.vention.medfast.search.dto.SearchResponse;

public interface SearchService {

    SearchResponse performGlobalSearch(String query);
}
