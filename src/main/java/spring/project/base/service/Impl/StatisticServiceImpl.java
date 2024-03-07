package spring.project.base.service.Impl;

import org.springframework.stereotype.Service;
import spring.project.base.dto.request.StatisticRequest;
import spring.project.base.dto.response.RevenueStatisticResponse;
import spring.project.base.entity.Account;
import spring.project.base.entity.GymPlanRegister;
import spring.project.base.repository.GymPlanRegisterRepository;
import spring.project.base.service.IStatisticService;
import spring.project.base.util.account.SecurityUtil;
import spring.project.base.util.formater.TimeUtil;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class StatisticServiceImpl implements IStatisticService {

    private final GymPlanRegisterRepository gymPlanRegisterRepository;

    public StatisticServiceImpl(GymPlanRegisterRepository gymPlanRegisterRepository) {
        this.gymPlanRegisterRepository = gymPlanRegisterRepository;
    }

    @Override
    public List<RevenueStatisticResponse> getRevenueStatistic(StatisticRequest request) {
        Account gymOwner = SecurityUtil.getCurrentUser();
        Instant fromMonth = TimeUtil.toFirstDayOfMonth(request.getFromMonth()).toInstant();
        Instant toMonth = TimeUtil.toLastDayOfMonth(request.getToMonth()).toInstant();

        List<GymPlanRegister> gymPlanRegisters = gymPlanRegisterRepository
                .findByCreatedIsGreaterThanEqualAndCreatedIsLessThanEqualAndGymPlan_GymOwner(fromMonth, toMonth, gymOwner.getId());

        // Group by day/month/year, ignoring the time
        Map<ZonedDateTime, List<GymPlanRegister>> groupedByDate = gymPlanRegisters.stream()
                .collect(Collectors.groupingBy(
                        gymPlanRegister -> ZonedDateTime.ofInstant(gymPlanRegister.getCreated(), ZoneId.systemDefault())
                                .toLocalDate()
                                .atStartOfDay(ZoneId.systemDefault())
                ));

        List<RevenueStatisticResponse> revenueStatisticResponses = new ArrayList<>();
        groupedByDate.forEach((dateTime, groupedGymPlanRegister) -> {
            RevenueStatisticResponse revenueStatisticResponse = new RevenueStatisticResponse();
            revenueStatisticResponse.setDate(dateTime.toInstant());
            BigDecimal totalRevenue = groupedGymPlanRegister.stream().map(GymPlanRegister::getActualPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            revenueStatisticResponse.setRevenue(totalRevenue);
            revenueStatisticResponses.add(revenueStatisticResponse);
        });

        return revenueStatisticResponses;
    }
}
