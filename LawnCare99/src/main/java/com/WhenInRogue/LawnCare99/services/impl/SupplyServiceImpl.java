package com.WhenInRogue.LawnCare99.services.impl;

import com.WhenInRogue.LawnCare99.dtos.Response;
import com.WhenInRogue.LawnCare99.dtos.SupplyDTO;
import com.WhenInRogue.LawnCare99.exceptions.NotFoundException;
import com.WhenInRogue.LawnCare99.models.Supply;
import com.WhenInRogue.LawnCare99.repositories.SupplyRepository;
import com.WhenInRogue.LawnCare99.services.SupplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


//@Service marks classes that provide business functionalities within the application's service layer.
@Service
@RequiredArgsConstructor
@Slf4j
public class SupplyServiceImpl implements SupplyService {

    private final ModelMapper modelMapper;
    private final SupplyRepository supplyRepository;

    @Override
    public Response createSupply(SupplyDTO supplyDTO) {

        validateStockDoesNotExceedMaximum(supplyDTO.getCurrentStock(), supplyDTO.getMaximumQuantity());

        Supply supplyToSave = modelMapper.map(supplyDTO, Supply.class);

        supplyRepository.save(supplyToSave);

        return Response.builder()
                .status(200)
                .message("Supply created successfully")
                .build();
    }

    @Override
    public Response getAllSupplies() {
                                                                                                //supplyId or id? <--fixed
        List<Supply> supplies = supplyRepository.findAll(Sort.by(Sort.Direction.DESC, "supplyId"));

        List<SupplyDTO> SupplyDTOList = modelMapper.map(supplies, new TypeToken<List<SupplyDTO>>() {
        }.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .supplies(SupplyDTOList)
                .build();
    }

    @Override
    public Response getSupplyById(Long id) {
                                                //Does this need to be supplyId? <-- answer no
        Supply supply = supplyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Supply not found"));

        SupplyDTO supplyDTO = modelMapper.map(supply, SupplyDTO.class);

        return Response.builder()
                .status(200)
                .message("success")
                .supply(supplyDTO)
                .build();
    }

    @Override
    public Response updateSupply(Long id, SupplyDTO supplyDTO) {

        Supply existingSupply = supplyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Supply not found"));

        if (supplyDTO.getName() != null) existingSupply.setName(supplyDTO.getName());
        if (supplyDTO.getUnitOfMeasurement() != null) existingSupply.setUnitOfMeasurement(supplyDTO.getUnitOfMeasurement());
        if (supplyDTO.getReorderLevel() != null) existingSupply.setReorderLevel(supplyDTO.getReorderLevel());
        if (supplyDTO.getDescription() != null) existingSupply.setDescription(supplyDTO.getDescription());

        Integer updatedCurrentStock = supplyDTO.getCurrentStock() != null
                ? supplyDTO.getCurrentStock()
                : existingSupply.getCurrentStock();
        Integer updatedMaximumQuantity = supplyDTO.getMaximumQuantity() != null
                ? supplyDTO.getMaximumQuantity()
                : existingSupply.getMaximumQuantity();

        validateStockDoesNotExceedMaximum(updatedCurrentStock, updatedMaximumQuantity);

        existingSupply.setCurrentStock(updatedCurrentStock);
        existingSupply.setMaximumQuantity(updatedMaximumQuantity);

        supplyRepository.save(existingSupply);

        return Response.builder()
                .status(200)
                .message("Supply updated successfully")
                .build();
    }

    @Override
    public Response deleteSupply(Long id) {

        supplyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Supply not found"));

        supplyRepository.deleteById(id);

        return Response.builder()
                .status(200)
                .message("Supply deleted successfully")
                .build();
    }

    private void validateStockDoesNotExceedMaximum(Integer currentStock, Integer maximumQuantity) {
        if (maximumQuantity == null) {
            return;
        }

        int normalizedStock = currentStock == null ? 0 : currentStock;

        if (normalizedStock > maximumQuantity) {
            throw new IllegalArgumentException("Current stock cannot exceed maximum quantity");
        }
    }
}
