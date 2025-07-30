# 🏴 国旗クイズゲーム (Java版)

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green.svg)](https://spring.io/projects/spring-boot)
[![Gemini](https://img.shields.io/badge/Gemini-2.0%20Flash-purple.svg)](https://ai.google.dev/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Spring Boot と Gemini API を使用した国旗クイズWebアプリケーションです。国旗を見て国名を当てるインタラクティブなゲームを楽しめます。

## ✨ 特徴

- 🌍 **Spring Boot** によるエンタープライズレベルの構成
- 🤖 **Gemini AI** による質問回答システム
- 💡 **3種類のヒント** （主食・面積・言語）
- 🎨 **モダンデザイン** （レスポンシブ対応）
- ⚡ **高性能** （Java 17+ 最適化）
- 🔧 **設定可能** （application.properties）

## 🚀 クイックスタート

### 必要環境
- Java 17以上
- Maven 3.8以上
- Gemini API キー

### 1. リポジトリをクローン
```bash
git clone https://github.com/yourusername/flag-quiz-java.git
cd flag-quiz-java
```

### 2. Gemini API キーの設定
`.env` ファイルにあなたのGemini API キーを設定してください：

```bash
# .env ファイルを作成・編集
GEMINI_API_KEY=your-actual-gemini-api-key-here
```

### 3. アプリケーションの実行
```bash
# Maven でアプリケーションを実行
mvn spring-boot:run
```

### 4. ブラウザでアクセス
```
http://localhost:8080
```

## 🌐 デプロイ

### Renderデプロイ

1. **Renderアカウント作成**: [render.com](https://render.com)でアカウント作成

2. **新しいWebサービス作成**:
   - "New" → "Web Service"
   - GitHubリポジトリを接続
   - 以下の設定を入力：

3. **Render設定**:
   ```
   Name: flag-quiz-java
   Environment: Java
   Build Command: mvn clean package -DskipTests
   Start Command: java -Dserver.port=$PORT -jar target/flag-quiz-jv-0.0.1-SNAPSHOT.jar
   ```

4. **環境変数設定**:
   ```
   GEMINI_API_KEY = your-gemini-api-key-here
   SPRING_PROFILES_ACTIVE = production
   ```

5. **デプロイ実行**: "Create Web Service"をクリック

### ライブデモ
- 🔗 **デモURL**: https://flag-quiz-java.onrender.com *(デプロイ後に更新)*

```

## 🎮 ゲームルール

### 基本ルール
- 表示された国旗を見て、その国名を日本語で回答
- **2回まで** 回答チャンス
- **10回まで** AI質問可能
- **3種類** のヒント利用可能

### ヒントシステム
- **🍚 主食**: その国の代表的な食べ物
- **📏 面積**: 日本との面積比較
- **🗣️ 言語**: 公用語情報

### 質問システム
- AIに「はい/いいえ」形式で質問可能
- 地理・文化・政治に関する一般的な質問のみ
- 国名を直接聞く質問は無効

## 🛠️ 技術スタック

### バックエンド
- **Spring Boot 3.2+** - Java Webフレームワーク
- **Thymeleaf** - テンプレートエンジン
- **Google Gemini 2.0 Flash** - AI質問回答
- **Maven** - ビルドツール

### フロントエンド
- **HTML5 + Thymeleaf** - マークアップ
- **CSS3** - スタイリング
- **JavaScript** - インタラクション

### API・データ
- **Gemini API** - AI機能
- **flagcdn.com** - 国旗画像データ

## 📁 プロジェクト構造

```
flag-quiz-java/
├── src/
│   └── main/
│       ├── java/com/example/flagquiz/
│       │   ├── FlagQuizApplication.java    # メインアプリケーション
│       │   ├── controller/                 # コントローラー層
│       │   │   ├── GameController.java     # ゲーム制御
│       │   │   └── CustomErrorController.java # エラー処理
│       │   ├── service/                    # サービス層
│       │   │   ├── GameService.java        # ゲームロジック
│       │   │   └── GeminiService.java      # AI連携
│       │   ├── model/                      # データモデル
│       │   │   └── GameState.java          # ゲーム状態
│       │   └── config/                     # 設定クラス
│       │       └── DotEnvConfig.java       # 環境変数設定
│       └── resources/
│           ├── templates/                  # Thymeleaf テンプレート
│           │   ├── index.html              # ホームページ
│           │   └── error.html              # エラーページ
│           ├── static/                     # 静的リソース
│           │   ├── style.css               # スタイルシート
│           │   └── script.js               # JavaScript
│           └── application.properties      # Spring Boot設定
├── .env                                    # 環境変数（要作成）
├── pom.xml                                # Maven設定
├── README.md                              # このファイル
├── LICENSE                                # ライセンス
└── .gitignore                             # Git除外設定
```

## 🎯 使用方法

### 1. ゲーム開始
- ホームページで「新しいゲームを開始」をクリック
- ランダムに選択された国の国旗が表示される

### 2. 情報収集
- **質問**: AIに「はい/いいえ」で答えられる質問を投げかけ
- **ヒント**: 3種類のヒントボタンから情報を取得

### 3. 回答
- 国名を日本語で入力（例：「日本」「アメリカ」）
- 2回まで回答可能

### 4. 結果
- 正解時：おめでとうメッセージ
- 不正解時：正解の表示とゲーム終了

## 🔧 カスタマイズ

### application.properties 設定
```properties
# サーバーポート変更
server.port=8080

# ログレベル設定
logging.level.com.example.flagquiz=DEBUG
```

### デザイン変更
`src/main/resources/static/style.css` を編集してデザインをカスタマイズできます。

## 📝 ライセンス

このプロジェクトはMITライセンスの下で公開されています。詳細は [LICENSE](LICENSE) ファイルをご覧ください。

## 🙏 謝辞

- [Google Gemini AI](https://ai.google.dev/) - AI機能
- [flagcdn.com](https://flagcdn.com/) - 国旗画像データ
- [Spring Boot](https://spring.io/projects/spring-boot) - Webフレームワーク

## 📞 お問い合わせ

質問やフィードバックがある場合は、以下までお気軽にご連絡ください：

- GitHub Issues: [Issues Page](https://github.com/yourusername/flag-quiz-java/issues)

---

⭐ このプロジェクトが気に入ったら、ぜひスターを付けてください！