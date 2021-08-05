package com.github.hcsp.io;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException, ParseException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        httpGet.setHeader( "Accept", "application/vnd.github.v3+json" );
        try (CloseableHttpResponse response1 = httpClient.execute(httpGet)) {
            System.out.println(response1.getStatusLine());
            HttpEntity entity = response1.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            String responseString = EntityUtils.toString(entity, "UTF-8");
            JSONParser parser = new JSONParser();
            JSONArray pullRequest = (JSONArray) parser.parse(responseString);
            BufferedWriter resultCsvFile = new BufferedWriter(new FileWriter(csvFile));
            resultCsvFile.write("number,author,title\n");
            int csvFileSize = Math.min(pullRequest.size(), n);
            for (int i = 0; i < csvFileSize; i++) {
                JSONObject jsonSinglePullResult = (JSONObject) pullRequest.get(i);
                JSONObject jsonUserResult = (JSONObject) jsonSinglePullResult.get("user");
                resultCsvFile.write(jsonSinglePullResult.get("number").toString() + ','
                        + jsonUserResult.get("login").toString() + ','
                        + jsonSinglePullResult.get("title").toString() + '\n');
                }
            EntityUtils.consume(entity);
            resultCsvFile.close();
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        savePullRequestsToCSV("golang/go", 20, new File("csvTestFile.csv"));
    }
}
