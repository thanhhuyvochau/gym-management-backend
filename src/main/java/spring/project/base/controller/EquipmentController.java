package spring.project.base.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spring.project.base.common.ApiPage;
import spring.project.base.common.ApiResponse;
import spring.project.base.dto.request.UpdateEquipmentRequest;
import spring.project.base.dto.response.EquipmentResponse;
import spring.project.base.service.IEquipmentService;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/equipments")
@Validated
public class EquipmentController {

    private final IEquipmentService equipmentService;

    public EquipmentController(IEquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EquipmentResponse>> addEquipment(@Valid @ModelAttribute UpdateEquipmentRequest equipment) {
        // Validation will be automatically triggered due to @Valid annotation
        return ResponseEntity.ok(ApiResponse.success(equipmentService.saveEquipment(equipment)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EquipmentResponse>> getEquipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(equipmentService.getEquipmentById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ApiPage<EquipmentResponse>>> getAllEquipment(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(equipmentService.getAllEquipment(pageable)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteEquipment(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(equipmentService.deleteEquipment(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EquipmentResponse>> editEquipment(@PathVariable Long id, @Valid @ModelAttribute UpdateEquipmentRequest equipment) {
        // Validation will be automatically triggered due to @Valid annotation
        return ResponseEntity.ok(ApiResponse.success(equipmentService.editEquipment(id, equipment)));
    }

    // Other CRUD operations as needed
}
