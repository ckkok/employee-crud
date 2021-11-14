package sg.therecursiveshepherd.crud.services.mappers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;
import sg.therecursiveshepherd.crud.entities.Employee;
import sg.therecursiveshepherd.crud.exceptions.EmployeeNotFoundException;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeMapper {

  public Employee toEntity(EmployeeDto dto) {
    if (dto == null) {
      return null;
    }
    return Employee.builder()
      .id(dto.getId())
      .login(dto.getLogin())
      .name(dto.getName())
      .salary(dto.getSalary())
      .startDate(dto.getStartDate())
      .build();
  }

  public Employee toEntity(EmployeeDto dto, Employee entity) {
    if (dto == null) {
      return entity;
    }
    if (entity == null) {
      return toEntity(dto);
    }
    updateEntityField(dto::getId, entity::setId);
    updateEntityField(dto::getLogin, entity::setLogin);
    updateEntityField(dto::getName, entity::setName);
    updateEntityField(dto::getSalary, entity::setSalary);
    updateEntityField(dto::getStartDate, entity::setStartDate);
    return entity;
  }

  public Employee patchEntity(EmployeeDto dto, Employee entity) {
    if (entity == null) {
      throw new EmployeeNotFoundException();
    }
    if (dto == null) {
      return entity;
    }
    var updatedEntity = Objects.requireNonNullElseGet(entity, Employee::new);
    updateEntityFieldIfDtoFieldNotNull(dto::getId, updatedEntity::setId);
    updateEntityFieldIfDtoFieldNotNull(dto::getLogin, updatedEntity::setLogin);
    updateEntityFieldIfDtoFieldNotNull(dto::getName, updatedEntity::setName);
    updateEntityFieldIfDtoFieldNotNull(dto::getSalary, updatedEntity::setSalary);
    updateEntityFieldIfDtoFieldNotNull(dto::getStartDate, updatedEntity::setStartDate);
    return updatedEntity;
  }

  public EmployeeDto toDto(Employee entity) {
    if (entity == null) {
      return null;
    }
    return EmployeeDto.builder()
      .id(entity.getId())
      .login(entity.getLogin())
      .name(entity.getName())
      .salary(entity.getSalary())
      .startDate(entity.getStartDate())
      .build();
  }

  private <T> void updateEntityField(Supplier<T> func, Consumer<T> updateFunc) {
    updateFunc.accept(func.get());
  }

  private <T> void updateEntityFieldIfDtoFieldNotNull(Supplier<T> func, Consumer<T> updateFunc) {
    if (func.get() != null) {
      updateEntityField(func, updateFunc);
    }
  }

}
