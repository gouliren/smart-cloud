package org.smartframework.cloud.utility.test.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import junit.framework.TestCase;
import org.apache.http.message.BasicNameValuePair;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.smartframework.cloud.utility.HttpUtil;
import org.smartframework.cloud.utility.test.integration.vo.GetPageReqVO;
import org.smartframework.cloud.utility.test.integration.vo.GetPageRespVO;
import org.smartframework.cloud.utility.test.integration.vo.PostUrlEncodedRespVO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class HttpUtilIntegrationTest extends TestCase {
    /**
     * 服务启动端口
     */
    private static final int PORT = 12345;
    private static final String REQUEST_URL_PREFIX = "http://localhost:" + PORT + "/test";
    private static boolean bootstrap = false;

    @Test
    public void testGet() throws IOException {
        startService();
        String result = HttpUtil.get(REQUEST_URL_PREFIX + "?str=test", null);
        Assertions.assertThat(result).isEqualTo("test");
    }

    @Test
    public void testGetReturnType() throws IOException {
        startService();

        GetPageReqVO reqVO = new GetPageReqVO();
        reqVO.setStr("test");
        reqVO.setIds(new long[]{1, 2, 3});
        GetPageRespVO result = HttpUtil.get(REQUEST_URL_PREFIX + "/page", reqVO, new TypeReference<GetPageRespVO>() {
        });
        Assertions.assertThat(result.getStr()).isEqualTo("test");
        Assertions.assertThat(result.getIds()).isNotEmpty();
        Assertions.assertThat(result.getIds().length).isEqualTo(3);
        Assertions.assertThat(result.getIds()[2]).isEqualTo(3);
    }

    @Test
    public void testPostWithRaw() throws IOException {
        startService();
        String result = HttpUtil.postWithRaw(REQUEST_URL_PREFIX, "test");
        Assertions.assertThat(result).isEqualTo("test");
    }

    @Test
    public void testPostWithRawReturnType() throws IOException {
        startService();
        List<String> result = HttpUtil.postWithRaw(REQUEST_URL_PREFIX + "/list", "test",
                new TypeReference<List<String>>() {
                });
        Assertions.assertThat(result.get(0)).isEqualTo("test");
    }

    @Test
    public void testPostWithUrlEncoded() throws IOException {
        startService();
        List<BasicNameValuePair> parameters = new ArrayList<>();
        String id = "100";
        parameters.add(new BasicNameValuePair("id", id));
        PostUrlEncodedRespVO result = HttpUtil.postWithUrlEncoded(REQUEST_URL_PREFIX + "/postUrlEncoded", parameters,
                new TypeReference<PostUrlEncodedRespVO>() {
                });
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(id);
    }

    private void startService() {
        if (!bootstrap) {
            bootstrap = true;
            SpringApplication.run(HttpUtilIntegrationTest.class, "--server.port=" + PORT);
        }
    }

}