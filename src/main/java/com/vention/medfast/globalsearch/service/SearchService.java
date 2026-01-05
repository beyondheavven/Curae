package com.vention.medfast.globalsearch.service;

import com.vention.medfast.globalsearch.dto.SearchRequest;
import com.vention.medfast.globalsearch.dto.SearchResponseDto;

public interface SearchService {

	SearchResponseDto performGlobalSearch(SearchRequest request);

}
