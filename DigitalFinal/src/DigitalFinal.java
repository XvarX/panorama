import java.awt.image.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.*;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Spring;
public class DigitalFinal {
	public static void main(String args[]) throws IOException
	{
		JFrame frame = new JFrame();
		label = new JLabel();
		frame.setSize(1400,800);
		frame.add(label);
		stitching();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	public static void imageshow(BufferedImage img_buff) {
		ImageIcon newicon = new ImageIcon(img_buff);
		label.setIcon(newicon);
	}
	public static int[][]greyValue(BufferedImage img_buff) {
		BufferedImage bufferedImage = img_buff;
		int ImageWidth = img_buff.getWidth();
		int ImageHeight = img_buff.getHeight();
		int greyValue[][] = new int[ImageWidth][ImageHeight];
		int i = 0;
		int j = 0;
		while (i < ImageWidth) {
			j = 0;
			while (j < ImageHeight) {
				int a = bufferedImage.getRGB(i,j);
			    int r  =(a & 0xff0000) >> 16;
		        int g = (a & 0xff00) >> 8;
		        int b = a & 0xff;
			    int grey = (int) (r * 0.3 + g * 0.59 + b * 0.11);
			    greyValue[i][j] = grey;
			    j++;
			}
			i++;
		}
		return greyValue;
	}
	public static int[][] findSpecialValue(BufferedImage img_buff) {
		BufferedImage bufferedImage = img_buff;
		int ImageWidth = img_buff.getWidth();
		int ImageHeight = img_buff.getHeight();
		BufferedImage greyImage = new BufferedImage(ImageWidth,ImageHeight,BufferedImage.TYPE_INT_RGB);
		BufferedImage InterestImage = new BufferedImage(ImageWidth,ImageHeight,BufferedImage.TYPE_INT_RGB);
		int i = 0;
		int j = 0;
		while (i < ImageWidth) {
			j = 0;
			while (j < ImageHeight) {
				int a = img_buff.getRGB(i,j);
			    int r  =(a & 0xff0000) >> 16;
		        int g = (a & 0xff00) >> 8;
		        int b = a & 0xff;
			    int grey = (int) (r * 0.3 + g * 0.59 + b * 0.11);
			    int rgb=((grey*256)+grey)*256+grey;
			    greyImage.setRGB(i, j, rgb);
			    j++;
			}
			i++;
		}
		
		int greyValue[][] = new int[ImageWidth][ImageHeight];
		i = 0;
		j = 0;
		while (i < ImageWidth) {
			j = 0;
			while (j < ImageHeight) {
				int a = bufferedImage.getRGB(i,j);
			    int r  =(a & 0xff0000) >> 16;
		        int g = (a & 0xff00) >> 8;
		        int b = a & 0xff;
			    int grey = (int) (r * 0.3 + g * 0.59 + b * 0.11);
			    greyValue[i][j] = grey;
			    j++;
			}
			i++;
		}
		
		
		//兴趣点
		int IntereValue[][] = new int[ImageWidth][ImageHeight];
		int value[] = new int[4];
		i = 0;
		j = 0;
		while (i < ImageWidth) {
			j = 0;
			while (j < ImageHeight) {
				//竖向
				int turni = -1;
				value[0] = 0;
				while (turni <= 1) {
					if (i+turni >= 0&&i+1+turni < ImageWidth) {
						int turn = (greyValue[i+turni][j]-greyValue[i+turni+1][j]);
						turn = turn*turn;
						value[0]+=turn;
					}
					turni++;
				}
				//横向
				turni = -1;
				value[1] = 0;
				while (turni <= 1) {
					if (j+turni >= 0&&j+1+turni < ImageHeight) {
						int turn = (greyValue[i][j+turni]-greyValue[i][j+turni+1]);
						turn = turn*turn;
						value[1]+=turn;
					}
					turni++;
				}
				//斜向1
				turni = -1;
				value[2] = 0;
				while (turni <= 1) {
					if (i+turni >= 0&&i+1+turni < ImageWidth&&j+turni >= 0&&j+1+turni < ImageHeight) {
						int turn = (greyValue[i+turni][j+turni]-greyValue[i+turni+1][j+turni+1]);
						turn = turn*turn;
						value[2]+=turn;
					}
					turni++;
				}
				//斜向2
				turni = -1;
				value[3] = 0;
				while (turni <= 1) {
					if (j+turni >= 0&&j+1+turni < ImageHeight&&i-turni >= 0&&i-turni+1 < ImageWidth) {
						int turn = (greyValue[i-turni][j+turni]-greyValue[i-turni+1][j+turni+1]);
						turn = turn*turn;
						value[3]+=turn;
					}
					turni++;
				}
				
				
				//find minimal
				int minivalue = value[0];
				for (int minii = 0;minii < 4;minii++) {
					if (value[minii] < minivalue){
						minivalue = value[minii];
					}
				}
				IntereValue[i][j] = minivalue;
				
				j++;
			}
			i++;
		}
		i = 0;
		j = 0;
		
		//阙值
		int afterValue[][] = new int[ImageWidth][ImageHeight];
		while (i < ImageWidth) {
			j = 0;
			while (j < ImageHeight) {
				if (IntereValue[i][j] < 500) {
					afterValue[i][j] = 0; 
				} else {
					afterValue[i][j] = IntereValue[i][j];
				}
				j++;
			}
			i++;
		}
		int specialValue[][] = new int[ImageWidth][ImageHeight];
		i = 0;
		j = 0;
		int biggest = 0;
		int windowWidth = 3;
		int windowHeight = 3;
		int windowi = 0;
		int windowj = 0;
		int biggesti = 0;
		int biggestj = 0;
		while (i + windowWidth < ImageWidth) {
			j = 0;
			while (j+windowHeight < ImageHeight) {
				biggest = 0;
				biggesti = 0;
				biggestj = 0;
				windowi = 0;
				windowj = 0;
				while (windowi < windowWidth) {
					windowj = 0;
					while (windowj < windowHeight) {
						if (afterValue[i+windowi][j+windowj] > biggest) {
							biggest = afterValue[i+windowi][j+windowj];
							biggesti = i+windowi;
							biggestj = j+windowj;
						}
						windowj++;
					}
					windowi++;
				}
				specialValue[biggesti][biggestj]=biggest;
				j++;
			}
			i++;
		}
		i = 0;
		j = 0;
		while (i < ImageWidth) {
			j = 0;
			while (j < ImageHeight) {
				if (specialValue[i][j] != 0) {
					int rgb=((255*256)+0)*256+0;
					InterestImage.setRGB(i, j, rgb);
				} else {
					InterestImage.setRGB(i, j, greyImage.getRGB(i, j));
				}
				j++;
			}
			i++;
		}
		imageshow(InterestImage);
		return specialValue;
	}
	public static double computeVariance(Vector array) {
		double ave = 0;
        for (int i = 0; i < array.size(); i++)
            ave = ave + (int)array.elementAt(i);
        ave /= array.size();
        
        double sum = 0;
        for(int i = 0;i<array.size();i++)
            sum += ((int)array.elementAt(i) - ave)  * ((int)array.elementAt(i) - ave) ;
        sum /= array.size();
        return sum;
	}
	public static void stitching() throws IOException {
		File file1 = new File("C:\\Users\\Xvar\\Desktop\\test\\test1.jpg");
		File file2 = new File("C:\\Users\\Xvar\\Desktop\\test\\test2.jpg");
		BufferedImage bufferedImage1 = ImageIO.read(file1);
		BufferedImage bufferedImage2 = ImageIO.read(file2);
		int[][] greyValue1 = greyValue(bufferedImage1);
		int[][] greyValue2 = greyValue(bufferedImage2);
		int[][] specialValue1 = findSpecialValue(bufferedImage1);
		int[][] specialValue2 = findSpecialValue(bufferedImage2);
		int ImageWidth1 = bufferedImage1.getWidth();
		int ImageHeight1 = bufferedImage1.getHeight();
		int ImageWidth2 = bufferedImage2.getWidth();
		int ImageHeight2 = bufferedImage2.getHeight();
		int i = 0;
		int j = 0;
		while (i < ImageWidth1) {
			j = 0;
			while (j < ImageHeight1) {
				if(specialValue1[i][j] != 0) {
					int x=0;
					int y=0;
					while (x < ImageWidth2) {
						y = 0;
						while (y < ImageHeight2) {
							if (Math.abs(x-i)<=(1/4)*ImageWidth2 && Math.abs(y-j) <= (1/4)*ImageHeight2) {
								if (specialValue2[x][y] != 0) {
								    int windowWidth = 3;
								    int windowHeight = 3;
								    int windowi = -1;
								    int windowj = -1;
								    Vector windowgrey = new Vector();
								    double mean = 0;
								    while (windowi <= 1) {
									    windowj = -1;
									    while (windowj <= 1) {
										    if (i+windowi >=0 && i+windowi < ImageWidth1){
											    if (j+windowj >=0&&j+windowj<ImageHeight1){
											    	Integer integer1 = new Integer(greyValue1[i+windowi][j+windowj]);
											    	windowgrey.add(integer1);
											    	mean += integer1;
											    }
										    }
										    windowj++;
										
										}
									    windowi++;
									}
								    mean /= windowgrey.size();
								    double variance1 = computeVariance(windowgrey);
								}
							}
							y++;
						}
						x++;
					}
				}
				j++;
			}
			i++;
		}
	}
	private static JLabel label;
}