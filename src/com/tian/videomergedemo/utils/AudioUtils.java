package com.tian.videomergedemo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
public class AudioUtils {
	
	
	
	
	
//	private int size;




	public  static  boolean hebing(List<String> paths,String savaPath) {
		try {
		for (int i = 0; i < paths.size(); i++) {
			FileOutputStream fos = new FileOutputStream(savaPath + "bbbb.wav");
			FileInputStream fis = new FileInputStream(paths.get(i));
			byte[] temp = new byte[fis.available()];
			int len = temp.length;
			if (i == 0) {
				while (fis.read(temp) > 0) {
					fos.write(temp, 0, len);
				}
			} else {
				while (fis.read(temp) > 0) {
					fos.write(temp, 44, len - 44);
				}
			}
			fos.flush();
			fis.close();
			}
				
			} catch (IOException e) {
				return false;
			}
		
		return true;
	
	}
	
	/**
	 * 
	 * @param partsPaths 要合成的音频路径数组
	 * @param unitedFilePath 输入合并结果数组
	 */
	public static boolean uniteWavFile(List<String>  partsPaths, String unitedFilePath) {
		
		
		for(int i=0;i<partsPaths.size();i++){
			File f = new File(partsPaths.get(i));
			try {
				InputStream in=new FileInputStream(f);
				byte bytes[] = new byte[44];
				in.read(bytes);
				
				for(int j=0;j<bytes.length;j++){
					System.out.println("--------->"+bytes[i]);
				}
				
			} catch (FileNotFoundException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
			
		}

			return true;
	}

	private static byte[] getByte(String path){
		File f = new File(path);
		InputStream in;
		byte bytes[] = null;
		try {
			in = new FileInputStream(f);
			bytes = new byte[(int) f.length()];
			in.read(bytes);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bytes;
	}
	
	
	
	    
		/**
		 * merge *.wav files 
		 * @param target  output file
		 * @param paths the files that need to merge
		 * @return whether merge files success
		 */
	    public static boolean mergeAudioFiles(String target,List<String> paths) {
	    	try {
		        FileOutputStream fos = new FileOutputStream(target);	        
		        int size=0;
		        byte[] buf = new byte[1024 * 1000];
		        int PCMSize = 0;
		        for(int i=0;i<paths.size();i++){
		        	FileInputStream fis = new FileInputStream(paths.get(i));
		        	size = fis.read(buf);
		        	 while (size != -1){
		 	            PCMSize += size;
		 	            size = fis.read(buf);
		 	        }
		 	        fis.close();
		        }
		        PCMSize=PCMSize-paths.size()*44;
		        WaveHeader header = new WaveHeader();
		        header.fileLength = PCMSize + (44 - 8);
		        header.FmtHdrLeth = 16;
		        header.BitsPerSample = 16;
		        header.Channels = 1;
		        header.FormatTag = 0x0001;
		        header.SamplesPerSec = 16000;
		        header.BlockAlign = (short) (header.Channels * header.BitsPerSample / 8);
		        header.AvgBytesPerSec = header.BlockAlign * header.SamplesPerSec;
		        header.DataHdrLeth = PCMSize;
		        byte[] h = header.getHeader();
		        assert h.length == 44;
		        fos.write(h, 0, h.length);
		        for(int j=0;j<paths.size();j++){
		        	FileInputStream fis = new FileInputStream(paths.get(j));
			        size = fis.read(buf);
			        boolean isFirst=true;
			        while (size != -1){
			        	if(isFirst){
			        		fos.write(buf, 44, size-44);
				            size = fis.read(buf);
				            isFirst=false;
			        	}else{
			        		fos.write(buf, 0, size);
				            size = fis.read(buf);
			        	}
			        }
			        fis.close();
		        }
		        fos.close();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
	        return true;
	    }
	    
	    
	    
	    /**
	     * 
	     * 文件的部分剪辑（单端删除类型）
	     * 
	     * @param target  目标输出文件
	     * @param dest 源文件（wav）
	     * @param startPos  开始开始剪贴点
	     * @param endPos  剪贴结束点
	     * @param pcmTarget  源pcm文件
	     * @return
	     */
	    public static boolean cutAudioFiles(String target,String dest,int cutPos,int endPos,String pcmTarget,String pamOut) {
	    	try {
	    		
	    		File file = new File(pcmTarget);
	    		long fileSize = file.length();
		        FileOutputStream fos = new FileOutputStream(target);	        
		        FileOutputStream fos1 = new FileOutputStream(pamOut);	        
		       
		        int size=0;
		        int PCMSize = 0;
		        PCMSize=(int) (fileSize-(endPos-cutPos));
		       
		        WaveHeader header = new WaveHeader();
		        header.fileLength = PCMSize + (44 - 8);
		        header.FmtHdrLeth = 16;
		        header.BitsPerSample = 16;
		        header.Channels = 1;
		        header.FormatTag = 0x0001;
		        header.SamplesPerSec = 16000;
		        header.BlockAlign = (short) (header.Channels * header.BitsPerSample / 8);
		        header.AvgBytesPerSec = header.BlockAlign * header.SamplesPerSec;
		        header.DataHdrLeth = PCMSize;
		        byte[] h = header.getHeader();
		        assert h.length == 44;
		        
		        
		        fos.write(h, 0, h.length);
		        FileInputStream fis = new FileInputStream(dest);
		        FileInputStream fis1 = new FileInputStream(file);
		       
		        
		        byte[] buf = new byte[cutPos];
		        byte[] buf2 = new byte[cutPos];
		        
			    //读前半部分
		        fis.read(buf);
		        fis1.read(buf2);
		        
		        
		        //写前半部分
		        fos1.write(buf2);
		        if(buf.length>44){
		        	fos.write(buf, 44, buf.length-44);
		        }
		        //计算第二部分的长度
			    byte[] buf1 = new byte[(int) (fileSize-endPos)];
			    byte[] buf3 = new byte[(int) (fileSize-endPos)];
			    
			    
			    //如果不是末尾点，则进行跳读
			    if(buf1.length!=0){
			    	//wav 文件格式的读取
			    	 fis.skip(endPos);//流字节跳转到末尾点
					 fis.read(buf1);//末尾点到文件的总长度
			    	 fos.write(buf1, 0, buf1.length);
			    	 
			    	 
			    	 //pcm 文件的格式的读取
			    	 fis1.skip(endPos);
			    	 fis1.read(buf3);
			    	 fos1.write(buf3);
			    }
			    fis.close();
		        fos.close();
		        fos1.close();
		        buf1=null;
		        buf2=null;
		        buf3=null;
		        file.delete();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
	        return true;
	    }
	    
	    
	    
	    
	    
	    /**
	     * 音频的多段操作（pcm格式音频）
	     * @param dstPath  源文件
	     * @param outPath  输出文件 
	     * @param cutArea  裁剪编辑的区域
	     */
	    public static void getPcmEdits(String dstPath,String outPath,List<long[]> cutAreas){
	    	
	    	File dstFile=new File(dstPath);
	    	File outFile=new File(outPath);
	    	int dstFileLength=(int) dstFile.length();
	    	if(dstFile.exists()&&cutAreas!=null){
	    		try {
	    		FileInputStream in=new FileInputStream(dstFile);
	    		FileOutputStream out=new FileOutputStream(outFile);
//	    		for(int i=0;i<cutAreas.size();i++){
//	    			int[] cutArea=cutAreas.get(i);
//	    			totalSize=totalSize+(cutArea[1]-cutArea[0]);
//	    		}
//	    		WaveHeader header = new WaveHeader();
//		        header.fileLength = totalSize + (44 - 8);
//		        header.FmtHdrLeth = 16;
//		        header.BitsPerSample = 16;
//		        header.Channels = 1;
//		        header.FormatTag = 0x0001;
//		        header.SamplesPerSec = 16000;
//		        header.BlockAlign = (short) (header.Channels * header.BitsPerSample / 8);
//		        header.AvgBytesPerSec = header.BlockAlign * header.SamplesPerSec;
//		        header.DataHdrLeth = totalSize;
//		        byte[] h = header.getHeader();
//		        assert h.length == 44;
//		        out.write(h, 0, h.length);
		        int index=0;
		        while(index<cutAreas.size()){
		        		if(index==0){
		        			if(cutAreas.size()>1){
		        				if(cutAreas.get(index)[0]!=0){
		        					byte[] buf = new byte[(int) cutAreas.get(index)[0]];
				        			in.read(buf);
				        			out.write(buf);
		        				}
		        				
		        			}else{
		        				if(cutAreas.get(index)[0]!=0){
		        					byte[] buf = new byte[(int) cutAreas.get(index)[0]];
				        			in.read(buf);
				        			out.write(buf);
		        				}
		        				
		        				if((dstFileLength-(int) cutAreas.get(index)[1])!=0){
		        					byte[] buf1 = new byte[dstFileLength-(int) cutAreas.get(index)[1]];
				        			in.skip(cutAreas.get(index)[1]);
				        			in.read(buf1);
				        			out.write(buf1);
		        				}
			        			
		        			}
		        		}else{
		        			byte[] buf = new byte[(int) (cutAreas.get(index)[0]-cutAreas.get(index-1)[1])];
		        			in.skip(cutAreas.get(index-1)[1]);
		        			in.read(buf);
		        			out.write(buf);
		        			if(cutAreas.get(index)[1]<dstFileLength){
		        				byte[] buf1 = new byte[(int) (dstFileLength-cutAreas.get(index)[1])];
			        			in.skip(cutAreas.get(index)[1]);
			        			in.read(buf1);
			        			out.write(buf1);
		        			}
		        		}
		        		index=index+1;
		        	}
		        //读写完毕
		        in.close();
		        out.close();
		        dstFile.delete();
//		        outFile.renameTo(dstFile);
		        pcm2wav(outFile.getAbsolutePath(),outFile.getAbsolutePath().replace(".pcm", ".wav"));
	    		} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    }
	    
	    
	    
	    /**
	     * pcm转换成wav
	     * @param pcmPath
	     * @param wavPath
	     */
	    public static void pcm2wav(String pcmPath,String wavPath){
    			Pcm2Wav p2w = new Pcm2Wav();
    			try {
					p2w.convertAudioFiles(pcmPath, wavPath);
				} catch (Exception e) {
					e.printStackTrace();
				}
	    	
	    }
	    
	    
	    
	    
	    
	    
	    
	    /**
	     * 根据当前的像素点位置，获取对应的当前数据源的位置（有误差）
	     * 
	     * @param totlePixs   waveView控件的总长度
	     * @param currentPixs 当前像素点
	     * @param pcmSize     pcm数据的总长 
	     * @return
	     */
	    public static long getCurrentPos(int totlePixs,int currentPixs,long pcmSize){
	    	long result =0;
	    	if(totlePixs!=0){
	    		result=pcmSize*currentPixs/totlePixs;
	    	}
	    	return result;
	    	
	    	
	    }
	    

}
