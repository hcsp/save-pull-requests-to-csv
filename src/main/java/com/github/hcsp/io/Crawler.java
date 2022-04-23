package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

public class Crawler {
    static class GitHubPullRequest {
        int number;
        String author;
        String title;

        public GitHubPullRequest(int number, String author, String title) {
            this.number = number;
            this.author = author;
            this.title = title;
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        String rul = "https://api.github.com/repos/" + repo + "/pulls";
        ArrayList<String> pullRequestList = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(rul);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        String body = IOUtils.toString(content, StandardCharsets.UTF_8);
        JSONArray array = JSON.parseArray(body);
        for (Object o : array) {
            JSONObject jsonObject = (JSONObject) o;
            pullRequestList.add(
                    jsonObject.getIntValue("number") +
                            jsonObject.getJSONObject("user").getString("login") +
                            jsonObject.getString("title")
            );
        }
        Files.write(csvFile.toPath(), pullRequestList.subList(0, n));

        EntityUtils.consume(entity);
    }

    public static void main(String[] args) throws IOException {
        File csvFile = new File("./xxx.csv");
        savePullRequestsToCSV("gradle/gradle", 5, csvFile);
    }
}
