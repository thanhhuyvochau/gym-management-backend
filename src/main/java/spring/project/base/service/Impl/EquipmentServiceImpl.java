package spring.project.base.service.Impl;

import io.minio.ObjectWriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import spring.project.base.common.ApiException;
import spring.project.base.common.ApiPage;
import spring.project.base.dto.request.UpdateEquipmentRequest;
import spring.project.base.dto.response.EquipmentResponse;
import spring.project.base.entity.Account;
import spring.project.base.entity.Equipment;
import spring.project.base.repository.EquipmentRepository;
import spring.project.base.service.IEquipmentService;
import spring.project.base.util.account.SecurityUtil;
import spring.project.base.util.adapter.MinioAdapter;
import spring.project.base.util.formater.MiniIOUtil;
import spring.project.base.util.mapper.ConvertUtil;
import spring.project.base.util.mapper.PageUtil;
import spring.project.base.validator.EquipmentValidator;

import java.time.Instant;


@Service
public class EquipmentServiceImpl implements IEquipmentService {
    private static final Logger log = LoggerFactory.getLogger(EquipmentServiceImpl.class);
    private final EquipmentRepository equipmentRepository;
    private final MinioAdapter minioAdapter;
    @Value("${minio.endpoint}")
    private String minioUrl;

    public EquipmentServiceImpl(EquipmentRepository equipmentRepository, MinioAdapter minioAdapter) {
        this.equipmentRepository = equipmentRepository;
        this.minioAdapter = minioAdapter;
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

        if (equipment.getImage() != null && !equipment.getImage().isEmpty()) {
            try {
                MultipartFile file = equipment.getImage();
                String name = equipment.getName() + "_" + Instant.now().toString();
                ObjectWriteResponse objectWriteResponse = minioAdapter.uploadFile(name, file.getContentType(), file.getInputStream(), file.getSize());
                String imageURL = MiniIOUtil.buildUrl(minioUrl, objectWriteResponse);
                equipmentEntity.setImage(imageURL);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
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

    @Override
    public EquipmentResponse editEquipment(Long id, UpdateEquipmentRequest request) {
        Account gymOwner = SecurityUtil.getCurrentUser();
        Equipment equipment = equipmentRepository.findById(id).orElseThrow(() -> ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Not found request with id:" + id));
        if (!EquipmentValidator.isExpectedDateValid(request.getExpectedDateFrom(), request.getExpectedDateTo())) {
            throw ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Date is not validate: ExpectedDateTo must greater than now and ExpectedDateFrom");
        } else if (!EquipmentValidator.isQuantityValid(request.getQuantity())) {
            throw ApiException.create(HttpStatus.BAD_REQUEST).withMessage("The quantity must to greater than 0");
        } else if (!EquipmentValidator.isGymOwnerOfEquipment(gymOwner, equipment)) {
            throw ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Gym owner can not edit this equipment");
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                MultipartFile file = request.getImage();
                String name = request.getName() + "_" + Instant.now().toString();
                ObjectWriteResponse objectWriteResponse = minioAdapter.uploadFile(name, file.getContentType(), file.getInputStream(), file.getSize());
                String imageURL = MiniIOUtil.buildUrl(minioUrl, objectWriteResponse);
                equipment.setImage(imageURL);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        this.equipmentRepository.save(equipment);
        return ConvertUtil.convertEquipmentToResponse(equipment);
    }
}
