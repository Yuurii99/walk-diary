# ポートフォリオについて
今までモバイルアプリ開発は行った事が無かった為、学習用途・ポートフォリオ用としてこちらのアプリを作成しました。  
TODOアプリに毛が生えた程度ですが、モバイルアプリ特有の挙動や作法を学習しながらのため開発期間は10日程です。  
APKファイルは[こちらから](https://github.com/Yuurii99/walk-diary/releases/tag/release)お願いします。

## 選定ライブラリ
### Jetpack Compose
- 用途: UIフレームワーク
- 選定理由: React同様、コンポーネント指向のUIライブラリで学習コストが低そう、かつGoogle推奨のため

### Google Maps Compose
- 用途: 地図表示・操作・ピン指し
- 選定理由: GoogleMaps SDKをComposeで扱えるGoogle公式ライブラリのため

### Google Play Services Location
- 用途: 位置情報取得
- 選定理由: 現在地情報を取得、マップへのピンに使用するため

### Room DB
- 用途: ローカルDB (SQLite)
- 選定理由: SQLiteの抽象化を行い、生産性の向上、型安全性向上のため

### Kotlin Coroutines / Flow
- 用途: 非同期処理
- 選定理由: 計算リソースをフル活用したいモバイルアプリではマルチスレッドな実装が必要かと思い選定しました

### Coil
- 用途: 非同期画像表示
- 選定理由: Coroutinesとほぼ同様の理由ですが、今後Http requestを併用した画像処理を行う際にまず使用するだろうと思い選定しました

## アプリサンプル
<img height="600" alt="app_sample" src="https://github.com/user-attachments/assets/9c642d0c-c76e-4142-9f16-63fde9e19cb5" /> 
<img height="600" alt="Screenshot_20260421_133221" src="https://github.com/user-attachments/assets/981a7e9e-1c6a-40e3-a719-a7b8b6b22ac1" /> 
<img height="600" alt="Screenshot_20260421_133001" src="https://github.com/user-attachments/assets/2a0fc313-2a10-455b-ab36-96946fdde5c6" />
