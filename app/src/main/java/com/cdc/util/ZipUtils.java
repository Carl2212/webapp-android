package com.cdc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.util.Log;

/**
 * 提供一个压缩、解压缩文件的工具类. <p>
 * 
 * @author：SnoopyChen(ceo@vmeitime.com)
  * @version 1.0
 */
public class ZipUtils {

		
	/*
	 * 对目录的压缩.
	 * @pamar out 压缩文件流
	 * @pamar f 目标文件
	 * @pamar base 基本目录
	 */
	private static void directoryZip(ZipOutputStream out, File f, String base) throws Exception {		
		if (f.isDirectory()) {// 如果传入的是目录
			File[] fl = f.listFiles();			
			out.putNextEntry(new ZipEntry(base + "/"));// 创建压缩的子目录
			if (base.length() == 0) {
				base = "";
			} else {
				base = base + "/";
			}
			for (int i = 0; i < fl.length; i++) {
				directoryZip(out, fl[i], base + fl[i].getName());
			}
		} else {
			// 把压缩文件加入rar中
			out.putNextEntry(new ZipEntry(base));
			FileInputStream in = new FileInputStream(f);
			byte[] bb = new byte[2048];
			int aa = 0;
			while ((aa = in.read(bb)) != -1) {
				out.write(bb, 0, aa);
			}
			in.close();
		}
	}

	/*
	 * 压缩单个文件.
	 * @pamar zos 压缩文件流
	 * @pamar file 目标文件
	 */
	private static void fileZip(ZipOutputStream zos, File file)	throws Exception {
		if (file.isFile()) {
			zos.putNextEntry(new ZipEntry(file.getName()));
			FileInputStream fis = new FileInputStream(file);
			byte[] bb = new byte[2048];
			int aa = 0;
			while ((aa = fis.read(bb)) != -1) {
				zos.write(bb, 0, aa);
			}
			fis.close();		
		} else {
			directoryZip(zos, file, "");
		}
	}
	
	/*
	 * 解压单个文件.
	 * @param zis 压缩文件流
	 * @param file 目标文件
	 * @param inFolder 是否解压到文件夹内（以压缩文件名做为文件夹名）
	 */
	public static void fileUnZip(ZipInputStream zis, File file)	throws Exception {
		
		ZipEntry zip = zis.getNextEntry();
		
		if (zip == null)
			return;
		String name = zip.getName();
		Log.d("--------", file.getAbsolutePath()+ "/" + name);
		File f = new File(file.getAbsolutePath() + "/" + name);
//		Log.d("fileName", file.getAbsolutePath() + "/" + name);
		if (zip.isDirectory()) {
			f.mkdirs();
			fileUnZip(zis, file);
		} else {
			File folder = f.getParentFile();
			if(!folder.exists()){
				folder.mkdirs();
			}
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			byte b[] = new byte[2048];
			int aa = 0;
			while ((aa = zis.read(b)) != -1) {
				fos.write(b, 0, aa);
			}
			fos.close();
			fileUnZip(zis, file);
		}
	}
	
	/**
	 * 对目标文件源进行压缩.
	 * @param directory 需要压缩的文件源
	 * @param zipFile 新的压缩文件名
	 */
	public static void zip(String directory, String zipFile) {
		try {
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
			fileZip(zos, new File(directory));
			zos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 对目标文件进行解压.
	 * @param directory 解压后的目标文件夹子
	 * @param zipFile 需要解压的压缩包
	 * @param inFolder 是否解压到文件夹内（以压缩文件名做为文件夹名）	
	 */
	public static void unZip(String directory, String zipFile, boolean inFolder) {
		try {
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
			File f = new File(directory);
			f.mkdirs();
			fileUnZip(zis, f);
			zis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
