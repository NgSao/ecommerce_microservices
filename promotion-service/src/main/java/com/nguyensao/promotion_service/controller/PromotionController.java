package com.nguyensao.promotion_service.controller;

import com.nguyensao.promotion_service.dto.request.PromotionRequest;
import com.nguyensao.promotion_service.dto.request.VoucherRequest;
import com.nguyensao.promotion_service.model.Promotion;
import com.nguyensao.promotion_service.model.Voucher;
import com.nguyensao.promotion_service.service.PromotionService;
import com.nguyensao.promotion_service.service.VoucherService;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    private final PromotionService promotionService;
    private final VoucherService voucherService;

    public PromotionController(PromotionService promotionService, VoucherService voucherService) {
        this.promotionService = promotionService;
        this.voucherService = voucherService;
    }

    @PostMapping("/admin")
    public ResponseEntity<Promotion> createPromotion(@RequestBody PromotionRequest request) {
        return ResponseEntity.ok(promotionService.createPromotion(request));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<Promotion> getPromotion(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getPromotion(id));
    }

    @GetMapping("/public/{id}/valid")
    public ResponseEntity<Boolean> isPromotionValid(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.isPromotionValid(id));
    }

    //

    @PostMapping("/admin/vouchers")
    public ResponseEntity<Voucher> createVoucher(@RequestBody VoucherRequest request) {
        return ResponseEntity.ok(voucherService.createVoucher(request));
    }

    @GetMapping("/public/vouchers/{code}")
    public ResponseEntity<Voucher> getVoucherByCode(@PathVariable String code) {
        return ResponseEntity.ok(voucherService.getVoucherByCode(code));
    }

    @PostMapping("/public/vouchers/{code}/apply")
    public ResponseEntity<String> applyVoucher(@PathVariable String code, @RequestParam BigDecimal orderAmount) {
        voucherService.applyVoucher(code, orderAmount);
        return ResponseEntity.ok("Voucher áp dụng thành công");
    }

    @GetMapping("/public/vouchers/{code}/valid")
    public ResponseEntity<Boolean> isVoucherValid(@PathVariable String code, @RequestParam BigDecimal orderAmount) {
        return ResponseEntity.ok(voucherService.isVoucherValid(code, orderAmount));
    }
}