package com.vention.medfast.statistics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Breakdown of Lab Result statuses")
public record LabResultStatusCounts(
		@Schema(description = "Results currently being processed", example = "2") long processing,
		@Schema(description = "Results ready for download", example = "15") long ready,
		@Schema(description = "Results that failed processing", example = "0") long failed) {
}
