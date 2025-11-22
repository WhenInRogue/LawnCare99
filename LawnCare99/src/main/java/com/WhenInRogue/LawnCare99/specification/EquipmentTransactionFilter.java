package com.WhenInRogue.LawnCare99.specification;

import com.WhenInRogue.LawnCare99.models.EquipmentTransaction;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class EquipmentTransactionFilter {


    public static Specification<EquipmentTransaction> equipmentTransactionFilter(String searchValue) {
        return (root, query, criteriaBuilder) -> {
            //If filter is null or empty, return true for all transactions
            if (searchValue == null || searchValue.isEmpty()) {
                return criteriaBuilder.conjunction(); //Always true
            }

            String searchPattern = "%" + searchValue.toLowerCase() + "%";

            // create a list to hold all predicates
            List<Predicate> predicates = new ArrayList<>();

            //check equipment transaction fields
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("note")), searchPattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("equipmentTransactionType").as(String.class)), searchPattern));


            //Safely join and check user fields using LEFT JOIN
            if (root.getJoins().stream().noneMatch(j -> j.getAttribute().getName().equals("user"))) {
                root.join("user", JoinType.LEFT);
            }
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("user", JoinType.LEFT).get("name")), searchPattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("user", JoinType.LEFT).get("email")), searchPattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("user", JoinType.LEFT).get("phoneNumber")), searchPattern));


            //Safely join and check equipment fields using LEFT JOIN
            if (root.getJoins().stream().noneMatch(j -> j.getAttribute().getName().equals("equipment"))) {
                root.join("equipment", JoinType.LEFT);
            }

            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("equipment", JoinType.LEFT).get("name")), searchPattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("equipment", JoinType.LEFT).get("description")), searchPattern));

            //Combine all predicates with OR
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));

        };
    }


    //New methods for filtering equipment transactions by month and year
    public static Specification<EquipmentTransaction> byMonthAndYear(int month, int year) {
        return (root, query, criteriaBuilder) -> {
            // Use the month and year functions on the timestamp date field
            Expression<Integer> monthExpression = criteriaBuilder.function("month", Integer.class, root.get("timestamp"));
            Expression<Integer> yearExpression = criteriaBuilder.function("year", Integer.class, root.get("timestamp"));

            // Create predicates for the month and year
            Predicate monthPredicate = criteriaBuilder.equal(monthExpression, month);
            Predicate yearPredicate = criteriaBuilder.equal(yearExpression, year);

            // Combine the month and year predicates
            return criteriaBuilder.and(monthPredicate, yearPredicate);
        };
    }

}
