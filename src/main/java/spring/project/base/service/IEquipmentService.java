package spring.project.base.service;

import org.springframework.data.domain.Pageable;
import spring.project.base.common.ApiPage;
import spring.project.base.dto.request.UpdateEquipmentRequest;
import spring.project.base.dto.response.EquipmentResponse;

public interface IEquipmentService {

    EquipmentResponse saveEquipment(UpdateEquipmentRequest equipment);

    EquipmentResponse getEquipmentById(Long id);

    ApiPage<EquipmentResponse> getAllEquipment(Pageable pageable);

    boolean deleteEquipment(Long id);

    EquipmentResponse editEquipment(Long id, UpdateEquipmentRequest equipment);

}
