package com.softeer.backend.fo_domain.user.dto.verification;

import com.softeer.backend.global.common.constant.ValidationConstant;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class ConfirmCodeRequest {

    @Pattern(regexp = ValidationConstant.PHONE_NUMBER_REGEX,
        message = ValidationConstant.PHONE_NUMBER_MSG)
    private String phoneNumber;

    @Pattern(regexp = ValidationConstant.VERIFICATION_CODE_REGEX,
        message = ValidationConstant.VERIFICATION_CODE_MSG)
    private String verificationCode;
}
