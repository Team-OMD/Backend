package com.onmydesk.backend.setup.service;

import com.onmydesk.backend.error.errorcode.SetupErrorCode;
import com.onmydesk.backend.error.exception.RestApiException;
import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.setup.domain.Setup;
import com.onmydesk.backend.setup.repository.SetupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SetupValidator {

    private final SetupRepository setupRepository;

    public Setup validateSetupOwnership(Long setupId, Member member) {
        Setup setup = setupRepository.findById(setupId)
                .orElseThrow(() -> new RestApiException(SetupErrorCode.SETUP_NOT_FOUND));
        if (!setup.getMember().equals(member)) {
            throw new RestApiException(SetupErrorCode.NO_PERMISSION);
        }
        return setup;
    }
}
