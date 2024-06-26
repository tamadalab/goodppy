package org.goodppy;

import java.io.File;
import java.io.IOException;

/**
 * ビルドが正しく行えるかをチェックするクラス
 */
public class BuildChecker {
	/**
	 * コンストラクタ
	 */
	public BuildChecker() {
		// コンストラクタ
	}

	/**
	 * ビルドが行えるかをチェックする
	 * 
	 * @param localPath クローン先のディレクトリのパス
	 */
	public void buildCheck(String localPath) {
		try {
			System.out.println("Start the build...");
			long start = System.currentTimeMillis();
			ProcessBuilder builder = new ProcessBuilder();
			builder.command("gradle", "build"); // Gradleのビルドを行う
			builder.directory(new File(localPath));
			Process process = builder.start();
			Integer exitCode = process.waitFor();
			if (exitCode != 0) {
				System.out.println("Build failed");
				long end = System.currentTimeMillis();
				System.out.println("Time taken to build : " + (end - start) + "ms");
				return;
			}
			System.out.println("Build success");
			long end = System.currentTimeMillis();
			System.out.println("Time taken to build : " + (end - start) + "ms");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return;
	}
}
