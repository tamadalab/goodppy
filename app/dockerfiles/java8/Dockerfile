FROM gradle:jdk8

# 必要なパッケージをインストール
RUN apt-get update && apt-get install -y \
    wget \
    unzip

# Android SDKをインストールするディレクトリを作成
ENV ANDROID_HOME /opt/android-sdk
RUN mkdir -p $ANDROID_HOME

# Android SDKコマンドラインツールをダウンロードして解凍
RUN wget -q https://dl.google.com/android/repository/commandlinetools-linux-8092744_latest.zip -O sdk-tools.zip && \
    mkdir -p $ANDROID_HOME/cmdline-tools && \
    unzip sdk-tools.zip -d $ANDROID_HOME/cmdline-tools && \
    rm sdk-tools.zip && \
    mv $ANDROID_HOME/cmdline-tools/cmdline-tools $ANDROID_HOME/cmdline-tools/latest

# PATHにAndroid SDKのコマンドラインツールを追加
ENV PATH "$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"

# 必要なSDKパッケージをインストール
RUN yes | sdkmanager --licenses && \
    sdkmanager "platform-tools" "platforms;android-30" "build-tools;30.0.3"

WORKDIR /app

COPY ./ /app/

CMD ["./gradlew", "build"]
