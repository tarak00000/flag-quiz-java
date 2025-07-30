# Java 17をベースイメージとして使用
FROM openjdk:17-jdk-slim

# 作業ディレクトリを設定
WORKDIR /app

# Mavenをインストール
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# pom.xmlとソースコードをコピー
COPY pom.xml .
COPY src ./src

# アプリケーションをビルド
RUN mvn clean package -DskipTests

# ポートを公開
EXPOSE $PORT

# 環境変数のデフォルト値
ENV SPRING_PROFILES_ACTIVE=production

# アプリケーションを起動
CMD java -Dserver.port=$PORT -jar target/flag-quiz-jv-0.0.1-SNAPSHOT.jar