package com.boyitong.entity;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class CustomerSpecification {

    public static Specification<Customer> withFilters(
            String city, String area, String category,
            Double minSize, Double maxSize,
            String salesperson, String keyword,
            String assignedTo) {

        return (Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();

            if (StringUtils.hasText(city)) {
                predicate = cb.and(predicate, cb.equal(root.get("city"), city));
            }
            if (StringUtils.hasText(area)) {
                predicate = cb.and(predicate, cb.like(root.get("area"), "%" + area + "%"));
            }
            if (StringUtils.hasText(category)) {
                predicate = cb.and(predicate, cb.like(root.get("category"), "%" + category + "%"));
            }
            if (minSize != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("size"), minSize));
            }
            if (maxSize != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("size"), maxSize));
            }
            if (StringUtils.hasText(salesperson)) {
                predicate = cb.and(predicate, cb.equal(root.get("salesperson"), salesperson));
            }
            if (StringUtils.hasText(keyword)) {
                String pattern = "%" + keyword + "%";
                Predicate addressLike = cb.like(root.get("address"), pattern);
                Predicate remarksLike = cb.like(root.get("remarks"), pattern);
                Predicate phoneLike = cb.like(root.get("phone"), pattern);
                predicate = cb.and(predicate, cb.or(addressLike, remarksLike, phoneLike));
            }
            if (StringUtils.hasText(assignedTo)) {
                predicate = cb.and(predicate, cb.equal(root.get("assignedTo"), assignedTo));
            }

            return predicate;
        };
    }
}