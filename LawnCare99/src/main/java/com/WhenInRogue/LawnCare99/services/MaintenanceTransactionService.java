package com.WhenInRogue.LawnCare99.services;

import com.WhenInRogue.LawnCare99.dtos.MaintenanceTransactionRequest;
import com.WhenInRogue.LawnCare99.dtos.Response;

public interface MaintenanceTransactionService {

    Response startMaintenance(MaintenanceTransactionRequest maintenanceTransactionRequest);

    Response completeMaintenance(MaintenanceTransactionRequest maintenanceTransactionRequest);

    Response getMaintenanceTransactionsByEquipment(Long equipmentId);
}
