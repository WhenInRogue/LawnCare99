package com.WhenInRogue.LawnCare99.services;

import com.WhenInRogue.LawnCare99.dtos.Response;
import com.WhenInRogue.LawnCare99.dtos.SupplyTransactionRequest;

public interface SupplyTransactionService {
    Response checkInSupply(SupplyTransactionRequest supplyTransactionRequest);

    Response checkOutSupply(SupplyTransactionRequest supplyTransactionRequest);

    Response getAllSupplyTransactions(int page, int size, String filter);

    Response getAllSupplyTransactionsById(Long id);

    Response getAllSupplyTransactionsByMonthAndYear(int month, int year);

    //getAllSupplyTransactionsByUser or name ?

}
