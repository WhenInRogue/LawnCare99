package com.WhenInRogue.LawnCare99.services;

import com.WhenInRogue.LawnCare99.dtos.Response;

public interface MaintenanceRecordService {

    Response getMaintenanceRecordsByEquipment(Long equipmentId);

    Response getAllMaintenanceRecords();
}
