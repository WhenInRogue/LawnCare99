package com.WhenInRogue.LawnCare99.services;

import com.WhenInRogue.LawnCare99.dtos.EquipmentTransactionRequest;
import com.WhenInRogue.LawnCare99.dtos.Response;

public interface EquipmentTransactionService {

    Response checkInEquipment(EquipmentTransactionRequest equipmentTransactionRequest);

    Response checkOutEquipment(EquipmentTransactionRequest equipmentTransactionRequest);

    Response getAllEquipmentTransactions(int page, int size, String filter);

    Response getAllEquipmentTransactionsById(Long id);

    Response getAllEquipmentTransactionsByMonthAndYear(int month, int year);

}
