# STEP 1: ビルド環境の準備 (MavenとJava 21が使える環境)
FROM maven:3.9-eclipse-temurin-21-jammy AS build

# STEP 2: アプリケーションのコードをコンテナにコピー
WORKDIR /app
COPY . .

# STEP 3: Mavenでアプリケーションをビルド (テストはスキップ)
RUN mvn clean package -DskipTests

# STEP 4: 実行環境の準備 (軽量なJava 21実行環境)
FROM eclipse-temurin:21-jre-jammy

# STEP 5: ビルド成果物(.jarファイル)を実行用イメージにコピー
WORKDIR /app
COPY --from=build /app/target/rakusakubai-0.0.1-SNAPSHOT.jar ./app.jar

# STEP 6: アプリケーションの起動コマンドを指定
ENTRYPOINT ["java", "-jar", "app.jar"]