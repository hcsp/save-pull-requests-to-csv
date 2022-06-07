package com.github.hcsp.io;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {

        CloseableHttpClient httpClient= HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");

        CloseableHttpResponse response = httpClient.execute(httpGet);

        try {
            //打印响应的第一栏状态
            System.out.println(response.getStatusLine());
            //得到Http实体
            HttpEntity entity1 = response.getEntity();
            // do something useful wi th the response body
            // and ensure it is fully consumed
            //将实体转化为字节流
            InputStream is=entity1.getContent();
            //通过第三方库commons-io的IOUtils方法将字节流转换为字符串
            String s = IOUtils.toString(is, "UTF-8");
            //使用第三方库Jsoup，解析HTML字符串，并转换为Document
            Document document = Jsoup.parse(s);
            //根据class名查找需要的内容
            Elements select = document.select(".js-issue-row");
            FileWriter fw=new FileWriter(csvFile,true);
            fw.write("number"+",");
            fw.write("author"+",");
            fw.write("title"+"\r\n");
            int i=0;
            for (Element e:select
            ) {
                String id = e.child(0).child(1).child(0).attr("id").split("_")[1];
                fw.write(id+",");
                //这里使用getElementsByClass方法来直接找这一行得到其内容，
                //因为我通过child查找时，总是报越界异常
                String name = e.getElementsByClass("Link--muted").text();
                fw.write(name+",");
                //通过查找child来确定自己需要的内容
                String text = e.child(0).child(1).child(0).text();
                fw.write(text+"\r\n");
                i++;
                if (i>=n){
                    break;
                }
            }

            fw.close();
        } finally {
            response.close();

        }
    }
}
