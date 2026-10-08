# 🎵 Vmusix - Modern YouTube Music Player

[![Build Status](https://img.shields.io/badge/Build-GitHub%20Actions-brightgreen?logo=github-actions)](.github/workflows/build-release.yml)
[![Platform](https://img.shields.io/badge/Platform-Android-green?logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-purple?logo=kotlin)](https://kotlinlang.org)
[![Developer](https://img.shields.io/badge/Developer-VeruProject-6D28D9)](mailto:verucaadev@gmail.com)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

> **"Listen Without Limits"**  
> Pemutar musik modern berbasis YouTube Music dengan desain premium AMOLED, lirik tersinkronisasi otomatis, pengenalan musik Shazam, Last.fm scrobbler, dan manajemen unduhan offline.

---

## 📖 Deskripsi

**Vmusix** adalah aplikasi pemutar musik Android modern yang dirancang untuk memberikan pengalaman mendengarkan musik terbaik tanpa hambatan. Terinspirasi dari estetika modern Spotify namun hadir dengan identitas unik serba gelap (ramah AMOLED), Vmusix menghubungkan pengguna langsung ke katalog YouTube Music dengan performa ringan, cepat, dan stabil.

Seluruh konfigurasi identitas aplikasi, nama, credit developer, dan skema warna dapat diubah hanya dari **satu file** konfigurasi (`BrandConfig.kt`), serta penggantian ikon cukup dengan menukar satu file gambar pada folder `assets/app_icon.png`.

---

## ✨ Fitur Unggulan

### 1. 🎧 Playback Berkualitas Tinggi (Media3 ExoPlayer)
- Pemutaran musik di latar belakang (*background playback*) dengan `PlaybackService` (`MediaSessionService`).
- Kontrol penuh: Play, Pause, Next, Previous, Shuffle, Repeat, dan Draggable Progress Slider.
- Sticky **Mini Player** elegan di atas bilah navigasi dan **Full Player** imersif dengan latar belakang blur adaptif.

### 2. 🔍 Pencarian Real-time & Cepat
- Pencarian YouTube Music dengan sistem *debounce* otomatis (400ms) untuk menghemat kuota dan memori.
- Filter pencarian instan: Lagu, Artis, Album, dan Playlist.

### 3. 📜 Sinkronisasi Lirik Otomatis (Synced Lyrics)
- Sistem kaskade multi-provider otomatis:
  1. **YouTube Music Timed Lyrics**
  2. **LRCLIB** (Lirik sinkronisasi & teks biasa)
  3. **Better Lyrics**
  4. **KuGou Lyrics**
  5. **Paxsenix Lyrics**
- Auto-sync, auto-scroll mengikuti detik lagu, baris aktif bersinar terang, dan klik baris lirik untuk langsung melompat (*seek*) ke bagian lagu tersebut.
- Dukungan *cache lirik lokal* untuk dibaca saat offline.

### 4. ⚡ Custom Minimalist Loader
- Loader animasi melingkar modern tanpa teks, tanpa logo, tanpa angka persentase, dan sangat ringan pada CPU/GPU.

### 5. 📥 Sistem Unduhan Offline (Music/Vmusix)
- Unduh Lagu satuan, Album, atau seluruh Playlist.
- Kontrol unduhan lengkap: **Pause**, **Resume**, **Cancel**, dan **Retry**.
- Disimpan rapi di direktori `Music/Vmusix` dan langsung dapat diputar saat tidak ada koneksi internet.

### 6. 🎙️ Shazam Music Recognition (ShazamKit)
- Kenali lagu yang sedang diputar di sekitar menggunakan mikrofon perangkat.
- Menampilkan judul, musisi, album, dan persentase kecocokan.
- Tombol satu ketukan untuk memutar lagu atau memasukkannya ke playlist favorit.

### 7. 📻 Integrasi Last.fm Scrobbler
- Scrobbling otomatis setiap kali lagu selesai didengarkan.
- Kirim status *Now Playing* secara langsung.
- Riwayat scrobble lokal dan daftar lagu terpopuler.

### 8. 🧹 Manajemen Cache Cerdas
- Tampilan detail pemakaian memori untuk gambar, stream audio, dan lirik.
- Tombol satu ketukan untuk membersihkan cache tanpa menghapus lagu yang sudah diunduh.

---

## 📸 Screenshot Placeholder

```
+--------------------+   +--------------------+   +--------------------+
|     [ HOME ]       |   |     [ SEARCH ]     |   |   [ FULL PLAYER ]  |
|                    |   |                    |   |                    |
|  * Banner Rekomend |   |  [Q Cari lagu...]  |   |    +----------+    |
|  * Quick Picks     |   |                    |   |    |  COVER   |    |
|  * Recently Played |   |  * Hasil Lagu      |   |    +----------+    |
|  * Trending Songs  |   |  * Artis Terkait   |   |  Starboy - Weeknd  |
|  * New Releases    |   |  * Album Populer   |   |  [===O=========]   |
|                    |   |                    |   |  |<  <<  ||  >> >| |
| [Mini Player Bar]  |   | [Mini Player Bar]  |   |  Lirik / Antrean   |
+--------------------+   +--------------------+   +--------------------+
```

---

## 🛠️ Teknologi & Library

| Kategori | Teknologi | Deskripsi |
|---|---|---|
| **Language** | Kotlin 2.2.10 | Bahasa pemrograman modern berorientasi tipe aman |
| **UI Framework** | Jetpack Compose + Material 3 | Desain deklaratif modern ramah layar sentuh |
| **Audio Engine** | AndroidX Media3 ExoPlayer | Pemutar audio standar industri Google |
| **Database** | Room Database | Penyimpanan lokal untuk lagu favorit, playlist, dan lirik |
| **Preferences** | DataStore Preferences | Penyimpanan pengaturan aplikasi terenkripsi & asinkron |
| **Network** | OkHttp & Retrofit | HTTP client cepat dengan connection pooling |
| **Image Loading** | Coil Compose | Pemuatan dan *caching* cover album yang optimal |
| **Async** | Kotlin Coroutines & StateFlow | Pemrosesan latar belakang yang reaktif |

---

## 📂 Struktur Folder Proyek

```
Vmusix/
├── .github/
│   └── workflows/
│       └── build-release.yml       # Skrip CI/CD GitHub Actions untuk build APK otomatis
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── assets/
│   │   │   │   └── app_icon.png    # Ikon aplikasi (ganti file ini untuk ubah ikon)
│   │   │   ├── java/com/veruproject/vmusix/
│   │   │   │   ├── config/
│   │   │   │   │   └── BrandConfig.kt      # Single source of truth untuk identitas & warna
│   │   │   │   ├── data/
│   │   │   │   │   ├── cache/              # CacheManager & kalkulasi pemakaian disk
│   │   │   │   │   ├── download/           # DownloadManager (Pause, Resume, Retry)
│   │   │   │   │   ├── local/db/           # Room Database, Entities, & DAOs
│   │   │   │   │   ├── local/prefs/        # DataStore UserPreferences
│   │   │   │   │   └── remote/             # InnerTube API, Lyrics Multi-Provider, Shazam, Last.fm
│   │   │   │   ├── domain/model/           # Domain models: Track, Album, Artist, Lyrics
│   │   │   │   ├── playback/               # Media3 ExoPlayer Controller
│   │   │   │   ├── service/                # PlaybackService (Foreground MediaSession)
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/         # Custom VmusixLoader, MiniPlayer, FullPlayer, SyncedLyrics
│   │   │   │   │   ├── screens/            # Home, Search, More, Shazam, Downloads, Liked, Settings
│   │   │   │   │   ├── theme/              # Color, Type, dan Theme
│   │   │   │   │   └── viewmodel/          # MainViewModel
│   │   │   │   ├── MainActivity.kt         # Entry point & navigasi utama 3-menu
│   │   │   │   └── VmusixApplication.kt    # Inisialisasi Coil memory & disk cache
│   │   │   └── res/                        # Resources Android (drawables, values, mipmap)
│   │   └── test/                           # Unit tests & Robolectric JVM tests
│   └── build.gradle.kts                    # Konfigurasi build Gradle modul app
├── gradle/
│   └── libs.versions.toml                  # Version Catalog seluruh dependency
├── metadata.json                           # Metadata platform AI Studio
└── README.md                               # Panduan lengkap proyek
```

---

## 🚀 Panduan Memulai untuk Pemula

### 1. Cara Clone Repositori
Buka terminal atau command prompt di komputer Anda, lalu jalankan:
```bash
git clone https://github.com/veruproject/vmusix.git
cd vmusix
```

### 2. Cara Membuka Project di Android Studio
1. Buka aplikasi **Android Studio** (disarankan versi Ladybug / Meerkat atau yang lebih baru).
2. Pilih menu **File > Open...**.
3. Navigasikan ke folder hasil clone `vmusix`, lalu klik **OK**.
4. Tunggu beberapa saat hingga proses **Gradle Sync** selesai secara otomatis.

### 3. Cara Menjalankan Aplikasi
1. Sambungkan perangkat Android menggunakan kabel USB (pastikan *USB Debugging* aktif), atau jalankan Android Virtual Device (Emulator).
2. Klik tombol **Run** (ikon segitiga hijau `▶`) di toolbar atas Android Studio, atau gunakan pintasan keyboard `Shift + F10`.

---

## 📦 Cara Build APK

### Melalui Terminal Komputer Lokal:
Jalankan perintah berikut di direktori root proyek:
```bash
gradle assembleDebug
```
File APK hasil kompilasi akan berada di folder:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🤖 Cara Build Melalui GitHub Actions

Vmusix sudah dilengkapi dengan workflow CI/CD bawaan pada file `.github/workflows/build-release.yml`.

### Langkah-langkah:
1. Push kode Anda ke repository GitHub di cabang `main`.
2. Buka tab **Actions** di repositori GitHub Anda.
3. Anda akan melihat workflow **"Build Release APK - Vmusix"** berjalan otomatis.
4. Anda juga dapat memicu build manual dengan mengklik tombol **"Run workflow"**.
5. Setelah status centang hijau (berhasil), gulir ke bagian paling bawah di halaman workflow tersebut pada bagian **Artifacts**.
6. Klik **`Vmusix-APK`** untuk langsung mengunduh file `.apk` yang siap dipasang di ponsel Android!

---

## 🎨 Panduan Kustomisasi Branding (Sangat Mudah!)

### 1. Cara Mengganti Nama & Tagline Aplikasi
Buka file:
`app/src/main/java/com/veruproject/vmusix/config/BrandConfig.kt`
Ubah baris berikut:
```kotlin
const val APP_NAME = "NamaAplikasiAnda"
const val DEVELOPER_NAME = "NamaDeveloperAnda"
const val TAGLINE = "Tagline Keren Anda"
```
Juga sesuaikan nama di `app/src/main/res/values/strings.xml`:
```xml
<string name="app_name">NamaAplikasiAnda</string>
```

### 2. Cara Mengganti Ikon Aplikasi
Cukup ganti file gambar di:
`app/src/main/assets/app_icon.png`
dengan gambar logo PNG Anda berukuran 512x512 piksel. Aplikasi akan otomatis memuat gambar tersebut sebagai ikon baru!

### 3. Cara Mengubah Warna Tema
Buka file `BrandConfig.kt` dan ubah kode hex warnanya sesuai keinginan:
```kotlin
val PRIMARY_COLOR = Color(0xFF6D28D9)   // Warna Utama
val SECONDARY_COLOR = Color(0xFF2563EB) // Warna Sekunder
val ACCENT_COLOR = Color(0xFFA855F7)    // Warna Aksen
```

### 4. Cara Menambahkan Fitur Baru
Arsitektur Vmusix dirancang modular:
1. Tambahkan model data di `domain/model/`.
2. Tambahkan pemrosesan data / API di `data/remote/` atau `data/local/`.
3. Buat Composable screen baru di `ui/screens/`.
4. Hubungkan fungsinya ke `MainViewModel.kt`.

---

## ❓ Troubleshooting (Penyelesaian Masalah Umum)

| Masalah | Penyebab | Solusi |
|---|---|---|
| **Gradle Sync Error** | Versi JDK di Android Studio belum disetel ke Java 17 | Buka `Settings > Build, Execution, Deployment > Build Tools > Gradle`, ubah **Gradle JDK** ke versi 17. |
| **Lirik Tidak Muncul** | Lagu tidak memiliki lirik di provider pertama | Vmusix akan otomatis berpindah ke LRCLIB, BetterLyrics, KuGou, hingga Paxsenix. Jika tetap kosong, pastikan koneksi internet aktif. |
| **Pencarian Tidak Berfungsi** | Pembatasan sementara koneksi IP | Gunakan fitur pencarian lagi setelah beberapa detik atau pilih saran pencarian populer yang tersedia. |
| **GitHub Actions Gagal** | Izin eksekusi Gradle | Workflow GitHub Actions Vmusix sudah otomatis menangani izin dan setup SDK mandiri. Cek log error di tab Actions jika ada dependensi jaringan terputus. |
| **Suara Tidak Terdengar** | Volume perangkat mati / stream audio terhalang | Periksa slider volume perangkat dan pastikan izin koneksi internet aplikasi aktif. |

---

## 💬 FAQ (Pertanyaan yang Sering Diajukan)

**Q: Apakah Vmusix memerlukan login akun Google / YouTube?**  
A: Tidak! Vmusix dapat langsung digunakan untuk memutar musik, mencari lagu, dan membaca lirik tanpa harus login.

**Q: Apakah lagu yang diunduh bisa diputar tanpa kuota internet?**  
A: Ya, seluruh lagu yang diunduh disimpan di penyimpanan lokal `Music/Vmusix` dan dapat diputar kapan saja secara offline.

**Q: Apakah aplikasi ini ramah baterai dan layar AMOLED?**  
A: Sangat ramah. Latar belakang aplikasi menggunakan warna hitam murni (`#09090B`) yang mematikan piksel pada panel AMOLED sehingga sangat hemat daya baterai.

---

## 🏆 Credit & Pengembang

Dikembangkan dengan sepenuh hati oleh:  
**VeruProject**  
- Email: [verucaadev@gmail.com](mailto:verucaadev@gmail.com)  
- Proyek: **Vmusix - Listen Without Limits**

---

## 📄 Lisensi

Proyek ini dilisensikan di bawah [MIT License](LICENSE). Bebas digunakan, dipelajari, dan dikembangkan lebih lanjut.
