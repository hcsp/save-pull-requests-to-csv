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

import java.io.*;
import java.util.ArrayList;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/gradle/gradle");
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity1 = response.getEntity();
        InputStream Ins = entity1.getContent();
        //输入流变成字符串
        String html = IOUtils.toString(Ins, "UTF-8");
        //Jsoup解析
        Document document = Jsoup.parse(html);
        //writer
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(csvFile));
        //得到仓库名
        String repopart1 = document.getElementById("repository-container-header").child(0).child(0).child(0).child(1).text();
        String repopart2 = document.getElementById("repository-container-header").child(0).child(0).child(0).child(2).text();
        String repopart3 = document.getElementById("repository-container-header").child(0).child(0).child(0).child(3).text();
        String reponame = repopart1 + repopart2 + repopart3;
        if (reponame.equals(repo)) {
            //创建信息列表 准备拿数据
            ArrayList<Element> pr = document.select(".js-issue-row");
            for (Element eles : pr) {
                //遍历数据得到 标题
                String prtitle = eles.child(0).child(1).child(0).text() + " ";
                //System.out.println(prtitle);
                //遍历数据得到 ：#18826 opened 12 hours ago by bamboo
                String shuju = eles.child(0).child(1).children().last().child(0).text();
                //处理shuju， 得到编号
                String[] str = shuju.split("\\s+");
                String[] str2 = str[0].split("#");
                String getnum = str2[1];
                //System.out.println(getnum);
                //处理shuju， 得到名字
                String getname = str[(str.length - 1)];
                //System.out.println(getname);
                for (int i = 0; i < n; i++) {
                    bufferedWriter.write(getnum);
                    bufferedWriter.write(",");
                    bufferedWriter.write(getname);
                    bufferedWriter.write(",");
                    bufferedWriter.write(prtitle);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
        }
        System.out.println("仓库名不存在");
    }
}
