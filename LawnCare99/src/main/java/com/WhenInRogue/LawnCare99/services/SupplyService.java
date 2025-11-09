package com.WhenInRogue.LawnCare99.services;

import com.WhenInRogue.LawnCare99.dtos.Response;
import com.WhenInRogue.LawnCare99.dtos.SupplyDTO;

public interface SupplyService {

    //Create
    Response createSupply(SupplyDTO supplyDTO);

    //Read
    Response getAllSupplies();

    Response getSupplyById(Long id);

    //Update
    Response updateSupply(Long id, SupplyDTO supplyDTO);

    //Delete
    Response deleteSupply(Long id);
}
