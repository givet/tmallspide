import java.io.File;
import java.io.IOException;


public class QuickGenMain {
    public static void main(String[] args) throws IOException {
    	String imgThumb640AbsPath="C:\\Users\\dvlp\\Desktop\\ps\\2.jpeg";
    	String imgThumb352AbsPath=imgThumb640AbsPath+"352.jpeg";
    	String img750AbsPath="C:\\Users\\dvlp\\Desktop\\ps\\1.jpeg";
    	ImgUtil.resizeImage(imgThumb640AbsPath, imgThumb352AbsPath, 352,
				"jpg");
		ImgUtil.mergeThumb(imgThumb352AbsPath, "jpg",
				img750AbsPath);
		new File(imgThumb352AbsPath).delete();
	}
}
