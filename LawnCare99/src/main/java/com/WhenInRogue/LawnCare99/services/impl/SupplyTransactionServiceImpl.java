package com.WhenInRogue.LawnCare99.services.impl;

import com.WhenInRogue.LawnCare99.dtos.Response;
import com.WhenInRogue.LawnCare99.dtos.SupplyTransactionDTO;
import com.WhenInRogue.LawnCare99.dtos.SupplyTransactionRequest;
import com.WhenInRogue.LawnCare99.enums.SupplyTransactionType;
import com.WhenInRogue.LawnCare99.exceptions.NotFoundException;
import com.WhenInRogue.LawnCare99.models.Supply;
import com.WhenInRogue.LawnCare99.models.SupplyTransaction;
import com.WhenInRogue.LawnCare99.models.User;
import com.WhenInRogue.LawnCare99.repositories.SupplyRepository;
import com.WhenInRogue.LawnCare99.repositories.SupplyTransactionRepository;
import com.WhenInRogue.LawnCare99.services.SupplyTransactionService;
import com.WhenInRogue.LawnCare99.services.UserService;
import com.WhenInRogue.LawnCare99.specification.SupplyTransactionFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SupplyTransactionServiceImpl implements SupplyTransactionService {

    private final SupplyTransactionRepository supplyTransactionRepository;
    private final SupplyRepository supplyRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public Response checkInSupply(SupplyTransactionRequest supplyTransactionRequest) {

        Long supplyId = supplyTransactionRequest.getSupplyId();
        Integer quantity = supplyTransactionRequest.getQuantity();

        Supply supply = supplyRepository.findById(supplyId)
                .orElseThrow(() -> new NotFoundException("Supply Not Found"));

        User user = userService.getCurrentLoggedInUser();

        // validate quantity
        if (quantity == null || quantity <= 0) {
            return Response.builder()
                    .status(400)
                    .message("Quantity must be greater than 0")
                    .build();
        }

        // ensure currentStock and maximumQuantity are not null (defensive; maximumQuantity is @NotNull on entity)
        int current = (supply.getCurrentStock() == null) ? 0 : supply.getCurrentStock();
        Integer maxQty = supply.getMaximumQuantity();
        if (maxQty == null) {
            // fallback — if max not set, allow the check-in (or you could treat this as an error)
            supply.setCurrentStock(current + quantity);
            supplyRepository.save(supply);
        } else {
            long newStock = (long) current + (long) quantity; // use long to avoid overflow
            if (newStock > maxQty) {
                return Response.builder()
                        .status(400)
                        .message("Check-in would exceed maximum quantity. Available space: " + (maxQty - current))
                        .build();
            }
            // safe to update stock quantity and resave
            supply.setCurrentStock(current + quantity);
            supplyRepository.save(supply);
        }


        //create a supply transaction
        SupplyTransaction supplyTransaction = SupplyTransaction.builder()
                .supplyTransactionType(SupplyTransactionType.CHECK_IN)
                .supply(supply)
                .user(user)
                .quantity(quantity)
                .note(supplyTransactionRequest.getNote())
                .build();

        supplyTransactionRepository.save(supplyTransaction);
        return Response.builder()
                .status(200)
                .message("Check In Successful")
                .build();
    }

    @Override
    public Response checkOutSupply(SupplyTransactionRequest supplyTransactionRequest) {

        Long supplyId = supplyTransactionRequest.getSupplyId();
        Integer quantity = supplyTransactionRequest.getQuantity();

        Supply supply = supplyRepository.findById(supplyId)
                .orElseThrow(() -> new NotFoundException("Supply Not Found"));

        User user = userService.getCurrentLoggedInUser();

        // guard: prevent negative stock
        if (quantity == null || quantity <= 0) {
            return Response.builder()
                    .status(400)
                    .message("Quantity must be greater than 0")
                    .build();
        }

        if (supply.getCurrentStock() == null || quantity > supply.getCurrentStock()) {
            return Response.builder()
                    .status(400)
                    .message("Insufficient stock")
                    .build();
        }

        //safe to update stock quantity and re-save
        supply.setCurrentStock(supply.getCurrentStock() - quantity);
        supplyRepository.save(supply);


        //create a supply transaction
        SupplyTransaction supplyTransaction = SupplyTransaction.builder()
                .supplyTransactionType(SupplyTransactionType.CHECK_OUT)
                .supply(supply)
                .user(user)
                .quantity(quantity)
                .note(supplyTransactionRequest.getNote())
                .build();

        supplyTransactionRepository.save(supplyTransaction);
        return Response.builder()
                .status(200)
                .message("Check Out Successful")
                .build();
    }

    @Override
    public Response getAllSupplyTransactions(int page, int size, String filter) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "supplyTransactionId"));

        //user the Transaction specification
        Specification<SupplyTransaction> spec = SupplyTransactionFilter.byFilter(filter);
        Page<SupplyTransaction> supplyTransactionPage = supplyTransactionRepository.findAll(spec, pageable);

        List<SupplyTransactionDTO> supplyTransactionDTOS = modelMapper.map(supplyTransactionPage.getContent(), new TypeToken<List<SupplyTransactionDTO>>() {
        }.getType());

        supplyTransactionDTOS.forEach(supplyTransactionDTO -> {
            supplyTransactionDTO.setUser(null);
            supplyTransactionDTO.setSupply(null);
        });

        return Response.builder()
                .status(200)
                .message("success")
                .supplyTransactions(supplyTransactionDTOS)
                .totalElements(supplyTransactionPage.getTotalElements())
                .totalPages(supplyTransactionPage.getTotalPages())
                .build();
    }

    @Override
    public Response getAllSupplyTransactionsById(Long id) {
        SupplyTransaction supplyTransaction = supplyTransactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction Not Found"));

        SupplyTransactionDTO supplyTransactionDTO = modelMapper.map(supplyTransaction, SupplyTransactionDTO.class);

        supplyTransactionDTO.getUser().setSupplyTransactions(null);
        supplyTransactionDTO.getUser().setEquipmentTransactions(null);

        return Response.builder()
                .status(200)
                .message("success")
                .supplyTransaction(supplyTransactionDTO)
                .build();
    }

    @Override
    public Response getAllSupplyTransactionsByMonthAndYear(int month, int year) {
        List<SupplyTransaction> supplyTransactions = supplyTransactionRepository.findAll(SupplyTransactionFilter.byMonthAndYear(month, year));

        List<SupplyTransactionDTO> supplyTransactionDTOS = modelMapper.map(supplyTransactions, new TypeToken<List<SupplyTransactionDTO>>() {
        }.getType());

        supplyTransactionDTOS.forEach(supplyTransactionDTO -> {
            supplyTransactionDTO.setUser(null);
            supplyTransactionDTO.setSupply(null);
        });

        return Response.builder()
                .status(200)
                .message("success")
                .supplyTransactions(supplyTransactionDTOS)
                .build();
    }
}
