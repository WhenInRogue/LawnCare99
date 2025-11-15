package com.WhenInRogue.LawnCare99.specification;

import com.WhenInRogue.LawnCare99.models.SupplyTransaction;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

//Specification is used in Filtering data in a database
public class SupplyTransactionFilter {


    public static Specification<SupplyTransaction> byFilter(String searchValue) {
        return (root, query, criteriaBuilder) -> {
            //If filter is null or empty, return true for all transactions
            if (searchValue == null || searchValue.isEmpty()) {
                return criteriaBuilder.conjunction(); // Always true
            }

            String searchPattern = "%" + searchValue.toLowerCase() + "%";

            // Create a list to hold all predicates
            List<Predicate> predicates = new ArrayList<>();

            // Check Supply Transaction Fields
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("note")), searchPattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("supplyTransactionType").as(String.class)), searchPattern));


            //Safely join and check user fields using LEFT JOIN
            if (root.getJoins().stream().noneMatch(j -> j.getAttribute().getName().equals("user"))) {
                root.join("user", JoinType.LEFT);
            }
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("user", JoinType.LEFT).get("name")), searchPattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("user", JoinType.LEFT).get("email")), searchPattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("user", JoinType.LEFT).get("phoneNumber")), searchPattern));


            // Safely join and check supply fields using LEFT JOIN
            if (root.getJoins().stream().noneMatch(j -> j.getAttribute().getName().equals("supply"))) {
                root.join("supply", JoinType.LEFT);
            }

            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("supply", JoinType.LEFT).get("name")), searchPattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("supply", JoinType.LEFT).get("unit_of_measurement")), searchPattern)); //potential flaw
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("supply", JoinType.LEFT).get("description")), searchPattern));

            // Combine all predicates with OR
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }



    // New methods for filtering supply transactions by month and year
    public static Specification<SupplyTransaction> byMonthAndYear(int month, int year) {
        return (root, query, criteriaBuilder) -> {
            // Use the month and year functions on the createdAt date field
            Expression<Integer> monthExpression = criteriaBuilder.function("month", Integer.class, root.get("createdAt"));
            Expression<Integer> yearExpression = criteriaBuilder.function("year", Integer.class, root.get("createdAt"));

            // Create predicates for the month and year
            Predicate monthPredicate = criteriaBuilder.equal(monthExpression, month);
            Predicate yearPredicate = criteriaBuilder.equal(yearExpression, year);

            // Combine the month and year predicates
            return criteriaBuilder.and(monthPredicate, yearPredicate);
        };
    }

}
