FROM gradle:jdk21

RUN apt-get update && apt-get install -y \
	wget \
	unzip

ENV ANDROID_HOME /opt/android-sdk
RUN mkdir -p $ANDROID_HOME

RUN wget -q https://dl.google.com/android/repository/commandlinetools-linux-8092744_latest.zip -O sdk-tools.zip && \
	unzip sdk-tools.zip -d $ANDROID_HOME/cmdline-tools && \
	rm sdk-tools.zip

ENV PATH "$ANDROID_HOME/cmdline-tools/bin:$ANDROID_HOME/cmdline-tools/tools/bin:$ANDROID_HOME/platform-tools:$PATH"

RUN yes | sdkmanager --licenses && \
	sdkmanager "platform-tools" "platforms;android-30" "build-tools;30.0.3"

WORKDIR /app

COPY ./ /app/

CMD ["./gradlew", "build"]
