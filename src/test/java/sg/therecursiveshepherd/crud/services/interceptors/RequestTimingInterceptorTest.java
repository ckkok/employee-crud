package sg.therecursiveshepherd.crud.services.interceptors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.spy;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Instant;

class RequestTimingInterceptorTest {

  private static final String REQUEST_START_TIME_ATTRIBUTE = "startTime";

  @Test
  void preHandle() {
    var interceptor = new RequestTimingInterceptor();
    var mockRequest = new MockHttpServletRequest("GET", "/test");
    var mockResponse = new MockHttpServletResponse();
    interceptor.preHandle(mockRequest, mockResponse, null);
    assertNotNull(mockRequest.getAttribute(REQUEST_START_TIME_ATTRIBUTE));
  }

  @Test
  void afterCompletion() {
    var interceptor = new RequestTimingInterceptor();
    var mockRequest = spy(new MockHttpServletRequest("GET", "/test"));
    var mockResponse = new MockHttpServletResponse();
    mockRequest.setAttribute(REQUEST_START_TIME_ATTRIBUTE, Instant.now().toEpochMilli());
    interceptor.afterCompletion(mockRequest, mockResponse, null, null);
    verify(mockRequest, times(1)).getAttribute(REQUEST_START_TIME_ATTRIBUTE);
  }
}
