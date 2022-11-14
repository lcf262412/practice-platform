package com.example.demo.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.Charset; 
import org.springframework.web.multipart.MultipartFile; 
import info.monitorenter.cpdetector.io.ASCIIDetector; 
import info.monitorenter.cpdetector.io.CodepageDetectorProxy; 
import info.monitorenter.cpdetector.io.JChardetFacade; 
import info.monitorenter.cpdetector.io.ParsingDetector; 
import info.monitorenter.cpdetector.io.UnicodeDetector;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EncodeDetector {

	//用于检测导入文件的编码格式
	public static String getFileEncode(MultipartFile file) {
		//获取探测器实体
		CodepageDetectorProxy detectorProxy = CodepageDetectorProxy.getInstance();
		//配置PasingDetector,用于探测HTML、XML文件编码
		detectorProxy.add(new ParsingDetector(false));
		//配置JChardetFacade,用于探测大多数文件编码
		detectorProxy.add(JChardetFacade.getInstance());
		//配置ASCIIDetector,用于探测ASCII编码
		detectorProxy.add(ASCIIDetector.getInstance());
		//配置ASCIIDetector,用于探测Unicode家族编码
		detectorProxy.add(UnicodeDetector.getInstance());
		
		Charset charset = null;
		
		try {
			BufferedInputStream inputStream = new BufferedInputStream(file.getInputStream());
			charset = detectorProxy.detectCodepage(inputStream, 10000);
			inputStream.close();
		}catch (IOException | IllegalArgumentException e) {
			log.error("Exception :", e);
		}
		if (charset!=null) 
			return charset.name();
		else
			return "UTF-8";

	}
}
