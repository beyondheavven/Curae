package com.vention.curae.wrapper;

import java.util.List;

public record CachedSlice<T>(List<T> content, int page, int size, boolean hasNext) {
}
