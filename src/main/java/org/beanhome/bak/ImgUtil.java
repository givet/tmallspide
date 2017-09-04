package org.beanhome.bak;


import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;


public class ImgUtil {
    private Image img;
    private final static int WIDTH = 147;
    private final static int HEIGHT = 136;

    public static void resizeImage(String inputFile, String outputFile, int size, String format) throws IOException {
    	 InputStream is = new FileInputStream(new File(inputFile));
         OutputStream os = new FileOutputStream(new File(outputFile));
    	 resizeImage(is, os, size, format);
    }
    
    /**
     * 改变图片的大小到宽为size，然后高随着宽等比例变化
     * @param is 上传的图片的输入流
     * @param os 改变了图片的大小后，把图片的流输出到目标OutputStream
     * @param size 新图片的宽
     * @param format 新图片的格式
     * @throws IOException
     */
    public static OutputStream resizeImage(InputStream is, OutputStream os, int size, String format) throws IOException {
        BufferedImage prevImage = ImageIO.read(is);
        double width = prevImage.getWidth();
        double height = prevImage.getHeight();
        double percent = size/width;
        int newWidth = (int)(width * percent);
        int newHeight = (int)(height * percent);
        BufferedImage image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_BGR);
        Graphics graphics = image.createGraphics();
        graphics.drawImage(prevImage, 0, 0, newWidth, newHeight, null);
        ImageIO.write(image, format, os);
        os.flush();
        is.close();
        os.close();
//        FileOutputStream b = (FileOutputStream) os;
        return os;
    }
    
    /**
	 * Java拼接多张图片
	 * 
	 * @param pics
	 * @param type
	 * @param dst_pic
	 * @return
	 */
	public static boolean merge(String[] pics, String type, String dst_pic) {

		int len = pics.length;
		if (len < 1) {
			System.out.println("pics len < 1");
			return false;
		}
		File[] src = new File[len];
		BufferedImage[] images = new BufferedImage[len];
		int[][] ImageArrays = new int[len][];
		for (int i = 0; i < len; i++) {
			try {
				src[i] = new File(pics[i]);
				images[i] = ImageIO.read(src[i]);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			int width = images[i].getWidth();
			int height = images[i].getHeight();
			ImageArrays[i] = new int[width * height];// 从图片中读取RGB
			ImageArrays[i] = images[i].getRGB(0, 0, width, height,
					ImageArrays[i], 0, width);
		}

		int dst_height = 0;
		int dst_width = images[0].getWidth();
		for (int i = 0; i < images.length; i++) {
			dst_width = dst_width > images[i].getWidth() ? dst_width
					: images[i].getWidth();

			dst_height += images[i].getHeight();
		}
		System.out.println(dst_width);
		System.out.println(dst_height);
		if (dst_height < 1) {
			System.out.println("dst_height < 1");
			return false;
		}

		// 生成新图片
		try {
			// dst_width = images[0].getWidth();
			BufferedImage ImageNew = new BufferedImage(dst_width, dst_height,
					BufferedImage.TYPE_INT_RGB);
			int height_i = 0;
			for (int i = 0; i < images.length; i++) {
				ImageNew.setRGB(0, height_i, dst_width, images[i].getHeight(),
						ImageArrays[i], 0, dst_width);
				height_i += images[i].getHeight();
			}

			File outFile = new File(dst_pic);
			System.out.println(outFile.getAbsolutePath());
			ImageIO.write(ImageNew, type, outFile);// 写图片
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean delFonts(String srcFilePath, String type, String dst_pic,int copyFromIndex,int copyLength,int distFromIndex,int distEndIndex) {
		File srcFile = new File(srcFilePath);
		try {
			BufferedImage srcImage = ImageIO.read(srcFile);
			int[] srcImageArrays = new int[srcImage.getWidth() * srcImage.getHeight()];// 从图片中读取RGB
			srcImageArrays = srcImage.getRGB(0, 0, srcImage.getWidth(),  srcImage.getHeight(),
					srcImageArrays, 0, srcImage.getWidth());
//			System.out.println(srcImage.getWidth());
//			System.out.println(srcImage.getHeight());
			for(int i=distFromIndex;i<distEndIndex;i++){
				int distStart= i*srcImage.getWidth();
				System.arraycopy(srcImageArrays, copyFromIndex, srcImageArrays, distStart, copyLength);	
			}
			
			
			
			srcImage.setRGB(0, 0, srcImage.getWidth(), srcImage.getHeight(),
					srcImageArrays, 0, srcImage.getWidth());
			
			File outFile = new File(dst_pic);
			System.out.println(outFile.getAbsolutePath());
			ImageIO.write(srcImage, type, outFile);
			
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static boolean mergeThumb(String backImageFilePath,String srcFilePath, String type, String dst_pic) {
		File backImageFile = new File(backImageFilePath);
		File srcFile = new File(srcFilePath);
		try {
			BufferedImage backImage = ImageIO.read(backImageFile);
			BufferedImage srcImage = ImageIO.read(srcFile);
			int[] srcImageArrays = new int[srcImage.getWidth() * srcImage.getHeight()];// 从图片中读取RGB
			srcImageArrays = srcImage.getRGB(0, 0, srcImage.getWidth(),  srcImage.getHeight(),
					srcImageArrays, 0, srcImage.getWidth());
			backImage.setRGB(199, 0, srcImage.getWidth(), backImage.getHeight(),
					srcImageArrays, 0, srcImage.getWidth());
			
			File outFile = new File(dst_pic);
			System.out.println(outFile.getAbsolutePath());
			ImageIO.write(backImage, type, outFile);
			
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static void genMainImg(String backImgAbsPath,String imgThumb640AbsPath,String img750AbsPath){
    	String imgThumb352AbsPath =imgThumb640AbsPath+"352.jpg";
    	try {
			ImgUtil.resizeImage(imgThumb640AbsPath, imgThumb352AbsPath, 352,
					"jpg");
			ImgUtil.mergeThumb(backImgAbsPath, imgThumb352AbsPath, "jpg",
					img750AbsPath);
			new File(imgThumb352AbsPath).delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void taskGenMainImg(){
		String path = "C:\\Users\\wbh\\Desktop\\ps";
    	String imgThumb640AbsPath =path+"/2.jpg";
    	String backImgAbsPath =path+"/back.jpg";
    	String img750AbsPath =path+"/1.jpg";
    	genMainImg(backImgAbsPath, imgThumb640AbsPath, img750AbsPath);
	}
	final static String BACK_IMG_PATH ="C:\\Users\\wbh\\Desktop\\ps\\back.jpg";
	public static void taskGenAllMainImg(){
		String path = "D:/1-pdd/";
		File directory = new File(path);
		File[] subDirs = directory.listFiles(new FileFilter() {
			public boolean accept(File file) {
//				System.out.println(file);
				if (file.isDirectory()) {
					return true;
				}
				return false;
			}
		});
		
		final Map<String,String> mainImgMap = new HashMap<String,String>();
    	
    	int i=1;
    	for(File subDir :subDirs){
    		File[] pics = subDir.listFiles(new FileFilter() {
    			public boolean accept(File file) {
    				
    				if (file.getName().contains("轮播图")) {
    					return true;
    				}
    				return false;
    			}
    		});
    		File[] lunboPics = subDir.listFiles(new FileFilter() {
    			public boolean accept(File file) {
    				
    				if (file.getName().contains("hdthumb")) {
    					return true;
    				}
    				return false;
    			}
    		});
    		
    		
    		if(pics==null||lunboPics==null){
    			System.out.println("未找到主图:"+subDir);
    			continue;
    		}
    		
    		
    		//取第一张轮播图生成主图
    		String backImgAbsPath =BACK_IMG_PATH;
    		String img750Name = lunboPics[0].getName();
	    	String img750AbsPath =pics[0].getParent()+"/"+img750Name;
	    	genMainImg(backImgAbsPath, pics[0].getAbsolutePath(), img750AbsPath);
	    	
	    	
	    	//生成高清图。
	    	
	    	
	    	
	    	
    		System.out.println(String.format("转换主图:%s/%s", i++,subDirs.length));
    	}
    	
	}
    
    public static void main(String[] args) {
    	
    	
//    	taskGenMainImg();
    	taskGenAllMainImg();
    	
//    	delFonts("D:/1-pdd/335/轮播图2.jpg", "jpg", "D:/1-pdd/335/轮播图2-dist.jpg",
//    			0, 200, 10, 100);
//    	delFonts("D:/1-pdd/335/轮播图2.jpg", "jpg", "D:/1-pdd/335/轮播图2-dist.jpg");
    	
//    	int[] a = {1,2,3,4,5,6,7,8,9};
//
//		System.arraycopy(a, 0, a, 3, 2);
//  	
//		System.out.println(Arrays.toString(a));
    	
    	
    	
//        try {
//        	String path = "D:\\1-wd\\Huatone\\拼多多上货助理\\downloadPic";
//            InputStream is = new FileInputStream(new File(path+"\\20170820104800587mCPGkdlunbo.jpg"));
//            OutputStream os = new FileOutputStream(new File("D:\\20170820104800587mCPGkdlunbo352.jpg"));
//            resizeImage(is, os, 352, "jpg");
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

}