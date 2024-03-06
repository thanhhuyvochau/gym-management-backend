package spring.project.base.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import spring.project.base.dto.request.SaleReportRequest;
import spring.project.base.dto.request.SaleReportResponse;
import spring.project.base.dto.response.MemberResponse;
import spring.project.base.entity.GymPlanRegister;
import spring.project.base.repository.GymPlanRegisterRepository;
import spring.project.base.service.ISaleReportService;
import spring.project.base.util.other.EncryptionUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SaleReportServiceImpl implements ISaleReportService {
    private final GymPlanRegisterRepository gymPlanRegisterRepository;
    private final ObjectMapper objectMapper;

    public SaleReportServiceImpl(GymPlanRegisterRepository gymPlanRegisterRepository, ObjectMapper objectMapper) {
        this.gymPlanRegisterRepository = gymPlanRegisterRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<SaleReportResponse> getSaleReports(SaleReportRequest saleReportRequest) {
        List<GymPlanRegister> reports = this.gymPlanRegisterRepository
                .findByCreatedIsGreaterThanEqualAndCreatedIsLessThanEqual(saleReportRequest.getFromDate(), saleReportRequest.getToDate());
        return reports.stream().map(register -> {
            String decrypt = EncryptionUtils.decrypt(register.getMember().getEncodeMemberData());
            try {
                MemberResponse memberResponse = objectMapper.readValue(decrypt, MemberResponse.class);
                return SaleReportResponse.convertDtoToSaleReport(memberResponse, register.getGymPlan(), register);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
}
