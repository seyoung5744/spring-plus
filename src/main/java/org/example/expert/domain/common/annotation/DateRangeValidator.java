package org.example.expert.domain.common.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return true; // null인 경우는 유효한 것으로 처리
        }

        try {
            LocalDate startDate = (LocalDate) object.getClass().getMethod("startDate").invoke(object);
            LocalDate endDate = (LocalDate) object.getClass().getMethod("endDate").invoke(object);

            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                context.disableDefaultConstraintViolation(); // 기본 에러 메시지 비활성화
                context.buildConstraintViolationWithTemplate("시작일이 종료일보다 늦으면 안됩니다.")
                        .addPropertyNode("startDate") // 오류가 발생한 필드 지정
                        .addConstraintViolation();
                return false;
            }
        } catch (Exception e) {
            return false; // 예외 처리
        }
        return true;
    }
}
