package org.goodppy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * ビルドが正しく行えるかをチェックするクラス
 */
public class BuildChecker {

	/**
	 * ログファイルを保存するディレクトリのパス
	 */
	private String logFileDirectory;

	/**
	 * リポジトリのURL
	 */
	private String repositoryUrl;

	/**
	 * 評価するリポジトリについて操作を行う
	 */
	private RepositoryController repositoryController;

	/**
	 * コンストラクタ
	 *
	 * @param repositoryUrl リポジトリのURL
	 */
	public BuildChecker(String repositoryUrl) {
		this.repositoryController = new RepositoryController(repositoryUrl);
		// ./logs/build/owner/repositoryName/
		this.logFileDirectory = "./logs/build/"
				+ this.repositoryController.ownerAndRepositoryName();
		this.repositoryUrl = repositoryUrl;

		return;
	}

	/**
	 * ビルドが行えるかをチェックする
	 * 
	 * @param localPath     クローン先のディレクトリのパス
	 * @param repositoryUrl リポジトリのURL
	 */
	public void buildCheck(Path localPath) {
		try {
			String[] javaLTS = { "8", "11", "17", "21" };
			for (int i = 0; i < javaLTS.length; i++) {
				// String dockerfilePath = "./dockerfiles/java" + javaLTS[i];
				ProcessBuilder builder = new ProcessBuilder();
				System.out.println("Start the build...");
				builder.command("docker", "run", "-v", localPath.toString() + ":/app", "buildtest:java" + javaLTS[i]);
				builder.directory(new File("./"));
				createFile();
				builder.redirectErrorStream(true);
				Path logfile = Paths.get(generateLogfilePath(javaLTS[i]).toString());
				builder.redirectOutput(logfile.toFile());
				Process process = builder.start();
				long start = System.nanoTime();
				Integer exitCode = process.waitFor();
				if (exitCode != 0) {
					System.out.println("Build failed");
					long end = System.nanoTime();
					System.out.println("Time taked to build : " + (end - start) + "ns");

					continue;
				}
				System.out.println("Build success");
				long end = System.nanoTime();
				System.out.println("Time taked to build : " + (end - start) + "ns");
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return;
	}

	/**
	 * ログファイルを生成する
	 */
	public void createFile() {
		File logDirectory = new File(getLogfileDirectory());
		// File logFile = new File(generateLogfilePath().toString());
		// try {
		if (logDirectory.exists() == false) {
			logDirectory.mkdirs();
		}
		// if (logFile.exists() == false)
		// logFile.createNewFile();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		return;
	}

	/**
	 * ログファイルのパスを生成する
	 * 
	 * @return ログファイルのパス
	 */
	public Path generateLogfilePath(String javaLTS) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String logFile = getLogfileDirectory()
				// + this.repositoryController.getOwner()
				// + "_"
				// + this.repositoryController.getRepositoryName()
				// + "_"
				+ "java"
				+ javaLTS
				+ "_"
				+ sdf.format(calendar.getTime())
				+ ".log";
		Path logFilePath = Paths.get(logFile);

		return logFilePath;
	}

	/**
	 * ログファイルを保存するディレクトリのパスを取得する
	 * 
	 * @return ログファイルを保存するディレクトリのパス
	 */
	public String getLogfileDirectory() {
		return this.logFileDirectory;
	}

	/**
	 * リポジトリのURLを取得する
	 * 
	 * @return リポジトリのURL
	 */
	public String getRepositoryUrl() {
		return this.repositoryUrl;
	}
}
