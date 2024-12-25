# goodppy
![goodppy](images/goodppy_logo.svg "goodppy")

## 概要
Gradleを用いたJavaプロジェクトの生存性を調査する．

## 設定
* dependency-check CLIをインストールする．インストールの方法や詳細については[こちら](http://jeremylong.github.io/DependencyCheck/dependency-check-cli/index.html)から．
* GitHub API Tokenとdependency-check API Keyを取得する．
  * GitHub API TokenはclassicのTokenを取得すること．
  * dependency-check API Keyは[こちら](https://nvd.nist.gov/developers/request-an-api-key)から取得する．詳細については[こちら](https://github.com/jeremylong/DependencyCheck?tab=readme-ov-file#nvd-api-key-highly-recommended)を参照すること．
* シェルの初期化ファイルに，GitHub API Tokenとdependency-check API Keyをそれぞれ以下の変数名で設定する．
  * Accsess Token : `GITHUB_API_TOKEN`
  * API Key : `DEPENDENCY_CHECK_APIKEY`

以下に`.bash_profile`で設定する場合の例を示す．

```bash
### GitHub API Token ###
export GITHUB_API_TOKEN=your_api_token

### dependency-check API key ###
export DEPENDENCY_CHECK_APIKEY=your_api_key
```

* `repositories.txt`に調査を行いたいGitHubプロジェクトのURLを設定する．URLはhttpsのURLを用いる．プロジェクトは１行毎に設定すること．

以下に設定例を示す．
```text
https://github.com/owner1/project1.git
https://github.com/owner2/project2.git
https://github.com/owner3/project3.git
```

## 使用方法
以下のコマンドで実行する．
```bash
$ ./gradlew run
```
