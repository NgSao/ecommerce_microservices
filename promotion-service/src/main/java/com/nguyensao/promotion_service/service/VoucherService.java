package com.nguyensao.promotion_service.service;

import com.nguyensao.promotion_service.dto.request.VoucherRequest;
import com.nguyensao.promotion_service.exception.AppException;
import com.nguyensao.promotion_service.model.Voucher;
import com.nguyensao.promotion_service.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;

    @Transactional
    public Voucher createVoucher(VoucherRequest request) {
        // Kiểm tra mã voucher duy nhất
        if (voucherRepository.findByCodeVoucher(request.getCodeVoucher()).isPresent()) {
            throw new AppException("Mã voucher đã tồn tại: " + request.getCodeVoucher());
        }

        // Kiểm tra ngày hợp lệ
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new AppException("Ngày bắt đầu phải trước ngày kết thúc");
        }

        // Kiểm tra giá trị giảm giá
        if (request.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException("Giá trị giảm giá phải lớn hơn 0");
        }

        // Kiểm tra số lần sử dụng tối đa
        if (request.getMaxUsage() <= 0) {
            throw new AppException("Số lần sử dụng tối đa phải lớn hơn 0");
        }

        // Kiểm tra giá trị đơn hàng tối thiểu
        if (request.getMinOrderAmount() != null && request.getMinOrderAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new AppException("Giá trị đơn hàng tối thiểu phải không âm");
        }

        // Tạo voucher
        Voucher voucher = new Voucher();
        voucher.setCodeVoucher(request.getCodeVoucher());
        voucher.setType(request.getType());
        voucher.setValue(request.getValue());
        voucher.setStartDate(request.getStartDate());
        voucher.setEndDate(request.getEndDate());
        voucher.setMinOrderAmount(request.getMinOrderAmount());
        voucher.setUsageCount(0); // Khởi tạo số lần sử dụng
        voucher.setMaxUsage(request.getMaxUsage());
        voucher.setActive(request.isActive());

        return voucherRepository.save(voucher);
    }

    @Transactional
    public boolean applyVoucher(String codeVoucher, BigDecimal orderAmount) {
        Voucher voucher = voucherRepository.findByCodeVoucher(codeVoucher)
                .orElseThrow(() -> new AppException("Không tìm thấy voucher: " + codeVoucher));

        Instant now = Instant.now();

        // Kiểm tra tính hợp lệ của voucher
        if (!voucher.isActive()) {
            throw new AppException("Voucher không hoạt động");
        }
        if (now.isBefore(voucher.getStartDate()) || now.isAfter(voucher.getEndDate())) {
            throw new AppException("Voucher không trong thời gian hiệu lực");
        }
        if (voucher.getUsageCount() >= voucher.getMaxUsage()) {
            throw new AppException("Voucher đã hết lượt sử dụng");
        }
        if (voucher.getMinOrderAmount() != null && orderAmount.compareTo(voucher.getMinOrderAmount()) < 0) {
            throw new AppException("Giá trị đơn hàng không đủ để sử dụng voucher");
        }

        // Tăng số lần sử dụng
        voucher.setUsageCount(voucher.getUsageCount() + 1);
        voucherRepository.save(voucher);

        return true;
    }

    public Voucher getVoucherByCode(String codeVoucher) {
        return voucherRepository.findByCodeVoucher(codeVoucher)
                .orElseThrow(() -> new AppException("Không tìm thấy voucher: " + codeVoucher));
    }

    public boolean isVoucherValid(String codeVoucher, BigDecimal orderAmount) {
        try {
            Voucher voucher = getVoucherByCode(codeVoucher);
            Instant now = Instant.now();
            return voucher.isActive() &&
                    now.isAfter(voucher.getStartDate()) &&
                    now.isBefore(voucher.getEndDate()) &&
                    voucher.getUsageCount() < voucher.getMaxUsage() &&
                    (voucher.getMinOrderAmount() == null || orderAmount.compareTo(voucher.getMinOrderAmount()) >= 0);
        } catch (AppException e) {
            return false;
        }
    }
}