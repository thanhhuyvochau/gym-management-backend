package spring.project.base.service.Impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import spring.project.base.common.ApiException;
import spring.project.base.common.ApiPage;
import spring.project.base.dto.request.UpdateEquipmentRequest;
import spring.project.base.dto.response.EquipmentResponse;
import spring.project.base.entity.Account;
import spring.project.base.entity.Equipment;
import spring.project.base.repository.EquipmentRepository;
import spring.project.base.service.IEquipmentService;
import spring.project.base.util.account.SecurityUtil;
import spring.project.base.util.mapper.ConvertUtil;
import spring.project.base.util.mapper.PageUtil;
import spring.project.base.validator.EquipmentValidator;


@Service
public class EquipmentServiceImpl implements IEquipmentService {
    private final EquipmentRepository equipmentRepository;

    public EquipmentServiceImpl(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public EquipmentResponse saveEquipment(UpdateEquipmentRequest equipment) {
        Account gymOwner = SecurityUtil.getCurrentUser();
        if (!EquipmentValidator.isExpectedDateValid(equipment.getExpectedDateFrom(), equipment.getExpectedDateTo())) {
            throw ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Date is not validate: ExpectedDateTo must greater than now and ExpectedDateFrom");
        } else if (!EquipmentValidator.isQuantityValid(equipment.getQuantity())) {
            throw ApiException.create(HttpStatus.BAD_REQUEST).withMessage("The quantity must to greater than 0");
        }
        Equipment equipmentEntity = ConvertUtil.convertEquipmentRequestToEntity(equipment);
        equipmentEntity.setGymOwner(gymOwner);
        equipmentEntity = this.equipmentRepository.save(equipmentEntity);
        return ConvertUtil.convertEquipmentToResponse(equipmentEntity);
    }

    @Override
    public EquipmentResponse getEquipmentById(Long id) {
        Account gymOwner = SecurityUtil.getCurrentUser();
        Equipment equipment = this.equipmentRepository.findById(id)
                .orElseThrow(() -> ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Not found equipment with id:" + id));
        if (!EquipmentValidator.isGymOwnerOfEquipment(gymOwner, equipment)) {
            throw ApiException.create(HttpStatus.BAD_REQUEST).withMessage("You not own this equipment");
        }
        return ConvertUtil.convertEquipmentToResponse(equipment);
    }

    @Override
    public ApiPage<EquipmentResponse> getAllEquipment(Pageable pageable) {
        Account gymOwner = SecurityUtil.getCurrentUser();
        Page<Equipment> allEquipment = this.equipmentRepository.findAllByGymOwner_Id(gymOwner.getId(), pageable);
        return PageUtil.convert(allEquipment.map(ConvertUtil::convertEquipmentToResponse));
    }

    @Override
    public boolean deleteEquipment(Long id) {
        Account gymOwner = SecurityUtil.getCurrentUser();
        Equipment equipment = this.equipmentRepository.findById(id)
                .orElseThrow(() -> ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Not found equipment with id:" + id));
        if (!EquipmentValidator.isGymOwnerOfEquipment(gymOwner, equipment)) {
            throw ApiException.create(HttpStatus.BAD_REQUEST).withMessage("You not own this equipment");
        }
        this.equipmentRepository.delete(equipment);
        return true;
    }
}
