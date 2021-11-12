package sg.therecursiveshepherd.crud.utils;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;
import java.util.Optional;

public class RangeQuery implements Pageable {

  private final int offset;
  private final int page;
  private final int size;
  private final Sort sort;

  private RangeQuery(int offset, int page, int size, Sort sort) {
    Objects.requireNonNull(sort, "Sort must not be null");
    this.offset = offset;
    this.page = page;
    this.size = size == 0 ? Integer.MAX_VALUE : size;
    this.sort = sort;
  }

  public static RangeQuery of(int offset, int page, int size) {
    return new RangeQuery(offset, page, size, Sort.unsorted());
  }

  public static RangeQuery of(int offset, int page, int size, Sort sort) {
    return new RangeQuery(offset, page, size, sort);
  }

  @Override
  public boolean isPaged() {
    return Pageable.super.isPaged();
  }

  @Override
  public boolean isUnpaged() {
    return Pageable.super.isUnpaged();
  }

  @Override
  public int getPageNumber() {
    return page;
  }

  @Override
  public int getPageSize() {
    return size;
  }

  @Override
  public long getOffset() {
    return offset + ((long) page * size);
  }

  @Override
  public Sort getSort() {
    return sort;
  }

  @Override
  public Sort getSortOr(Sort sort) {
    return Pageable.super.getSortOr(sort);
  }

  @Override
  public Pageable next() {
    return of(offset, page + 1, size);
  }

  @Override
  public Pageable previousOrFirst() {
    return hasPrevious() ? of(offset, page - 1, size) : first();
  }

  @Override
  public Pageable first() {
    return of(offset, 0, size);
  }

  @Override
  public Pageable withPage(int pageNumber) {
    return of(offset, pageNumber, size);
  }

  @Override
  public boolean hasPrevious() {
    return page > 0;
  }

  @Override
  public Optional<Pageable> toOptional() {
    return Pageable.super.toOptional();
  }
}
