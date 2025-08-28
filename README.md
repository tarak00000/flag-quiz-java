# 🏴 国旗クイズゲーム (Java版)

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green.svg)](https://spring.io/projects/spring-boot)
[![Gemini](https://img.shields.io/badge/Gemini-2.0%20Flash-purple.svg)](https://ai.google.dev/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📖 プロジェクト概要

Spring Boot と Gemini API を使用した国旗クイズWebアプリケーションです。国旗を見て国名を当てるインタラクティブなゲームを楽しめます。

**🎯 ポートフォリオとして公開しているプロジェクトです**

## ✨ 主な特徴

- 🌍 **Spring Boot 3.2+** によるエンタープライズレベルの構成
- 🤖 **Gemini AI** による質問回答システム
- 💡 **3種類のヒント** （主食・面積・言語）
- 🎨 **モダンデザイン** （レスポンシブ対応）
- ⚡ **高性能** （Java 17+ 最適化）
- 🚀 **Renderでの本番デプロイ済み**

## 🌐 ライブデモ

**🔗 デモURL**: [https://flag-quiz-java.onrender.com](https://flag-quiz-java.onrender.com)

実際に動作するアプリケーションをお試しください！

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

### インフラ・デプロイ
- **Render** - クラウドホスティング
- **Docker** - コンテナ化
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
├── Dockerfile                             # Docker設定
├── render.yaml                            # Render設定
├── pom.xml                                # Maven設定
├── README.md                              # このファイル
└── LICENSE                                # ライセンス
```

## 🚀 ローカル開発環境での実行

### 必要環境
- Java 17以上
- Maven 3.8以上
- Gemini API キー

### セットアップ手順

1. **リポジトリをクローン**
```bash
git clone https://github.com/yourusername/flag-quiz-java.git
cd flag-quiz-java
```

2. **Gemini API キーの設定**
`.env` ファイルにあなたのGemini API キーを設定してください：

```bash
# .env ファイルを作成・編集
GEMINI_API_KEY=your-actual-gemini-api-key-here
```

3. **アプリケーションの実行**
```bash
# Maven でアプリケーションを実行
mvn spring-boot:run
```

4. **ブラウザでアクセス**
```
http://localhost:8080
```

## 📝 ライセンス

このプロジェクトはMITライセンスの下で公開されています。詳細は [LICENSE](LICENSE) ファイルをご覧ください。

## 🙏 謝辞

- [Google Gemini AI](https://ai.google.dev/) - AI機能
- [flagcdn.com](https://flagcdn.com/) - 国旗画像データ
- [Spring Boot](https://spring.io/projects/spring-boot) - Webフレームワーク
- [Render](https://render.com/) - クラウドホスティング

## 📞 お問い合わせ

質問やフィードバックがある場合は、以下までお気軽にご連絡ください：

- GitHub Issues: [Issues Page](https://github.com/yourusername/flag-quiz-java/issues)

---

⭐ このプロジェクトが気に入ったら、ぜひスターを付けてください！