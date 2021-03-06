package com.ajaya.cashloan.core.common.util.file;


import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import com.ajaya.cashloan.core.common.exception.RDRuntimeException;
import com.ajaya.cashloan.core.common.util.DateUtil;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import tool.util.StringUtil;

/**
 * 校验一个file文件是否是真实的文件类型
 * （避免是用户在上传时将后缀修改掉，伪装
 * 其他类型的文件进行上传操作）
 * @author RDuser
 */
public class FileUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
    public static final Map<String, String> FILE_TYPE_MAP = new HashMap<>();    
    
    /** 图片类型 */
    public static final String FILE_TYPE_GIF = "gif";
    public static final String FILE_TYPE_JPEG = "jpeg";
    public static final String FILE_TYPE_PNG = "png";
    public static final String FILE_TYPE_JPG = "jpg";
    /** pdf类型 */
    public static final String FILE_TYPE_PDF = "pdf";
    /** excel类型 */
    public static final String FILE_TYPE_XLS = "xls";
    public static final String FILE_TYPE_XLSX = "xlsx";
    /** word类型 */
    public static final String FILE_TYPE_DOC = "doc";
    public static final String FILE_TYPE_DOCX = "docx";
    
    static{    
        getAllFileType();  //初始化文件类型信息     
    }    
        
    private static void getAllFileType() {    
        FILE_TYPE_MAP.put("jpg", "FFD8FF"); //JPEG (jpg)
        FILE_TYPE_MAP.put("jpeg", "FFD8FF");
        FILE_TYPE_MAP.put("png", "89504E47");  //PNG (png)     
        FILE_TYPE_MAP.put("gif", "47494638");  //GIF (gif)     
        FILE_TYPE_MAP.put("tif", "49492A00");  //TIFF (tif)     
        FILE_TYPE_MAP.put("bmp", "424D"); //Windows Bitmap (bmp)     
        FILE_TYPE_MAP.put("dwg", "41433130"); //CAD (dwg)     
        FILE_TYPE_MAP.put("html", "68746D6C3E");  //HTML (html)     
        FILE_TYPE_MAP.put("rtf", "7B5C727466");  //Rich Text Format (rtf)     
        FILE_TYPE_MAP.put("xml", "3C3F786D6C");    
        FILE_TYPE_MAP.put("rar", "52617221");    
        FILE_TYPE_MAP.put("psd", "38425053");  //Photoshop (psd)     
        FILE_TYPE_MAP.put("eml", "44656C69766572792D646174653A");  //Email [thorough only] (eml)     
        FILE_TYPE_MAP.put("dbx", "CFAD12FEC5FD746F");  //Outlook Express (dbx)     
        FILE_TYPE_MAP.put("pst", "2142444E");  //Outlook (pst)     
        FILE_TYPE_MAP.put("xlsx", "504B0304");
        FILE_TYPE_MAP.put("xls", "D0CF11E0");  //MS Word     
        FILE_TYPE_MAP.put("doc", "D0CF11E0");  //MS Excel 注意：word 和 excel的文件头一样
        FILE_TYPE_MAP.put("pdf", "255044462D312E");
    }    
    
    public static final String getFileType(File file) {    
        String filetype = null;   
        InputStream is = null;
        byte[] b = new byte[50];    
        try {    
            is = new FileInputStream(file);    
            is.read(b);    
            filetype = getFileTypeByStream(b);    
            is.close();    
        }    
        catch (FileNotFoundException e) {    
        	logger.error("FileNotFoundException", e);  
        }    
        catch (IOException e) {    
            logger.error("IOException", e);
        } finally {
        	try {
        		if(is != null){    				
        			is.close();
    			}
			} catch (IOException e) {
				logger.error("IO关闭异常", e);
			}
        }
        return filetype;    
    }    
        
    public final static String getFileTypeByStream(byte[] b) {    
        String filetypeHex = String.valueOf(getFileHexString(b));    
        Iterator<Entry<String, String>> entryiterator = FILE_TYPE_MAP.entrySet().iterator();    
        while (entryiterator.hasNext()) {    
            Entry<String,String> entry =  entryiterator.next();    
            String fileTypeHexValue = entry.getValue();    
            if (filetypeHex.toUpperCase().startsWith(fileTypeHexValue)) {    
                return entry.getKey();    
            }    
        }    
        return null;    
    }    
        
    /**
     * 判断上传 类型是否 是jpg/png/gif/pdf 类型
     * @param file
     * @return
     */
    public static boolean checkFileType(File file){
    	String fileType = getFileType(file);
    	if (StringUtil.isBlank(fileType)) {
    		return false;
    	}
    	return isImage(file, fileType);
    }
    
    /**
     * 
     * 是否为图片类型 
     * @param file
     * @param fileType
     * @return
     */
    public static boolean isImage(File file, String fileType) {
		if ("jpeg".equals(fileType) || "jpg".equals(fileType) || "png".equals(fileType) || "gif".equals(fileType)) {
			if (fileIsImage(file)) {
				return true;
			}
		}
    	return false;
    }
    
    @SuppressWarnings("resource")
	public static Boolean fileIsImage(File file) {
		InputStream is = null;
		BufferedReader reader = null;
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			// 将文件转换成输入流
			is = new FileInputStream(file);
			//用image IO读取文件，如果文件file不是图片，则为null
			BufferedImage image = ImageIO.read(is);
			if (image != null) { // 如果image不为空，则说明file文件是图片
				reader = new BufferedReader(fr);
				String exits = null;
				while((exits = reader.readLine())!=null){
					exits = shiftD(exits);
					if(exits.indexOf("eval") >-1 || exits.indexOf("<?php") >-1 ){
						return false;
					}
				}
				return true;
			}
		} catch (Exception e) {
			logger.error("fileIsImage方法异常", e);
		} finally {
			try {
				if(is!=null){
					is.close();
				}
				if(reader!=null){
					reader.close();
				}
				if (fr!=null) {
					fr.close();
				}
			} catch (IOException e) {
				logger.error("fileIsImage IO关闭异常", e);
			}
		}
		return false;
	}
    
    public static String shiftD(String str) {
		int size = str.length();
		char[] chs = str.toCharArray();
		for (int i = 0; i < size; i++) {
			if (chs[i] <= 'Z' && chs[i] >= 'A') {
				chs[i] = (char) (chs[i] + 32);
			}
		}
		return new String(chs);
	}
    
    /**
     * 是否为pdf
     * @param fileType
     * @return
     */
    public static boolean isPdf(String fileType) {
    	if("pdf".equals(fileType) ){
    		return true;
    	}
    	return false;
    }
    
    /**
     * 是否为excel
     * @param fileType
     * @return
     */
    public static boolean isExcel(String fileType) {
    	if("xlsx".equals(fileType) || "xls".equals(fileType) ){
    		return true;
    	}
    	return false;
    }
    
    /**
     * 是否为word
     * @param fileType
     * @return
     */
    public static boolean isWord(String fileType) {
    	if("doc".equals(fileType) || "docx".equals(fileType) ){
    		return true;
    	}
    	return false;
    }

    /**
     * 根据 FILE_TYPE_MAP 的value值获取key
     * @param fileType
     * @return
     * @param
     */
    public static String getFileTypeStr(String fileType) {
    	 Iterator<Entry<String, String>> entryiterator = FILE_TYPE_MAP.entrySet().iterator();    
         while (entryiterator.hasNext()) {    
             Entry<String,String> entry =  entryiterator.next();    
             String fileTypeHexValue = entry.getValue();    
             if (fileType.equalsIgnoreCase(fileTypeHexValue)) {    
                 return entry.getKey();    
             }    
         }    
    	return "";
    }
    
    public static final String getFileHexString(byte[] b){    
        StringBuilder stringBuilder = new StringBuilder();    
        if (b == null || b.length <= 0)    
        {    
            return null;    
        }    
        for (int i = 0; i < b.length; i++)    
        {    
            int v = b[i] & 0xFF;    
            String hv = Integer.toHexString(v);    
            if (hv.length() < 2)    
            {    
                stringBuilder.append(0);    
            }    
            stringBuilder.append(hv);    
        }    
        return stringBuilder.toString();    
    }
    
    /**
	 * @description 图片上传
	 * @param file
	 * @param prefix 文件名称前缀
	 * @param folder 文件夹名称，紧接在/data/image之后
	 * @return
	 * @author mcwang
	 * @return UploadFileRes
	 * @since  1.0.0
	 */
	public static UploadFileModel uploadImg(MultipartFile file, String prefix, String folder) {
		
		UploadFileModel model = new UploadFileModel();
		model.setCreateTime(DateUtil.getNow());
		// 文件名称-特定前缀
		model.setOldName(file.getOriginalFilename());
		
		CommonsMultipartFile cf = (CommonsMultipartFile) file;
		DiskFileItem fi = (DiskFileItem) cf.getFileItem();
		File newFile = (File) fi.getStoreLocation();
		// 文件格式
		String fileType = getFileType(newFile);
		prefix = StringUtil.isBlank(prefix) ? "" : prefix + "_";
		String picName = prefix + DateUtil.dateStr(DateUtil.getNow(),DateUtil.DATEFORMAT_STR_016)+"."+fileType;  
		
		if (StringUtil.isBlank(fileType) || !isImage(newFile, fileType)) {
			model.setErrorMsg("图片格式错误或内容不规范");
			return model;
		}
		// 校验图片大小
		Long picSize = file.getSize();
		if (picSize.compareTo(20971520L) > 0) {
			logger.error("文件超出20M大小限制");
			model.setErrorMsg("文件超出20M大小限制");
			return model;
		}
		// 保存文件
		String s=File.separator;
		String filePath = s+"data"+s+"image"+s + folder + s +
				DateUtil.dateStr(DateUtil.getNow(), DateUtil.DATEFORMAT_STR_013) + s + picName;
		if(s.equals("\\")){
			filePath="D:"+filePath;
		}
		File files = new File(filePath);
		if (!files.exists()) {
			try {
				files.mkdirs();
			} catch (Exception e) {
				logger.error("上传文件时："+e.getMessage(), e);
				model.setErrorMsg("文件目录不存在");
				return model;
			}
		}
		try {
			file.transferTo(files);
		} catch (IllegalStateException | IOException e) {
			logger.error("上传文件时："+e.getMessage(), e);
			throw new RDRuntimeException(e.getMessage(),e);
		}
		// 转存文件
		model.setResPath(filePath);
		model.setFileName(picName);
		model.setFileFormat(fileType);
		model.setFileSize(new BigDecimal(picSize));
		return model;
	}


}  