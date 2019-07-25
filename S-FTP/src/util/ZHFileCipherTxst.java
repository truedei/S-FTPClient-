package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class ZHFileCipherTxst {
	
	public static byte[] key = new byte[] { 49, 38, -88, -75, 103, -50, 94, -92 }; // 字节数必须是8的整数倍
	
	/**
	 * DES加密介绍 DES是一种对称加密算法，所谓对称加密算法即：加密和解密使用相同密钥的算法。
	 * 注意：DES加密和解密过程中，密钥长度都必须是8的倍数
	 */
	private static String Algorithm = "DES"; // 定义 加密算法,可用DES,DESede,Blowfish

	// static {
	// Security.addProvider(new com.sun.crypto.provider.SunJCE());
	// }

	// 生成密钥, 注意此步骤时间比较长
	public static byte[] getKey() throws Exception {
		KeyGenerator keygen = KeyGenerator.getInstance(Algorithm);
		keygen.init(new SecureRandom());
		SecretKey deskey = keygen.generateKey();
		return deskey.getEncoded();
	}

	/**
	 * 加密
	 * 
	 * @param enfile 要加密的文件
	 * @param defile 加密后的文件
	 * @param key    密钥
	 * @throws Exception
	 */
	public static void encode(String enfile, String defile, byte[] key) throws Exception {
		// 秘密（对称）密钥(SecretKey继承(key))
		// 根据给定的字节数组构造一个密钥。
		SecretKey deskey = new SecretKeySpec(key, Algorithm);
		// 生成一个实现指定转换的 Cipher 对象。Cipher对象实际完成加解密操作
		Cipher c = Cipher.getInstance(Algorithm);
		// 用密钥初始化此 cipher
		c.init(Cipher.ENCRYPT_MODE, deskey);

		byte[] buffer = new byte[1024];
		FileInputStream in = new FileInputStream(enfile);
		OutputStream out = new FileOutputStream(defile);

		CipherInputStream cin = new CipherInputStream(in, c);
		int i;
		while ((i = cin.read(buffer)) != -1) {
			out.write(buffer, 0, i);
		}
		out.close();
		cin.close();
	}

	// 解密
	public static void decode(String file, String defile, byte[] key) throws Exception {

		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 创建一个 DESKeySpec 对象,指定一个 DES 密钥
		DESKeySpec ks = new DESKeySpec(key);
		// 生成指定秘密密钥算法的 SecretKeyFactory 对象。
		SecretKeyFactory factroy = SecretKeyFactory.getInstance(Algorithm);
		// 根据提供的密钥规范（密钥材料）生成 SecretKey 对象,利用密钥工厂把DESKeySpec转换成一个SecretKey对象
		SecretKey sk = factroy.generateSecret(ks);
		// 生成一个实现指定转换的 Cipher 对象。Cipher对象实际完成加解密操作
		Cipher c = Cipher.getInstance(Algorithm);
		// 用密钥和随机源初始化此 cipher
		c.init(Cipher.DECRYPT_MODE, sk, sr);

		byte[] buffer = new byte[1024];
		FileInputStream in = new FileInputStream(file);
		OutputStream out = new FileOutputStream(defile);
		CipherOutputStream cout = new CipherOutputStream(out, c);
		int i;
		while ((i = in.read(buffer)) != -1) {
			cout.write(buffer, 0, i);
		}
		cout.close();
		in.close();
	}

	public static void main(String[] args) throws Exception {
		// byte[] b = getKey();
		// System.out.println(b.toString());
		
		// 文件加密
//        encode("D:/a.bat", "D:/b.bat", key);
		encode("E:/a.txt", "E:/b.txt", key);

		// 文件解密
//        decode("D:/b.bat", "D:/c.bat", key);
		decode("E:/b.txt", "E:/c.txt", key);
	}
}