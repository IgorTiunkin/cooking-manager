package com.phantom.receipt.controllers;

import com.phantom.receipt.controllers.ReceiptController;
import com.phantom.receipt.dto.ReceiptDTO;
import com.phantom.receipt.models.Receipt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ReceiptControllerTest {

    private final ReceiptController receiptController;

    @Autowired
    ReceiptControllerTest(ReceiptController receiptController) {
        this.receiptController = receiptController;
    }

    @Test
    public void whenAll_then1() {
        List<Receipt> allReceipts =receiptController.getAllReceipts();
        assertEquals(1, allReceipts.size());
    }

    @Test
    public void when1_thenSalad() {
        ReceiptDTO receiptById = receiptController.getReceiptById(1);
        assertEquals("Salad", receiptById.getTitle());
    }


}