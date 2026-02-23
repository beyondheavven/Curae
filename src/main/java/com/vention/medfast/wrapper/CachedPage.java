package com.vention.medfast.wrapper;

import java.util.List;

public record CachedPage<T>(List<T> content, int page, int size, long totalElements) {
}
