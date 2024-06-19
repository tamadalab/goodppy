package org.goodppy;

import java.io.File;
import java.io.IOException;


/**
 * ビルドが正しく行えるかをチェックするクラス
 */
public class BuildChecker {
	public BuildChecker() {
		// コンストラクタ
	}

	/**
	 * @param localPath
	 */
	public void BuildCheck(String localPath) {
		try {
			System.out.println("Start the build...");
			ProcessBuilder builder = new ProcessBuilder();
			builder.command("gradle", "build");
			builder.directory(new File(localPath));
			Process process = builder.start();
			Integer exitCode = process.waitFor();
			if (exitCode != 0) {
				System.out.println("Build failed");
				return;
			}
			System.out.println("Build success");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return;
	}
}
