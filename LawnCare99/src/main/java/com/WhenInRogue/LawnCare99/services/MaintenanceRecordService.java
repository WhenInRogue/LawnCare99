package com.WhenInRogue.LawnCare99.services;

import com.WhenInRogue.LawnCare99.dtos.MaintenanceRequest;
import com.WhenInRogue.LawnCare99.dtos.Response;

public interface MaintenanceRecordService {

    Response startMaintenance(MaintenanceRequest maintenanceRequest);

    Response endMaintenance(MaintenanceRequest maintenanceRequest);

    Response getMaintenanceRecordsByEquipment(Long equipmentId);

    Response getAllMaintenanceRecords();
}
