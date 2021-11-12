package sg.therecursiveshepherd.crud.services.mappers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import sg.therecursiveshepherd.crud.configurations.Content;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;
import sg.therecursiveshepherd.crud.entities.Employee;
import sg.therecursiveshepherd.crud.exceptions.DataValidationException;

import javax.validation.Validator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeMapper {

  private final Validator validator;

  public Employee toEntity(EmployeeDto dto) {
    if (dto == null) {
      return null;
    }
    return Employee.builder()
      .id(dto.getId().orElseThrow(() -> new DataValidationException(Content.ERROR_INVALID_ID)))
      .login(dto.getLogin().orElseThrow(() -> new DataValidationException(Content.ERROR_INVALID_LOGIN)))
      .name(dto.getName().orElseThrow(() -> new DataValidationException(Content.ERROR_INVALID_NAME)))
      .salary(dto.getSalary().orElseThrow(() -> new DataValidationException(Content.ERROR_INVALID_SALARY)))
      .startDate(dto.getStartDate().orElseThrow(() -> new DataValidationException(Content.ERROR_INVALID_DATE)))
      .build();
  }

  public Employee toEntity(EmployeeDto dto, Employee entity) {
    if (dto == null) {
      return entity;
    }
    if (entity == null) {
      return toEntity(dto);
    }
    updateEntityField(dto::getId, entity::setId, Content.ERROR_INVALID_ID);
    updateEntityField(dto::getLogin, entity::setLogin, Content.ERROR_INVALID_LOGIN);
    updateEntityField(dto::getName, entity::setName, Content.ERROR_INVALID_NAME);
    updateEntityField(dto::getSalary, entity::setSalary, Content.ERROR_INVALID_SALARY);
    updateEntityField(dto::getStartDate, entity::setStartDate, Content.ERROR_INVALID_DATE);
    return entity;
  }

  public Employee patchEntity(EmployeeDto dto, Employee entity) {
    if (dto == null) {
      return entity;
    }
    var updatedEntity = Objects.requireNonNullElseGet(entity, Employee::new);
    optionallyUpdateEntityField(dto::getId, updatedEntity::setId, dto, EmployeeDto.FIELD_ID, Content.ERROR_INVALID_ID);
    optionallyUpdateEntityField(dto::getLogin, updatedEntity::setLogin, dto, EmployeeDto.FIELD_LOGIN, Content.ERROR_INVALID_LOGIN);
    optionallyUpdateEntityField(dto::getName, updatedEntity::setName, dto, EmployeeDto.FIELD_NAME, Content.ERROR_INVALID_NAME);
    optionallyUpdateEntityField(dto::getSalary, updatedEntity::setSalary, dto, EmployeeDto.FIELD_SALARY, Content.ERROR_INVALID_SALARY);
    optionallyUpdateEntityField(dto::getStartDate, updatedEntity::setStartDate, dto, EmployeeDto.FIELD_START_DATE, Content.ERROR_INVALID_DATE);
    return updatedEntity;
  }

  private <T> void updateEntityField(Supplier<Optional<T>> func, Consumer<T> updateFunc, String errorReason) {
    updateFunc.accept(func.get().orElseThrow(() -> new DataValidationException(errorReason)));
  }

  @SuppressWarnings("OptionalAssignedToNull")
  private <T> void optionallyUpdateEntityField(Supplier<Optional<T>> func, Consumer<T> updateFunc, EmployeeDto dto, String fieldName, String errorReason) {
    if (func.get() != null) { //NOSONAR This optional is used to distinguish between undefined and null in JSON and should not be removed
      var violations = validator.validateProperty(dto, fieldName);
      if (!CollectionUtils.isEmpty(violations)) {
        throw new DataValidationException(errorReason);
      }
      func.get().ifPresentOrElse(updateFunc, () -> updateFunc.accept(null));
    }
  }

  public EmployeeDto toDto(Employee entity) {
    if (entity == null) {
      return null;
    }
    return EmployeeDto.builder()
      .id(Optional.of(entity.getId()))
      .login(Optional.of(entity.getLogin()))
      .name(Optional.of(entity.getName()))
      .salary(Optional.of(entity.getSalary()))
      .startDate(Optional.of(entity.getStartDate()))
      .build();
  }

}
