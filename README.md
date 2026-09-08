<!-- Back to top anchor -->
<a id="readme-top"></a>

<!-- PROJECT SHIELDS -->
[![Kotlin][kotlin-shield]][kotlin-url]
[![Jetpack Compose][compose-shield]][compose-url]
[![Supabase][supabase-shield]][supabase-url]
[![Min SDK][minsdk-shield]][minsdk-url]
[![Version][version-shield]][version-url]

<br />
<div align="center">

  <!-- ============================================================ -->
  <!-- LOGO: Upload file logo ke GitHub lalu ganti URL di bawah ini -->
  <!-- Path : presentasi/logo.png                                   -->
  <!-- Ukuran yang disarankan: 120×120 px                           -->
  <!-- ============================================================ -->
  <a href="https://github.com/alHerys/SwaraBox">
    <img width="192" height="192" alt="ic_launcher" src="https://github.com/user-attachments/assets/89326cae-0f94-4992-b058-804c3aeb5731" />
  </a>
  <br>
  <img width="800" alt="ChatGPT Image Sep 9, 2026, 12_43_15 AM" src="https://github.com/user-attachments/assets/83f7deb1-8fa4-467d-90fa-6b1dec3d210b" />


  <h3 align="center">Swaraloka
</h3>

  <p align="center">
    Platform streaming musik Android berbasis Supabase — upload, kelola, dan nikmati lagu kapan saja.
    <br />
    <a href="#arsitektur">Lihat Arsitektur</a>
    &middot;
    <a href="#fitur">Fitur</a>
    &middot;
    <a href="#alur-data">Alur Data</a>
  </p>
</div>

---

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Daftar Isi</summary>
  <ol>
    <li><a href="#tentang-proyek">Tentang Proyek</a></li>
    <li><a href="#teknologi">Teknologi</a></li>
    <li><a href="#arsitektur">Arsitektur</a></li>
    <li><a href="#fitur">Fitur</a></li>
    <li><a href="#alur-data">Alur Data</a></li>
    <li><a href="#struktur-database">Struktur Database</a></li>
    <li><a href="#getting-started">Getting Started</a></li>
    <li><a href="#kontak">Kontak</a></li>
  </ol>
</details>

---

<!-- ABOUT THE PROJECT -->
## Tentang Proyek

**Swaraloka** adalah aplikasi Android untuk streaming dan manajemen musik pribadi. Pengguna dapat mendaftar akun, mengunggah lagu milik sendiri (audio + thumbnail), memutarnya secara langsung, serta mengelola profil dan koleksi lagu — semua tersinkronisasi ke cloud melalui **Supabase**.

Aplikasi ini dibangun sebagai proyek akademik dengan menerapkan:
- Pola arsitektur **MVVM (Model-View-ViewModel)**
- **Reactive state management** menggunakan Kotlin `StateFlow`
- **Backend-as-a-Service** dengan Supabase (Auth, PostgreSQL, Storage)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

<!-- TECH STACK -->
## Teknologi

| Kategori | Library / Platform |
|---|---|
| Bahasa | Kotlin |
| UI Framework | Jetpack Compose + Material 3 |
| Navigasi | Navigation Compose (Type-Safe Routes) |
| ViewModel | AndroidX ViewModel + `collectAsStateWithLifecycle` |
| Backend | Supabase (Auth, PostgREST, Storage) |
| HTTP Client | Ktor Android |
| Audio Player | ExoPlayer (Media3) |
| Image Loading | Coil Compose |
| Animasi | Lottie Compose |
| Serialisasi | Kotlinx Serialization |
| Splash Screen | AndroidX Core SplashScreen |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 |

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

<!-- ARCHITECTURE -->
## Arsitektur

SwaraBox mengikuti pola **MVVM** yang dibagi ke tiga lapisan utama.

<div align="center">
  <img width="1236" height="868" alt="struktur" src="https://github.com/user-attachments/assets/d59dda27-b959-41e6-87d9-9f6dce9dd81c" />
</div>


### Lapisan UI
Seluruh UI dibangun dengan **Jetpack Compose**. Setiap screen menerima state dari ViewModel melalui `collectAsStateWithLifecycle()` dan mengirimkan event balik ke ViewModel — tidak ada logika bisnis di dalam Composable.

### Lapisan ViewModel
Setiap fitur utama memiliki ViewModel-nya sendiri yang meng-expose `StateFlow`. State modelling menggunakan sealed class sehingga setiap kondisi (Idle, Loading, Success, Error) terdefinisi secara eksplisit.

| ViewModel | Scope | StateFlow |
|---|---|---|
| `AuthViewModel` | App-level | `AuthUiState`, `AuthCheckState` |
| `SongViewModel` | App-level | `SongUiState`, `songs` |
| `ProfileViewModel` | App-level | `ProfileUiState`, `mySongs` |
| `PlayerViewModel` | App-level | `PlayerUiState`, `currentSong` |
| `UploadViewModel` | Screen-local | `UploadUiState` |
| `EditSongViewModel` | Screen-local | `EditSongUiState` |
| `EditProfileViewModel` | Screen-local | `EditProfileUiState` |

### Lapisan Data (Repository)
Repository menjadi satu-satunya sumber kebenaran data. `SongRepository` menerapkan **in-memory cache** (`cachedSongs`) untuk mengurangi request yang tidak perlu ke Supabase.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

<!-- FEATURES -->
## Fitur

### Autentikasi
- Registrasi akun dua langkah — input nama dulu, lalu email & password
- Login dan logout dengan Supabase Auth (email/password)
- Sesi otomatis dipulihkan saat aplikasi dibuka kembali (`SessionStatus` Flow)
- Pesan error yang ramah pengguna (`toUserFriendlyMessage()`)

### Beranda & Daftar Lagu
- Menampilkan semua lagu aktif dari seluruh pengguna (via RPC `fetch_all_active_song`)
- Urutan lagu diacak setiap kali dibuka
- **Featured Card** — satu lagu unggulan ditampilkan di bagian atas
- Pull-to-refresh untuk memperbarui daftar

### Pemutar Musik
- Streaming audio langsung dari URL Supabase Storage menggunakan **ExoPlayer**
- Kontrol lengkap: play, pause, seek, skip
- **MiniPlayer** menetap di bagian bawah layar saat berpindah tab
- Tombol edit hanya muncul jika lagu milik pengguna yang sedang login

### Upload Lagu
- Pilih file audio dari perangkat (MP3) dan gambar thumbnail
- Preview audio sebelum upload dengan kontrol play/pause/seek lokal
- Thumbnail dikompres otomatis (maks 400px) sebelum diunggah
- File tersimpan di Supabase Storage bucket `lagu` dan `gambar`

### Kelola Lagu
- Edit judul, audio, atau thumbnail secara independen (field yang tidak diubah tidak di-upload ulang)
- Hapus lagu dengan **soft delete** (`is_active = false`) — file di storage turut dihapus
- Seluruh perubahan otomatis tercatat di tabel `log_song` melalui database trigger

### Profil
- Lihat profil (nama, email, avatar) beserta koleksi lagu milik sendiri
- Edit nama dan foto profil (avatar dikompres maks 256px)
- Pull-to-refresh pada daftar lagu

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

<!-- DATA FLOW -->
## Alur Data

Diagram berikut menggambarkan alur data dari UI ke Supabase untuk setiap fitur utama.

### Register

<div align="center">
<img width="1236" height="996" alt="1  register" src="https://github.com/user-attachments/assets/407dc4ef-1884-423e-9980-aa45468f71d6" />
</div>

### Login
<div align="center">
<img width="1060" height="1126" alt="2  login" src="https://github.com/user-attachments/assets/c828789a-cd09-4010-b52a-795a7edd5ea0" />
</div>

### Fetch Lagu

<div align="center">
<img width="1204" height="965" alt="5  fetch lagu" src="https://github.com/user-attachments/assets/8ba84e6d-294b-4c85-b224-691323e57c00" />
</div>

### Upload Lagu

<div align="center">
<img width="1076" height="852" alt="4  upload" src="https://github.com/user-attachments/assets/a8a48167-10a7-4b81-b9be-48446b681b20" />
</div>

### Edit & Hapus Lagu

<div align="center">
<img width="884" height="852" alt="6  edit hapus lagu" src="https://github.com/user-attachments/assets/b67614fa-97ae-4dad-a9bb-45fb71636c35" />
</div>

### Profil & Edit Profil

<div align="center">
<img width="1124" height="1124" alt="3  fetch edit profile" src="https://github.com/user-attachments/assets/3e3a87f4-1f2d-4486-baa3-e29525c27566" />
</div>

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

<!-- DATABASE -->
## Struktur Database

SwaraBox menggunakan **Supabase PostgreSQL** dengan skema berikut.

### Tabel Utama

| Tabel             | Deskripsi                                                     |
|-------------------|---------------------------------------------------------------|
| `auth.users`      | Tabel autentikasi bawaan Supabase                             |
| `public.user`     | Data profil pengguna (disinkronisasi via trigger)             |
| `public.song`     | Data lagu (judul, url audio, thumbnail, durasi, status aktif) |
| `public.log_song` | Log setiap perubahan lagu (created, editing, deleting)        |
| `public.log_user` | Log perubahan data pengguna                                   |

### Database Triggers

| Trigger            | Event                         | Aksi                                                |
|--------------------|-------------------------------|-----------------------------------------------------|
| `on_register_user` | `INSERT` pada `auth.users`    | Insert profil ke `public.user`                      |
| `on_song_updated`  | `INSERT / UPDATE` pada `song` | Insert baris ke `log_song` dengan `log_type` sesuai |

### Storage Buckets

| Bucket | Konten | Penamaan File |
|---|---|---|
| `lagu` | File audio MP3 | `audio_{userId}_{uuid}.mp3` |
| `gambar` | Thumbnail lagu & avatar profil | `thumb_{userId}_{uuid}.png` / `avatar_{userId}.png` |

### RPC Function

| Fungsi | Deskripsi |
|---|---|
| `fetch_all_active_song` | Mengambil semua lagu aktif beserta nama artis (JOIN antara `song` dan `user`) |

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

<!-- GETTING STARTED -->
## Getting Started

### Instalasi

1. Clone repositori ini
   ```sh
   git clone https://github.com/alHerys/SwaraBox.git
   ```

2. Buka project di **Android Studio**

3. Konfigurasi koneksi Supabase di `SupabaseClientProvider.kt`
   ```kotlin
   val client = createSupabaseClient(
       supabaseUrl = "YOUR_SUPABASE_URL",
       supabaseKey = "YOUR_SUPABASE_ANON_KEY"
   ) {
       install(Auth)
       install(Postgrest)
       install(Storage)
   }
   ```

4. Pastikan skema database sudah terbuat di Supabase — buat tabel `song`, `user`, `log_song`, `log_user` serta triggers dan storage buckets yang dibutuhkan

5. Jalankan aplikasi di emulator atau perangkat fisik via **Run > Run 'app'**

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

<!-- CONTACT -->
## Kontak

**Alvianto Hery Sarborn**

Project Link: [https://github.com/alHerys/Swaraloka](https://github.com/alHerys/Swaraloka)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

<!-- MARKDOWN LINKS & IMAGES -->
[kotlin-shield]: https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white
[kotlin-url]: https://kotlinlang.org/
[compose-shield]: https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white
[compose-url]: https://developer.android.com/compose
[supabase-shield]: https://img.shields.io/badge/Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white
[supabase-url]: https://supabase.com/
[minsdk-shield]: https://img.shields.io/badge/Min%20SDK-24-brightgreen?style=for-the-badge&logo=android&logoColor=white
[minsdk-url]: https://developer.android.com/about/versions/nougat
[version-shield]: https://img.shields.io/badge/Version-1.0-blue?style=for-the-badge
[version-url]: https://github.com/alHerys/SwaraBox/releases
