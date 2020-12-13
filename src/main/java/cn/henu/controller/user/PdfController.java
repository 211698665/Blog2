package cn.henu.controller.user;

import cn.henu.pojo.Article;
import cn.henu.service.ArticleService;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;

/**
 * @author: XXX
 * @date: 2019/7/10 11:32
 */
@Controller
public class PdfController {
    @Autowired
    private ArticleService articleService;

    @RequestMapping("/user/pdfDown")
    public void writePdf(int id, HttpServletResponse response, HttpServletRequest request) {
        Document document = new Document(PageSize.A4);
        Article article = articleService.findById(id);
        String articleContent = HtmlUtils.htmlUnescape(article.getArticleContent());
        String posi = "C:\\image\\" + article.getArticleTitle() + ".pdf";
        try {
            OutputStream outputStream = new FileOutputStream(new File(posi));
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();
            //获取字体文件目录
            String fontDir = this.getClass().getClassLoader().getResource("static").getFile();
            //注册字体文件
            XMLWorkerFontProvider xmlWorkerFontProvider = new XMLWorkerFontProvider("/static/user/fonts/");
            //设置中文字体，本文举例使用的是仿宋
            BaseFont baseFont = BaseFont.createFont("/static/user/fonts/simfang.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            //html转换成普通文字,方法如下:
            org.jsoup.nodes.Document contentDoc = Jsoup.parseBodyFragment("<h2 style='text-align:center;'>" + article.getArticleTitle() + "</h2><br/>" + articleContent);
            org.jsoup.nodes.Document.OutputSettings outputSettings = new org.jsoup.nodes.Document.OutputSettings();
            outputSettings.syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
            contentDoc.outputSettings(outputSettings);
            String parsedHtml = contentDoc.outerHtml();
            //这儿的font-family不支持汉字，{font-family:仿宋} 是不可以的。
            InputStream cssIs = new ByteArrayInputStream("* {font-family: fangsong;}".getBytes("UTF-8"));
            //第四个参数是html中的css文件的输入流
            //第五个参数是字体提供者，使用系统默认支持的字体时，可以不传。
            XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream(parsedHtml.getBytes()), cssIs);
            //关闭
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        OutputStream out = null;
        try {
            String filename = article.getArticleTitle() + ".pdf";
            filename = new String(filename.getBytes(), "ISO8859-1");
            response.addHeader("Content-Disposition", "attachment;filename=" + filename);
            // 2.下载
            out = response.getOutputStream();
            // inputStream：读文件，前提是这个文件必须存在，要不就会报错
            InputStream is = new FileInputStream(posi);
            byte[] b = new byte[4096];
            int size = is.read(b);
            while (size > 0) {
                out.write(b, 0, size);
                size = is.read(b);
            }
            out.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}