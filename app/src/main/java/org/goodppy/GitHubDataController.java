package org.goodppy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import com.opencsv.CSVWriter;

/**
 * GitHub上のデータについて操作を行うクラス
 */
public class GitHubDataController {
	/**
	 * GitHub APIのURL
	 */
	private static final String GITHUB_API_URL = "https://api.github.com/repos/";

	/**
	 * GitHub APIのURL
	 */
	private String apiUrl;

	/**
	 * リポジトリのURL
	 */
	private String repositoryUrl;

	/**
	 * リポジトリのオーナー名
	 */
	private String owner;

	/**
	 * リポジトリ名
	 */
	private String repositoryName;

	/**
	 * 評価するリポジトリについて操作を行う
	 */
	private RepositoryController repositoryController;

	/**
	 * GitHub APIのトークン
	 */
	private String token;

	/**
	 * コンストラクタ
	 * 
	 * @param repositoryUrl リポジトリのURL
	 */
	public GitHubDataController(String repositoryUrl) {
		this.repositoryController = new RepositoryController(repositoryUrl);
		this.owner = this.repositoryController.getOwner();
		this.repositoryName = this.repositoryController.getRepositoryName();
		this.apiUrl = GITHUB_API_URL + getOwner() + "/" + getRepositoryName();
		this.repositoryController = new RepositoryController(repositoryUrl);
		this.repositoryUrl = this.repositoryController.getRepositoryUrl();
		this.token = getToken();

		return;
	}

	/**
	 * GitHub上のデータを収取する
	 */
	public void collectData() {
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = createRequest(getApiUrl());

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				System.out.println("Start collect data.");
				JSONObject data = new JSONObject(response.body());
				Integer stars = Integer.valueOf(data.getInt("stargazers_count"));
				Integer forks = Integer.valueOf(data.getInt("forks_count"));
				Integer contributors = getContributorsCount(client);
				Integer openIssues = Integer.valueOf(data.getInt("open_issues_count"));
				Integer closedIssues = Integer.valueOf(getClosedIssuesCount(client));
				Integer totalIssues = openIssues + closedIssues;
				Double ratioOfClosedIssues = Double.valueOf(closedIssues) / Double.valueOf(totalIssues);

				writeCsv(String.valueOf(stars), String.valueOf(forks), String.valueOf(contributors),
						String.valueOf(openIssues), String.valueOf(closedIssues),
						String.format("%.2f", ratioOfClosedIssues));
			} else {
				handleRateLimit(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return;
	}

	/**
	 * リクエストを生成する
	 * 
	 * @param url
	 * @return HTTPリクエスト
	 */
	public HttpRequest createRequest(String url) {
		return HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Accept", "application/vnd.github.v3+json")
				.header("Authorization", "token " + this.token)
				.build();
	}

	/**
	 * GitHub APIのURLを取得する
	 * 
	 * @return GitHub APIのURL
	 */
	public String getApiUrl() {
		return this.apiUrl;
	}

	/**
	 * クローズされたIssue数を取得する
	 * 
	 * @param client クライアント
	 * @return クローズされたIssue数
	 */
	private Integer getClosedIssuesCount(HttpClient client) {
		try {
			String url = getApiUrl() + "/issues?state=closed&per_page=100";
			Integer totalClosedIssues = 0;
			Integer page = 1;

			while (true) {
				System.out.print(page + "* ");
				HttpRequest request = createRequest(url + "&page=" + page);

				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

				if (response.statusCode() == 200) {
					JSONArray closedIssues = new JSONArray(response.body());
					totalClosedIssues += closedIssues.length();
					if (closedIssues.length() < 100)
						break;
					page++;
					TimeUnit.SECONDS.sleep(1);
				} else {
					handleRateLimit(response);
					break;
				}
			}
			return totalClosedIssues;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * 貢献者数を取得する
	 * 
	 * @param client クライアント
	 * @return 貢献者数
	 */
	private Integer getContributorsCount(HttpClient client) {
		try {
			String url = getApiUrl() + "/contributors?per_page=100";
			Integer totalContributors = 0;
			Integer page = 1;

			while (true) {
				System.out.print(page + " ");
				HttpRequest request = createRequest(url + "&page=" + page);

				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

				if (response.statusCode() == 200) {
					JSONArray contributors = new JSONArray(response.body());
					totalContributors += contributors.length();
					if (contributors.length() < 100)
						break;
					page++;
					TimeUnit.SECONDS.sleep(1);
				} else {
					handleRateLimit(response);
					break;
				}
			}
			return totalContributors;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * リポジトリのオーナー名を取得する
	 * 
	 * @return リポジトリのオーナー名
	 */
	public String getOwner() {
		return this.owner;
	}

	/**
	 * リポジトリ名を取得する
	 * 
	 * @return リポジトリ名
	 */
	public String getRepositoryName() {
		return this.repositoryName;
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
	 * トークンを取得する
	 * 
	 * @return GitHub APIのトークン
	 */
	public String getToken() {
		try {
			String token;
			File file = new File("./src/main/resource/GitHub_API_token");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			token = reader.readLine();
			reader.close();

			return token;
		} catch (IOException e) {
			e.printStackTrace();

			return null;
		}
	}

	/**
	 * レート制限を対処する
	 * 
	 * @param response HTTPレスポンス
	 */
	public void handleRateLimit(HttpResponse<String> response) {
		try {
			if (response.statusCode() == 403 || response.statusCode() == 429) {
				System.out.println("Rate limit exceeded. Waiting for reset...");
				TimeUnit.MINUTES.sleep(1);
			} else {
				System.out.println("Request failed. HTTP Status: " + response.statusCode());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return;
	}

	/**
	 * CSVファイルに書き込む
	 * 
	 * @param stars               スター数
	 * @param forks               フォーク数
	 * @param contributors        貢献者数
	 * @param ratioOfClosedIssues Isuue解決の割合
	 */
	public void writeCsv(String stars, String forks, String contributors, String openIssues, String closedIssues,
			String ratioOfClosedIssues) {
		Long secondsTimeDifference = new CommitTimeDifference(getRepositoryUrl()).secondTimeDifference();
		String[] csvHeader = { "Repository Name", "Stars", "Forks", "Contributors", "OpenIssues", "ClosedIssues",
				"RatioOfClosedIssues", "DifferenceBetweenLastCommitTimeAndCurrentTime" };
		String[] csvData = { getOwner() + "/" + getRepositoryName(), stars, forks, contributors, openIssues,
				closedIssues, ratioOfClosedIssues, secondsTimeDifference.toString() };
		String directoryFilePathString = "./GitHubData/" + this.repositoryController.ownerAndRepositoryName();
		String outputFile = "/githubData.csv";
		File directoryFilePath = new File(directoryFilePathString);
		if (directoryFilePath.exists() == false) {
			directoryFilePath.mkdirs();
		}
		try (CSVWriter writer = new CSVWriter(new FileWriter(directoryFilePath + outputFile))) {
			writer.writeNext(csvHeader);
			writer.writeNext(csvData);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}
}
