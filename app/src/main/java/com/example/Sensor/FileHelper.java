package com.example.Sensor;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.content.Context;
import android.os.Environment;

public class FileHelper {
	private Context context;
	/** SD card is exist? **/
	private boolean hasSD = false;
	/** the SD card's path **/
	private String SDPATH;
	/** the current software package's path **/
	private String FILESPATH;

	public FileHelper(Context context) {
		this.context = context;
		hasSD = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		SDPATH = Environment.getExternalStorageDirectory().getPath();
		FILESPATH = this.context.getFilesDir().getPath();
	}

	/**
	 * create file in SD card
	 * 
	 * @throws IOException
	 */
	public File createSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + "//" + fileName);
		if(file.exists())
		{
			file.delete();
		}
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	/**
	 * delete file in SD card
	 * 
	 * @param fileName
	 */
	public boolean deleteSDFile(String fileName) {
		File file = new File(SDPATH + "//" + fileName);
		if (file == null || !file.exists() || file.isDirectory())
			return false;
		return file.delete();
	}

	/**
	 * write content to the txt file in SD card,the content is str
	 */
	public void writeSDFile(String str, String fileName) {
		try {
			FileWriter fw = new FileWriter(SDPATH + "//" + fileName);
			File f = new File(SDPATH + "//" + fileName);
			fw.write(str);
			FileOutputStream os = new FileOutputStream(f);
			os.write(str.getBytes("gbk"));//to avoid chinese garbled when write to file,but the problem also happens in the end of the txt file
			DataOutputStream out = new DataOutputStream(os);
			out.writeShort(2);
			out.writeUTF("");
			System.out.println(out);
			fw.flush();
			fw.close();
			System.out.println(fw);
		} catch (Exception e) {
		}
	}

	/**
	 * read the txt file in SD card
	 * 
	 * @param fileName
	 * @return
	 */
	public String readSDFile(String fileName) {
		StringBuffer sb = new StringBuffer();
		File file = new File(SDPATH + "//" + fileName);
		try {
			FileInputStream fis = new FileInputStream(file);
			int c;
			while ((c = fis.read()) != -1) {
				sb.append((char) c);
			}
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public String getFILESPATH() {
		return FILESPATH;
	}

	public String getSDPATH() {
		return SDPATH;
	}

	public boolean hasSD() {
		return hasSD;
	}
}
