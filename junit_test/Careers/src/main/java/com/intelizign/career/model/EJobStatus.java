package com.intelizign.career.model;

import com.intelizign.career.exception.CustomExceptions;

public enum EJobStatus {
	PENDING, UNDER_REVIEW, INTERVIEW_SCHEDULED, INTERVIEW_COMPLETED, ON_HOLD, SELECTED, APPROVED, REJECTED, JOINING_CONFIRMED;

	public static EJobStatus fromString(String status) {
		for (EJobStatus e : EJobStatus.values()) {
			if (e.name().equalsIgnoreCase(status)) {
				return e;
			}
		}
		throw new CustomExceptions.DuplicateResourceException("Invalid Job Status: " + status);
	}

}