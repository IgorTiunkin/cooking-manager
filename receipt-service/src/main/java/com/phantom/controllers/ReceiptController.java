package com.phantom.controllers;

import com.phantom.dto.ReceiptDTO;
import com.phantom.models.Receipt;
import com.phantom.services.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/receipt")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;
    private final ModelMapper modelMapper;

    @GetMapping("/all")
    public List<Receipt> getAllReceipts() {
        return receiptService.getAllReceipts();
    }

    @GetMapping("/one")
    public ReceiptDTO getReceiptById(@RequestParam ("receiptId") Integer receiptId) {
        Receipt receiptById = receiptService.getReceiptById(receiptId);
        return convertToReceiptDto(receiptById);
    }

    @PostMapping("/save")
    public ReceiptDTO saveNewReceipt(@RequestBody ReceiptDTO receiptDTO) {
        Receipt receipt = convertToReceipt(receiptDTO);
        boolean saved = receiptService.save(receipt);
        if (saved) {
            return receiptDTO;
        } else {
            throw new RuntimeException("saved failure");//todo custom exception
        }
    }

    @DeleteMapping("/delete")
    public ReceiptDTO deleteReceipt(@RequestBody ReceiptDTO receiptDTO) {
        Receipt receipt = convertToReceipt(receiptDTO);
        boolean delete = receiptService.delete(receipt);
        if (delete) {
            return receiptDTO;
        } else {
            throw new RuntimeException("delete failure");//todo custom exception
        }

    }

    private Receipt convertToReceipt(ReceiptDTO receiptDTO) {
        return modelMapper.map(receiptDTO, Receipt.class);
    }

    private ReceiptDTO convertToReceiptDto(Receipt receipt) {
        return modelMapper.map(receipt, ReceiptDTO.class);
    }


}
