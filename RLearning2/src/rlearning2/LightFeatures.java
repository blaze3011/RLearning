package rlearning2;

public class LightFeatures {
	
	public double meanBL,meanBM, meanBR,meanTR,meanTL,meanTM,meanML,meanMM,meanMR, meanTot;
	
	public LightFeatures() {
		init_all();
	}
	
	private void init_all() {
		meanTL = 0.0;
		meanTM = 0.0;
		meanTR = 0.0;
		meanML = 0.0;
		meanMM = 0.0;
		meanMR = 0.0;
		meanBL = 0.0;
		meanBM = 0.0;
		meanBR = 0.0;
		meanTot = 0.0;
	}

	//Divides the screen into 9
	public void compFourScreen(byte [][] luminanceFrame, int height, int width){
		init_all();
		int height1 = height/3;
		int height2 = (height/3)*2;
		int width1 = width/3;
		int width2 = (width/3)*2;
		double totPix = (height*width);
		double segmentPix = totPix/9;
		
		//Scans the entirety of the top most frame
		for(int y = 0; y<height1; y++){
			//Scans Left
			for(int x = 0; x<width1; x++){
				meanTL += (double)(luminanceFrame[y][x] & 0xFF); 
				meanTot += (double) (luminanceFrame[y][x] & 0xFF);
			}
			//Scans Middle
			for(int x = width1; x <width2; x++){
				meanTM += (double)(luminanceFrame[y][x] & 0xFF);
				meanTot += (double) (luminanceFrame[y][x] & 0xFF);
			}
			//Scans Right
			for(int x = width2; x<width; x++){
				meanTR += (double)(luminanceFrame[y][x] & 0xFF); 
				meanTot += (double) (luminanceFrame[y][x] & 0xFF);
			}
		}
		
		//Scans the entirety of the middle frame
		for(int y = height1; y<height2; y++){
			//Scans Bottom Left
			for(int x = 0; x<width1; x++){
				meanML += (double)(luminanceFrame[y][x] & 0xFF); 
				meanTot += (double) (luminanceFrame[y][x] & 0xFF);
			}
			//Scans Bottom Middle
			for(int x = width1; x <width2; x++){
				meanMM += (double)(luminanceFrame[y][x] & 0xFF);
				meanTot += (double) (luminanceFrame[y][x] & 0xFF);
			}
			//Scans Bottom Right
			for(int x = width2; x<width; x++){
				meanMR += (double)(luminanceFrame[y][x] & 0xFF); 
				meanTot += (double) (luminanceFrame[y][x] & 0xFF);
			}
		}
		
		//Scans the entirety of the bottom most frame
		for(int y = height2; y<height; y++){
			//Scans Bottom Left
			for(int x = 0; x<width1; x++){
				meanBL += (double)(luminanceFrame[y][x] & 0xFF); 
				meanTot += (double) (luminanceFrame[y][x] & 0xFF);
			}
			//Scans Bottom Middle
			for(int x = width1; x <width2; x++){
				meanBM += (double)(luminanceFrame[y][x] & 0xFF);
				meanTot += (double) (luminanceFrame[y][x] & 0xFF);
			}
			//Scans Bottom Right
			for(int x = width2; x<width; x++){
				meanBR += (double)(luminanceFrame[y][x] & 0xFF); 
				meanTot += (double) (luminanceFrame[y][x] & 0xFF);
			}
		}
		
		//System.out.println(meanTL +" "+ meanTM +" "+ meanTR +" "+ meanML +" "+ meanMM +" "+ meanMR +" "+ meanBL +" "+ meanBM +" "+ meanBR);
		
		meanTL = meanTL/segmentPix;
		meanTM = meanTM/segmentPix;
		meanTR = meanTR/segmentPix;
		meanML = meanML/segmentPix;
		meanMR = meanMR/segmentPix;
		meanMM = meanMM/segmentPix;
		meanBL = meanBL/segmentPix;
		meanBM = meanBM/segmentPix;
		meanBR = meanBR/segmentPix;
		
		meanTot = meanTot/totPix;
		
	}

}
