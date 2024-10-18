package org.goodppy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

/**
 * 評価を行うクラス
 */
public class Evaluate {
	/**
	 * ビルドの結果リスト
	 */
	private List<String> buildResult = new ArrayList<String>();

	/**
	 * 依存関係の結果リスト
	 */
	private List<String> dependencyResult;

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
	public Evaluate(String repositoryUrl) {
		this.repositoryController = new RepositoryController(repositoryUrl);
		this.dependencyResult = new ArrayList<String>();
		this.repositoryUrl = repositoryUrl;

		return;
	}

	/**
	 * ビルドについて評定する
	 * 
	 * @param result ビルドの是非
	 */
	// public void evaluateBuild(List<String> result) {
	// this.buildResult = result;

	// return;
	// }

	/**
	 * 依存関係について評定する
	 */
	public void evaluateDependency() {
		DependencyChecker dependencyChecker = new DependencyChecker(getRepositoryUrl());
		List<String> result = new ArrayList<String>();
		Double score = 0.0;
		Integer[] scores = dependencyChecker.csvDataControl();
		Integer critical = scores[0] / 4;
		Integer high = scores[1] / 3;
		Integer medium = scores[2] / 2;
		Integer low = scores[3];
		for (int i = 0; i < scores.length - 1; i++) {
			score += scores[i];
		}
		if (scores[4] != 0) {
			score /= scores[4];
		}

		result.add(String.format("%.1f", score));
		result.add(critical.toString());
		result.add(high.toString());
		result.add(medium.toString());
		result.add(low.toString());
		this.dependencyResult = result;
		System.out.printf("score: %.1f\ncritical: %d high %d medium: %d low: %d\n", score, critical, high, medium, low);

		return;
	}

	/**
	 * リポジトリのURLを取得する
	 * 
	 * @return リポジトリのURL
	 */
	public String getRepositoryUrl() {
		return this.repositoryUrl;
	}

	/**
	 * ビルドの結果リストを取得する
	 * 
	 * @return ビルドの結果リスト
	 */
	public List<String> getBuildResult() {
		return this.buildResult;
	}

	/**
	 * CSVファイルに書き込む
	 * 
	 * @param buildResult ビルドの結果リスト
	 */
	public void writeCsv(List<String> buildResult) {
		this.buildResult = buildResult;
		String directoryFilePathString = "./result/"
				+ this.repositoryController.ownerAndRepositoryName();
		String outputFile = "/result.csv";
		String[] header = { "repositoryName", "build_java8", "build_java11", "build_java17", "build_java21",
				"dependencyScore", "critical", "high", "medium", "low" };
		String[] data = { this.repositoryController.getOwner() + "/" + this.repositoryController.getRepositoryName(),
				this.buildResult.get(0), this.buildResult.get(1), this.buildResult.get(2), this.buildResult.get(3),
				this.dependencyResult.get(0), this.dependencyResult.get(1),
				this.dependencyResult.get(2),
				this.dependencyResult.get(3), this.dependencyResult.get(4)
		};
		File directoryFilePath = new File(directoryFilePathString);
		if (directoryFilePath.exists() == false) {
			directoryFilePath.mkdirs();
		}
		try (CSVWriter writer = new CSVWriter(new FileWriter(directoryFilePath + outputFile))) {
			writer.writeNext(header);
			writer.writeNext(data);
			System.out.println("Make result.");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}
}
