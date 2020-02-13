/*
 * author : Li Yongliang
 * date : 20-2-1 上午11:44
 */

package org.yaml.lee;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpClient {
    public static void main(String[] args) throws Exception {
        //创建默认HttpClient
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //调用多次服务并控制台打印结果
        for (int i = 0; i < 6; i++) {
            //配置get方法请求
            HttpGet httpGet = new HttpGet("http://localhost:9000/findClient");
            //执行get请求并获取返回响应
            CloseableHttpResponse response = httpClient.execute(httpGet);
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }
}
