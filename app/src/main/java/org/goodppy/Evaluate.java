package org.goodppy;

/**
 * 評価を行うクラス
 */
public class Evaluate {
	/**
	 * リポジトリのURL
	 */
	private String repositoryUrl;

	public Evaluate(String repositoryUrl) {
		// コンストラクタ
		this.repositoryUrl = repositoryUrl;

		return;
	}

	public void evaluateDependency() {
		DependencyChecker dependencyChecker = new DependencyChecker(getRepositoryUrl());
		Integer score = 0;
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
		System.out.printf("score: %d\ncritical: %d high %d medium: %d low: %d\n", score, critical, high, medium, low);
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
}
