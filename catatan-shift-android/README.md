# Catatan Shift (Android)

Aplikasi Android untuk mencatat absen per shift dan lembur, dengan alarm sistem yang tetap berbunyi walau HP terkunci.

- `web/catatan-shift.html` adalah tampilan aplikasi (sama dengan versi web).
- `app/` adalah pembungkus Android: WebView + alarm (`AlarmManager.setAlarmClock`) + notifikasi alarm yang berbunyi berulang.
- APK dibuild otomatis oleh GitHub Actions (`.github/workflows/build-catatan-shift.yml`) dan diunggah ke rilis `catatan-shift-latest`.
