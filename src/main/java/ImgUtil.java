import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class ImgUtil {
	private Image img;
	private final static int WIDTH = 147;
	private final static int HEIGHT = 136;

	public static void resizeImage(String inputFile, String outputFile,
			int size, String format) throws IOException {
		InputStream is = new FileInputStream(new File(inputFile));
		OutputStream os = new FileOutputStream(new File(outputFile));
		resizeImage(is, os, size, format);
	}

	/**
	 * 改变图片的大小到宽为size，然后高随着宽等比例变化
	 * 
	 * @param is
	 *            上传的图片的输入流
	 * @param os
	 *            改变了图片的大小后，把图片的流输出到目标OutputStream
	 * @param size
	 *            新图片的宽
	 * @param format
	 *            新图片的格式
	 * @throws IOException
	 */
	public static OutputStream resizeImage(InputStream is, OutputStream os,
			int size, String format) throws IOException {
		BufferedImage prevImage = ImageIO.read(is);
		double width = prevImage.getWidth();
		double height = prevImage.getHeight();
		double percent = size / width;
		int newWidth = (int) (width * percent);
		int newHeight = (int) (height * percent);
		BufferedImage image = new BufferedImage(newWidth, newHeight,
				BufferedImage.TYPE_INT_BGR);
		Graphics graphics = image.createGraphics();
		graphics.drawImage(prevImage, 0, 0, newWidth, newHeight, null);
		ImageIO.write(image, format, os);
		os.flush();
		is.close();
		os.close();
		// FileOutputStream b = (FileOutputStream) os;
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

	public static boolean delFonts(String srcFilePath, String type,
			String dst_pic, int copyFromIndex, int copyLength,
			int distFromIndex, int distEndIndex) {
		File srcFile = new File(srcFilePath);
		try {
			BufferedImage srcImage = ImageIO.read(srcFile);
			int[] srcImageArrays = new int[srcImage.getWidth()
					* srcImage.getHeight()];// 从图片中读取RGB
			srcImageArrays = srcImage.getRGB(0, 0, srcImage.getWidth(),
					srcImage.getHeight(), srcImageArrays, 0,
					srcImage.getWidth());
			// System.out.println(srcImage.getWidth());
			// System.out.println(srcImage.getHeight());
			for (int i = distFromIndex; i < distEndIndex; i++) {
				int distStart = i * srcImage.getWidth();
				System.arraycopy(srcImageArrays, copyFromIndex, srcImageArrays,
						distStart, copyLength);
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

	public static boolean mergeThumb(
			String srcFilePath, String type, String dst_pic) {
		File backImageFile = new File(Object.class.getResource("/back.jpg").getPath());
		File srcFile = new File(srcFilePath);
		try {
			BufferedImage backImage = ImageIO.read(backImageFile);
			BufferedImage srcImage = ImageIO.read(srcFile);
			int[] srcImageArrays = new int[srcImage.getWidth()
					* srcImage.getHeight()];// 从图片中读取RGB
			srcImageArrays = srcImage.getRGB(0, 0, srcImage.getWidth(),
					srcImage.getHeight(), srcImageArrays, 0,
					srcImage.getWidth());
			backImage.setRGB(199, 0, srcImage.getWidth(),
					backImage.getHeight(), srcImageArrays, 0,
					srcImage.getWidth());

			File outFile = new File(dst_pic);
			System.out.println(outFile.getAbsolutePath());
			ImageIO.write(backImage, type, outFile);

		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}

		return true;
	}

	public static void genMainImg(
			String imgThumb640AbsPath, String img750AbsPath) {
		String imgThumb352AbsPath = imgThumb640AbsPath + "352.jpg";
		try {
			ImgUtil.resizeImage(imgThumb640AbsPath, imgThumb352AbsPath, 352,
					"jpg");
			ImgUtil.mergeThumb( imgThumb352AbsPath, "jpg",
					img750AbsPath);
			new File(imgThumb352AbsPath).delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void copyByFileChannel(File s, File t) {

		FileInputStream fi = null;

		FileOutputStream fo = null;

		FileChannel in = null;

		FileChannel out = null;

		try {

			fi = new FileInputStream(s);

			fo = new FileOutputStream(t);

			in = fi.getChannel();// 得到对应的文件通道

			out = fo.getChannel();// 得到对应的文件通道

			in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				fi.close();

				in.close();

				fo.close();

				out.close();

			} catch (IOException e) {

				e.printStackTrace();

			}

		}

	}

	public static String getSimpleName(File file) {
		return file.getName().substring(0, file.getName().indexOf("."));
	}

	public static List<ItemImage> findItemImages(final String handleDir,
			final Date date) {
		File directory = new File(handleDir);

		if (!directory.isDirectory()) {
			System.out.println("No directory provided");
			return null;
		}

		final List<ItemImage> itemImages = new ArrayList<ItemImage>();

		File[] itemFiles = directory.listFiles(new FileFilter() {
			public boolean accept(File file) {

				if (file.lastModified() > date.getTime()
						&& file.isDirectory()) {
					return true;
				}
				return false;
			}
		});
		
		if (itemFiles==null||itemFiles.length==0) {
			System.out.println("找不到拼多多工具下载的商品图片,目录:"+handleDir);
			return null;
		}

		for(File itemFile:itemFiles){

			
			File[] hdthumbFiles= itemFile.listFiles(new FileFilter() {
				public boolean accept(File file) {
					if (file.getName().endsWith("hdthumb.jpg")) return true;
					return false;
				}
			});
			
			
			
			File[] mainfiles = itemFile.listFiles(new FileFilter() {
				public boolean accept(File file) {
					if (file.getName().endsWith("00.jpg")) {
						try {
							BufferedImage sourceImg = ImageIO
									.read(new FileInputStream(file));
							if (sourceImg != null && sourceImg.getWidth() == 750
									&& sourceImg.getHeight() == 352)
								return true;
						} catch (Exception e) {
							e.printStackTrace();

						}
					}
					return false;
				}
			});
			if(hdthumbFiles==null||mainfiles==null){
				System.err.println("该目录下图片高清图和主图有问题。请检查"+itemFile.getAbsolutePath());
			}
			ItemImage itemImage = new ItemImage(itemFile.getAbsolutePath(), hdthumbFiles[0]
					.getName());
			itemImage.setImg750AbsPath(mainfiles[0].getName());
			itemImages.add(itemImage);
		}
		return itemImages;

	}


	public static void taskAfterPtu(String path, PicTask picTask) {
		File directory = new File(path);
		File[] subDirs = directory.listFiles(new FileFilter() {
			public boolean accept(File file) {
				// System.out.println(file);
				if (file.isDirectory()) {
					return true;
				}
				return false;
			}
		});


		int i = 1;
		for (File subDir : subDirs) {
			File[] picExports = subDir.listFiles(new FileFilter() {
				public boolean accept(File file) {

					if (file.getName().contains("轮播图")) {
						return true;
					}
					return false;
				}
			});
			File[] img400Copys = subDir.listFiles(new FileFilter() {
				public boolean accept(File file) {

					if (file.getName().contains("hdthumb")) {
						return true;
					}
					return false;
				}
			});

			File[] img750Copys = subDir.listFiles(new FileFilter() {
				public boolean accept(File file) {

					if (getSimpleName(file).endsWith("00")) {
						return true;
					}
					return false;
				}
			});
			
			File[] copyBackPathFiles = subDir.listFiles(new FileFilter() {
				public boolean accept(File file) {

					if (file.getName().equals(ItemImage.pathNameTxt)) {
						return true;
					}
					return false;
				}
			});

			if (picExports == null || img400Copys == null
					|| img750Copys == null|| copyBackPathFiles == null) {
				System.out.println("未找到轮播图,主图,拷贝回去路径:" + subDir);
				continue;
			}
			File defaultImg4genMain= picExports[0];
			File img400CopyFile = img400Copys[0];
			File img750CopyFile = img750Copys[0];
			String copyBackPath = readFile(copyBackPathFiles[0]);
			picTask.handle(defaultImg4genMain, img400CopyFile, img750CopyFile,copyBackPath);

			System.out.println(String.format("转换:%s/%s", i++, subDirs.length));

		}

	}
	
	public static void del(String dir) {
		if(!new File(dir).isDirectory())return;
		File[] orgfiles = new File(dir).listFiles();

		for (int i = 0; i < orgfiles.length; i++) {
			orgfiles[i].delete();
		}
		
	}
	public static void taskClearPddToolPic(String pddToolPicPath) {
		File[] orgfiles = new File(pddToolPicPath).listFiles();

		for (int i = 0; i < orgfiles.length; i++) {
			orgfiles[i].delete();
			del(orgfiles[i].getAbsolutePath());
		}
		System.out.println("清理拼多多工具目录完成");
	}

	public static void taskBeforeDownload(String distPath, String bakPath) {
		File[] bakfiles = new File(bakPath).listFiles();
		File[] picDirs = new File(distPath).listFiles();

		for (int k = 0; k < bakfiles.length; k++) {
			bakfiles[k].delete();
		}
		System.out.println("清理拼多多备份目录完成");

		for (int j = 0; j < picDirs.length; j++) {
			if (picDirs[j].isDirectory()) {
				File[] children = picDirs[j].listFiles();
				for (int i = 0; i < children.length; i++) {
					children[i].delete();
				}
			}
			picDirs[j].delete();
		}
		System.out.println("清理工作目录完成");
	}
	
	public static void writeFile(File file,String str){
		BufferedWriter bw =null;
		FileWriter fw=null;
		try {
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(str);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
		    try {
		    	bw.close();
		    	fw.close();
			} catch (Exception e2) {
			}	
		}
		
	}
	
	public static String readFile(File file){
		BufferedReader br=null;
		FileReader fr=null;
		try {
			fr = new FileReader(file);
		    br = new BufferedReader(fr);
			return br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
		    try {
		    	br.close();
		    	fr.close();
		    	
			} catch (Exception e2) {
			}	
		}
		return null;
	}

	public static void taskBeforePtu(String pddToolPicPath, String distPath,
			String bakPath, Date date) {
		List<ItemImage> itemImages = findItemImages(pddToolPicPath, date);
		File[] picDirs = new File(distPath).listFiles();
		if (picDirs == null || picDirs.length == 0) {
			System.err.println("请先使用上货助手导出图片到目录:" + distPath);

			return;
		}
		Arrays.sort(picDirs, new Comparator<File>() {

			public int compare(File o1, File o2) {
				if (Integer.parseInt(o1.getName()) > Integer.parseInt(o2
						.getName()))
					return 1;
				return -1;
			}
		});

		if (itemImages==null||itemImages.size() != picDirs.length) {
			System.err.println("上货助手下载的图片"+itemImages.size()+"与处理目录"+picDirs.length+"无法匹配");

			return;
		}
		for (int j = 0; j < itemImages.size(); j++) {

			ItemImage itemImage = itemImages.get(j);
			File img750File = new File(itemImage.getImg750AbsPath());
			copyByFileChannel(img750File, new File(picDirs[j].getAbsolutePath()
					+ "/" + img750File.getName()));
			copyByFileChannel(img750File,
					new File(bakPath + "/" + img750File.getName()));

			File img400File = new File(itemImage.getImgThumb400AbsPath());
			if (img400File.exists()) {
				copyByFileChannel(img400File,
						new File(picDirs[j].getAbsolutePath() + "/"
								+ img400File.getName()));
				copyByFileChannel(img400File, new File(bakPath + "/"
						+ img400File.getName()));

			} else {
				System.err.println("找不到轮播图:" + picDirs[j].getAbsolutePath());
			}
			
			File pddToolPathFile = new File(picDirs[j].getAbsolutePath()
					+ "/"+ItemImage.pathNameTxt  );
			writeFile(pddToolPathFile, itemImage.getParentPath());
			System.out.println("拷贝第"+(j+1)+"/"+itemImages.size());
		}

		System.out.println("成功拷贝上货助手下载的图片到处理目录！");
	}

	public static void taskDelXQ(String distPath) {

		File directory = new File(distPath);
		File[] subDirs = directory.listFiles(new FileFilter() {
			public boolean accept(File file) {
				System.out.println(file);
				if (file.isDirectory()) {
					return true;
				}
				return false;
			}
		});

		int i = 1;
		for (File subDir : subDirs) {
			File[] pics = subDir.listFiles(new FileFilter() {
				public boolean accept(File file) {
					if (file.getName().contains("详情图")) {
						file.delete();
						// System.out.println(file.getName());
						return true;
					}
					return false;
				}
			});
			System.out.println(String.format("删除%s/%s", i++, subDirs.length));
		}

	}

	private static Date getDownloadTimeByScanner() {
		Date downLoadTime = null;
		try {
			int year = Calendar.getInstance().get(Calendar.YEAR);
			System.out.println("请输入上货助手待处理图片的下载时间，下载年份默认:" + year);
			System.out.println("请输入下载日期,以空格隔开(比如2 14代表 2月14日):");
			String rq = new Scanner(System.in).nextLine();
			System.out.println("请输入下载时间,以空格隔开(比如18 14代表 18点14分):");
			String sj = new Scanner(System.in).nextLine();

			String[] monthAndDay = rq.split(" ");
			String[] hourAndmin = sj.split(" ");

			int month = Integer.parseInt(monthAndDay[0]) - 1;
			int date = Integer.parseInt(monthAndDay[1]);
			int hourOfDay = Integer.parseInt(hourAndmin[0]);
			int minute = Integer.parseInt(hourAndmin[1]);
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month, date, hourOfDay, minute);
			downLoadTime = calendar.getTime();
			System.out.println("您输入的下载时间为："
					+ new SimpleDateFormat("yyyy-MM-dd HH:mm")
							.format(downLoadTime));
		} catch (Exception e) {
			System.err.println("输入时间格式错误");
			// e.printStackTrace();
		}

		return downLoadTime;

	}
	
	public static String getConfig(String key,String defaultValue) {
        String value = defaultValue;
        InputStreamReader reader =null;
        try {
            Properties pros = new Properties();
        	reader = new InputStreamReader(Object.class.getResourceAsStream("/1.txt"), "UTF-8");
            pros.load(reader);
            if(pros.get(key)!=null)
                value = pros.get(key).toString();
            
        } catch (Exception e) {
        	System.err.println("找不到资源文件，确认安装目录下有1.txt");
        	e.printStackTrace();
        }finally{
        	try {
        		reader.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
        }
        return value;
    }

	public static void main(String[] args) {
		
		String pddToolPicPath = getConfig("toolPath","D:/1-wd/Huatone/拼多多上货助理/downloadPic");
		String distPath = getConfig("handPath", "D:/1-pdd");
		String bakPath = distPath + "-bak";
		new File(bakPath).mkdirs();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		Date downLoadTime = null;
		try {
			downLoadTime = sdf.parse("2017-08-26 13:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println("==================================");
		System.out.println("       简易图片批量处理工具 by wubh");
		System.out.println("==================================");
		System.out.println("请确认上货助手的图片目录为:" + pddToolPicPath);
		System.out.println("请确认图片处理的目录为:" + distPath);
		while (true) {
			System.out.println("输入1 清理上货助手的图片目录");
			System.out.println("输入2 清理图片处理目录");
			System.out.println("输入3 拷贝上货助手待处理图片到处理目录(请先使用上货助手导出图片到处理目录，再运行)");
			System.out.println("输入4 P图完成后，拷贝回上货助手图片目录");
			Scanner sc = new Scanner(System.in);
			int type = -1;

			try {
				type = sc.nextInt();
			} catch (Exception e) {
				System.err.println("输入非法字符");
				return;
			}

			if (type == 1) {
				// 清理目录
				taskClearPddToolPic(pddToolPicPath);
			} else if (type == 2) {
				// 清理目录
				taskBeforeDownload(distPath, bakPath);
			} else if (type == 3) {
				// 先用工具导出图片,再把工具下的源图片 拷贝导出的目录进行P图。注意工具导出的文件数量要与拷贝目录一样。
				downLoadTime = getDownloadTimeByScanner();

				if (downLoadTime != null)
					taskBeforePtu(pddToolPicPath, distPath, bakPath,
							downLoadTime);
			} else if (type == 4) {
				// p图后，生成主图，主轮播图，400px图，200px图
				taskAfterPtu(distPath, new GenImgsByPicTask());
				taskAfterPtu(distPath, new CopyHandledImgBackTask());
			}

		}

	}

}

interface PicTask {
	public void handle(File defaultImg4genMain, File img400CopyFile,
			File img750CopyFile,String copyBackPath);
}

class GenImgsByPicTask implements PicTask {
	public void handle(File defaultImg4genMain, File img400CopyFile,
			File img750CopyFile,String copyBackPath) {
		String imgThumb400AbsPath = defaultImg4genMain.getParent() + "/"
				+ img400CopyFile.getName();
		String imgThumb640AbsPath = imgThumb400AbsPath.replace("hdthumb",
				"lunbo");
		String imgThumb200AbsPath = imgThumb400AbsPath.replace("hdthumb",
				"thumb");

		String img750AbsPath = img750CopyFile.getAbsolutePath();
		// 取第一张轮播图
		// 生成750主图
		ImgUtil.genMainImg(
				defaultImg4genMain.getAbsolutePath(), img750AbsPath);

		// 生成640高清图。
		try {
			ImgUtil.resizeImage(defaultImg4genMain.getAbsolutePath(),
					imgThumb640AbsPath, 640, "jpg");
			// 生成400高清图。
			ImgUtil.resizeImage(defaultImg4genMain.getAbsolutePath(),
					imgThumb400AbsPath, 400, "jpg");

			// 生成200高清图。
			ImgUtil.resizeImage(defaultImg4genMain.getAbsolutePath(),
					imgThumb200AbsPath, 200, "jpg");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}


class CopyHandledImgBackTask implements PicTask {

	public void handle(File defaultImg4genMain, File img400CopyFile,
			File img750CopyFile,String copyBackPath) {
		String distPath =copyBackPath;
		String imgThumb400AbsPath = defaultImg4genMain.getParent() + "/"
				+ img400CopyFile.getName();
		String imgThumb640AbsPath = imgThumb400AbsPath.replace("hdthumb",
				"lunbo");
		String imgThumb200AbsPath = imgThumb400AbsPath.replace("hdthumb",
				"thumb");
		String img750AbsPath = img750CopyFile.getAbsolutePath();
		File img640CopyFile = new File(imgThumb640AbsPath);
		File img200CopyFile = new File(imgThumb200AbsPath);

		ImgUtil.copyByFileChannel(new File(img750AbsPath), new File(distPath
				+ "/" + img750CopyFile.getName()));
		ImgUtil.copyByFileChannel(new File(imgThumb640AbsPath), new File(
				distPath + "/" + img640CopyFile.getName()));
		ImgUtil.copyByFileChannel(new File(imgThumb400AbsPath), new File(
				distPath + "/" + img400CopyFile.getName()));
		ImgUtil.copyByFileChannel(new File(imgThumb200AbsPath), new File(
				distPath + "/" + img200CopyFile.getName()));

	}
}

class ItemImage {
	private String img750AbsPath;
	private String imgThumb640AbsPath;
	private String imgThumb400AbsPath;
	private String imgThumb200AbsPath;
	private String imgThumb352AbsPath;
	private List<String> imgLunboAbsPaths = new ArrayList<String>();
	private String parentPath;

	public final static String pathNameTxt="pddToolImgPic.txt";
	public ItemImage(String parentPath, String imgThumb400Name) {
		this.parentPath = parentPath;
		this.imgThumb400AbsPath = parentPath + "/" + imgThumb400Name;
		this.imgThumb640AbsPath = imgThumb400AbsPath
				.replace("hdthumb", "lunbo");
		this.imgThumb200AbsPath = imgThumb400AbsPath
				.replace("hdthumb", "thumb");
		this.imgThumb352AbsPath = imgThumb400AbsPath.replace("hdthumb", "352");
	}

	public void genImg750(String backImgAbsPath) {
		try {
			ImgUtil.resizeImage(imgThumb640AbsPath, imgThumb352AbsPath, 352,
					"jpg");
			ImgUtil.mergeThumb(imgThumb352AbsPath, "jpg",
					img750AbsPath);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setImg750AbsPath(String img750Name) {
		this.img750AbsPath = parentPath + "/" + img750Name;
	}

	public void addLunboAbsPath(String imgLunboName) {
		if (!imgThumb640AbsPath.contains(imgLunboName)) {
			imgLunboAbsPaths.add(parentPath + "/" + imgLunboName);
		}
	}

	public String getImg750AbsPath() {
		return img750AbsPath;
	}

	public String getImgThumb640AbsPath() {
		return imgThumb640AbsPath;
	}

	public String getImgThumb400AbsPath() {
		return imgThumb400AbsPath;
	}

	public String getImgThumb200AbsPath() {
		return imgThumb200AbsPath;
	}

	public List<String> getImgLunboAbsPaths() {
		return imgLunboAbsPaths;
	}

	public String getParentPath() {
		return parentPath;
	}
	
	

	@Override
	public String toString() {
		return "img750AbsPath:" + img750AbsPath + "\n" + "imgThumb640AbsPath:"
				+ imgThumb640AbsPath + "\n" + "imgThumb400AbsPath:"
				+ imgThumb400AbsPath + "\n" + "imgThumb200AbsPath:"
				+ imgThumb200AbsPath + "\n" + "imgLunboAbsPaths:"
				+ imgLunboAbsPaths + "\n";
	}

}
