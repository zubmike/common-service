package ru.zubmike.service.dao.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.Assert;
import org.junit.Test;
import ru.zubmike.core.types.BasicDictItem;
import ru.zubmike.core.utils.InternalException;
import ru.zubmike.service.utils.JsonUtils;

import java.io.Serializable;

public class BasicHttpDaoTest {

	private static final String TEST_HTTP_USER_AGENT = "github.com/zubmike/common-service/test";

	private final BasicHttpDao basicHttpDao = new BasicHttpDao(TEST_HTTP_USER_AGENT, 1000);

	@Test
	public void getRequest() {
		HttpBinUserAgentResponse userAgentResponse = basicHttpDao.doGetRequest("https://httpbin.org/user-agent",
				response -> JsonUtils.parse(response, HttpBinUserAgentResponse.class));
		Assert.assertEquals(TEST_HTTP_USER_AGENT, userAgentResponse.getUserAgent());
	}

	@Test
	public void getRequestOptional() {
		boolean notFound = basicHttpDao.doGetRequestOptional("https://httpbin.org/status/404", response -> null).isEmpty();
		Assert.assertTrue(notFound);
	}

	@Test(expected = InternalException.class)
	public void getRequestTimeout() {
		basicHttpDao.doGetRequest("https://httpbin.org/delay/2", response -> null);
	}

	@Test
	public void postRequest() {
		BasicDictItem requestBody = new BasicDictItem(1, "Test");
		HttpBinResponse httpBinResponse = basicHttpDao.doPostRequest("https://httpbin.org/anything",
				JsonUtils.writeObjectAsBytes(requestBody),
				response -> JsonUtils.parse(response, HttpBinResponse.class));
		Assert.assertNotNull(httpBinResponse.getData());
		Assert.assertEquals(requestBody, JsonUtils.parse(httpBinResponse.getData(), BasicDictItem.class));
	}

	private static class HttpBinUserAgentResponse implements Serializable {

		private static final long serialVersionUID = 1L;

		@JsonProperty("user-agent")
		private String userAgent;

		private String getUserAgent() {
			return userAgent;
		}

		private void setUserAgent(String userAgent) {
			this.userAgent = userAgent;
		}
	}

	private static class HttpBinResponse implements Serializable {

		private static final long serialVersionUID = 1L;

		private String data;

		private String getData() {
			return data;
		}

		private void setData(String data) {
			this.data = data;
		}
	}

}
