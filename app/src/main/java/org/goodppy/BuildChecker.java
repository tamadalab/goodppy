package org.goodppy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * ビルドが正しく行えるかをチェックするクラス
 */
public class BuildChecker {
	/**
	 * ビルドの失敗
	 */
	private String failed = "failed";

	/**
	 * ビルドの成功
	 */
	private String successful = "successful";

	/**
	 * ログファイルを保存するディレクトリのパス
	 */
	private String logFileDirectory;

	/**
	 * リポジトリのURL
	 */
	private String repositoryUrl;

	/**
	 * ビルドの結果
	 */
	List<String> result = new ArrayList<String>();

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
	 * Gradleでビルドする
	 * 
	 * @param javaLTS   javaのLTS
	 * @param localPath クローン先のディレクトリのパス
	 * @return ビルド結果のリスト
	 */
	private List<String> buildByGradle(String[] javaLTS, Path localPath) {
		List<String> result = new ArrayList<String>();
		try {
			for (int i = 0; i < javaLTS.length; i++) {
				ProcessBuilder builder = new ProcessBuilder();
				System.out.println("Start the build by java" + javaLTS[i] + "...");
				builder.command("docker", "run", "--rm", "-v", localPath.toString() + ":/app",
						"fussan0424/buildtest-gradle:java" + javaLTS[i]);
				builder.directory(new File("./"));
				createFile();
				builder.redirectErrorStream(true);
				Path logfile = Paths.get(generateLogfilePath(javaLTS[i]).toString());
				builder.redirectOutput(logfile.toFile());
				Process process = builder.start();
				long start = System.nanoTime();
				Integer exitCode = process.waitFor();
				if (exitCode != 0) {
					System.out.println("Build failed by java" + javaLTS[i]);
					long end = System.nanoTime();
					System.out.println("Time taked to build : " + (end - start) + "ns");
					result.add(failed);

					continue;
				}
				System.out.println("Build successful by java" + javaLTS[i]);
				long end = System.nanoTime();
				System.out.println("Time taked to build : " + (end - start) + "ns");
				result.add(successful);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Mavenでビルドする
	 * 
	 * @param javaLTS   javaのLTS
	 * @param localPath クローン先のディレクトリのパス
	 * @return ビルド結果のリスト
	 */
	private List<String> buildByMaven(String[] javaLTS, Path localPath) {
		List<String> result = new ArrayList<String>();
		try {
			for (int i = 0; i < javaLTS.length; i++) {
				ProcessBuilder builder = new ProcessBuilder();
				System.out.println("Start the build by java" + javaLTS[i] + "...");
				builder.command("docker", "run", "--rm", "-v", localPath.toString() + ":/app",
						"fussan0424/buildtest-maven:java" + javaLTS[i]);
				builder.directory(new File("./"));
				createFile();
				builder.redirectErrorStream(true);
				Path logfile = Paths.get(generateLogfilePath(javaLTS[i]).toString());
				builder.redirectOutput(logfile.toFile());
				Process process = builder.start();
				long start = System.nanoTime();
				Integer exitCode = process.waitFor();
				if (exitCode != 0) {
					System.out.println("Build failed by java" + javaLTS[i]);
					long end = System.nanoTime();
					System.out.println("Time taked to build : " + (end - start) + "ns");
					result.add(failed);

					continue;
				}
				System.out.println("Build successful by java" + javaLTS[i]);
				long end = System.nanoTime();
				System.out.println("Time taked to build : " + (end - start) + "ns");
				result.add(successful);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * ビルドが行えるかをチェックする
	 * 
	 * @param localPath     クローン先のディレクトリのパス
	 * @param repositoryUrl リポジトリのURL
	 */
	public List<String> buildCheck(Path localPath) {
		String[] javaLTS = { "8", "11", "17", "21" };
		// repositoryController.replaceJCenter();
		Path settingFilePath = Paths.get(repositoryController.getLocalPath() + "/settings.gradle");
		Path ktsSettingFilePath = Paths.get(repositoryController.getLocalPath() + "/settings.gradle.kts");
		if (Files.exists(settingFilePath) || Files.exists(ktsSettingFilePath)) {
			this.result = buildByGradle(javaLTS, localPath);
		} else {
			this.result = buildByMaven(javaLTS, localPath);
		}

		return this.result;
	}

	/**
	 * ログファイルを生成する
	 */
	public void createFile() {
		File logDirectory = new File(getLogfileDirectory());
		if (logDirectory.exists() == false) {
			logDirectory.mkdirs();
		}

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
