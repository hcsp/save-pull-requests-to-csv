package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.tools.internal.ws.wsdl.document.Output;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import sun.jvm.hotspot.oops.OopUtilities;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        RequestConfig build = RequestConfig.custom().setConnectTimeout(10000).setConnectionRequestTimeout(10000).build();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls" + "?per_page=" + n);
        httpGet.setConfig(build);
        try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            InputStream content = entity1.getContent();
            String s = IOUtils.toString(content, StandardCharsets.UTF_8);
            JSONArray objects;
            objects = JSON.parseArray(s);
            FileWriter fileWriter = new FileWriter(csvFile);
            fileWriter.write("number,author,title\n");
            for (int i = 0; i < objects.size(); i++) {
                JSONObject jsonObject = objects.getJSONObject(i);
                Integer id = jsonObject.getInteger("number");
                String author = jsonObject.getJSONObject("user").getString("login");
                String title = jsonObject.getString("title");
                fileWriter.write(id.toString() + "," + author + "," + title + '\n');
            }
            fileWriter.close();
            EntityUtils.consume(entity1);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
