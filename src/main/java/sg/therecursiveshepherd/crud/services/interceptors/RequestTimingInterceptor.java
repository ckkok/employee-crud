package sg.therecursiveshepherd.crud.services.interceptors;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;

@Slf4j
@Component
@NoArgsConstructor
public class RequestTimingInterceptor implements HandlerInterceptor {

  private static final String REQUEST_START_TIME_ATTRIBUTE = "startTime";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    log.info("Request ({}): {} {}", request.getRemoteAddr(), request.getMethod(), request.getRequestURI());
    request.setAttribute(REQUEST_START_TIME_ATTRIBUTE, Instant.now().toEpochMilli());
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
    long startTime = (long) request.getAttribute(REQUEST_START_TIME_ATTRIBUTE);
    long elapsedTime = Instant.now().toEpochMilli() - startTime;
    log.info("Response ({}ms): {} {} {}", elapsedTime, request.getMethod(), request.getRequestURI(), response.getStatus());
  }

}
