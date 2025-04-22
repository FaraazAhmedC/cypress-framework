package com.intelizign.career.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.intelizign.career.model.Job;
import com.intelizign.career.model.EJobStatus;

public class JobSpecification {

    public static Specification<Job> hasJobTitle(String jobTitle) {
        return (root, query, criteriaBuilder) -> 
            jobTitle == null ? null : criteriaBuilder.like(root.get("jobTitle"), "%" + jobTitle + "%");
    }

    public static Specification<Job> hasLocation(String location) {
        return (root, query, criteriaBuilder) -> 
            location == null ? null : criteriaBuilder.like(root.get("location"), "%" + location + "%");
    }

    public static Specification<Job> hasSalaryRange(Double minSalary, Double maxSalary) {
        return (root, query, criteriaBuilder) -> {
            if (minSalary == null && maxSalary == null) return null;
            if (minSalary != null && maxSalary != null) {
                return criteriaBuilder.between(root.get("salary"), minSalary, maxSalary);
            } else if (minSalary != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("salary"), minSalary);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("salary"), maxSalary);
            }
        };
    }

    public static Specification<Job> hasIndustryType(String industryType) {
        return (root, query, criteriaBuilder) -> 
            industryType == null ? null : criteriaBuilder.equal(root.get("industryType"), industryType);
    }

    public static Specification<Job> hasStatus(EJobStatus status) {
        return (root, query, criteriaBuilder) -> 
            status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }
}
